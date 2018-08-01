/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;


import java.util.ArrayList;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTLocalVariableDeclaration;
import de.monticore.java.javadsl._ast.ASTVariableDeclarator;
import de.monticore.java.symboltable.JavaSymbolFactory;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.JTypeSymbolsHelper;
import de.monticore.types.JTypeSymbolsHelper.JTypeReferenceFactory;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.ASTWildcardType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTComponentHead;
import montiarc._ast.ASTConnector;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTImportStatementLOCAL;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcAutoConnect;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTState;
import montiarc._ast.ASTStateDeclaration;
import montiarc._ast.ASTStereoValue;
import montiarc._ast.ASTSubComponent;
import montiarc._ast.ASTSubComponentInstance;
import montiarc._ast.ASTTransition;
import montiarc._ast.ASTValuation;
import montiarc._ast.ASTValueInitialization;
import montiarc._ast.ASTVariableDeclaration;
import montiarc.helper.JavaDefaultTypesManager;
import montiarc.helper.Timing;
import montiarc.trafos.AutoConnection;
import montiarc.trafos.SimpleConnectorToQualifiedConnector;
import montiarc.visitor.AssignmentNameCompleter;

/**
 * Visitor that creates the symboltable of a MontiArc AST.
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class MontiArcSymbolTableCreator extends MontiArcSymbolTableCreatorTOP {
  
  protected String compilationUnitPackage = "";
  
  // extra stack of components that is used to determine which components are
  // inner components.
  protected Stack<ComponentSymbol> componentStack = new Stack<>();
  
  protected List<ImportStatement> currentImports = new ArrayList<>();
  
  protected AutoConnection autoConnectionTrafo = new AutoConnection();
  
  protected SimpleConnectorToQualifiedConnector simpleConnectorTrafo = new SimpleConnectorToQualifiedConnector();
  
  private ASTComponent currentComponent;
  
  private final static JavaSymbolFactory javaSymbolFactory = new JavaSymbolFactory();
  
  private final static JTypeReferenceFactory<JavaTypeSymbolReference> javaTypeRefFactory = (name,
      scope, dim) -> new JavaTypeSymbolReference(name, scope, dim);
  
  public MontiArcSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }
  
  public MontiArcSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }
  
  @Override
  public void visit(ASTMACompilationUnit compilationUnit) {
    Log.debug("Building Symboltable for Component: " + compilationUnit.getComponent().getName(),
        MontiArcSymbolTableCreator.class.getSimpleName());
    compilationUnitPackage = Names.getQualifiedName(compilationUnit.getPackageList());
    
    // imports
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTImportStatementLOCAL astImportStatement : compilationUnit
        .getImportStatementLOCALList()) {
      String qualifiedImport = Names.getQualifiedName(astImportStatement.getImportList());
      ImportStatement importStatement = new ImportStatement(qualifiedImport,
          astImportStatement.isStar());
      imports.add(importStatement);
    }
    JavaDefaultTypesManager.addJavaDefaultImports(imports);
    
    ArtifactScope artifactScope = new MontiArcArtifactScope(
        Optional.empty(),
        compilationUnitPackage,
        imports);
    this.currentImports = imports;
    putOnStack(artifactScope);
  }
  
  @Override
  public void endVisit(ASTMACompilationUnit node) {
    // artifact scope
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTPort node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    
    List<String> names = node.getNameList();
    
    if (names.isEmpty()) {
      names.add(StringTransformations.uncapitalize(typeName));
    }
    
    for (String name : names) {
      PortSymbol sym = new PortSymbol(name);
      
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astType);
      JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
          currentScope().get(), dimension);
      
      addTypeArgumentsToTypeSymbol(typeRef, astType);
      
      sym.setTypeReference(typeRef);
      sym.setDirection(node.isIncoming());
      
      // stereotype
      if (node.getStereotypeOpt().isPresent()) {
        for (ASTStereoValue st : node.getStereotypeOpt().get().getValuesList()) {
          sym.addStereotype(st.getName(), st.getValue());
        }
      }
      
      addToScopeAndLinkWithNode(sym, node);
    }
  }
  
  @Override
  public void visit(ASTVariableDeclaration node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    
    List<String> names = node.getNameList();
    
    if (names.isEmpty()) {
      names.add(StringTransformations.uncapitalize(typeName));
    }
    for (String name : names) {
      VariableSymbol sym = new VariableSymbol(name);
      
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astType);
      JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
          currentScope().get(), dimension);
      addTypeArgumentsToTypeSymbol(typeRef, astType);
      
      sym.setTypeReference(typeRef);
      
      addToScopeAndLinkWithNode(sym, node);
    }
  }
  
  private void addTypeArgumentsToTypeSymbol(JTypeReference<? extends JTypeSymbol> typeRef,
      ASTType astType) {
    JTypeSymbolsHelper.addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get(),
        javaTypeRefFactory);
  }
  
  @Override
  public void visit(ASTConnector node) {
    String sourceName = node.getSource().toString();
    
    for (ASTQualifiedName target : node.getTargetsList()) {
      String targetName = target.toString();
      
      ConnectorSymbol sym = new ConnectorSymbol(sourceName, targetName);
      
      // stereotype
      if (node.getStereotypeOpt().isPresent()) {
        for (ASTStereoValue st : node.getStereotypeOpt().get().getValuesList()) {
          sym.addStereotype(st.getName(), st.getValue());
        }
      }
      
      addToScopeAndLinkWithNode(sym, node);
    }
  }
  
  @Override
  public void visit(ASTSubComponent node) {
    String referencedCompName = TypesPrinter
        .printTypeWithoutTypeArgumentsAndDimension(node.getType());
    
    // String refCompPackage = Names.getQualifier(referencedCompName);
    String simpleCompName = Names.getSimpleName(referencedCompName);
    
    ComponentSymbolReference componentTypeReference = new ComponentSymbolReference(
        referencedCompName,
        currentScope().get());
    // actual type arguments
    addTypeArgumentsToComponent(componentTypeReference, node.getType());
    
    // ref.setPackageName(refCompPackage);
    List<ASTExpression> configArgs = new ArrayList<>();
    
    for (ASTExpression arg : node.getArgumentsList()) {
      arg.setEnclosingScope(currentScope().get());
      setEnclosingScopeOfNodes(arg);
      configArgs.add(arg);
    }
    
    // instances
    if (!node.getInstancesList().isEmpty()) {
      // create instances of the referenced components.
      for (ASTSubComponentInstance i : node.getInstancesList()) {
        createInstance(i.getName(), i, componentTypeReference, configArgs);
      }
    }
    else {
      // auto instance because instance name is missing
      createInstance(StringTransformations.uncapitalize(simpleCompName), node,
          componentTypeReference, configArgs);
    }
    
    node.setEnclosingScope(currentScope().get());
  }
  
  /**
   * Creates the instance and adds it to the symTab.
   */
  private void createInstance(String name, ASTNode node,
      ComponentSymbolReference componentTypeReference,
      List<ASTExpression> configArguments) {
    ComponentInstanceSymbol instance = new ComponentInstanceSymbol(name,
        componentTypeReference);
    configArguments.forEach(v -> instance.addConfigArgument(v));
    // create a subscope for the instance
    // setLinkBetweenSymbolAndNode(instance, node);
    addToScopeAndLinkWithNode(instance, node);
    // remove the created instance's scope
    // removeCurrentScope();
  }
  
  @Override
  public void visit(ASTComponent node) {
    this.currentComponent = node;
    String componentName = node.getName();
    
    String componentPackageName = "";
    if (componentStack.isEmpty()) {
      // root component (most outer component of the diagram)
      componentPackageName = compilationUnitPackage;
    }
    else {
      // inner component uses its parents component full name as package
      componentPackageName = componentStack.peek().getFullName();
    }
    ComponentSymbol component = new ComponentSymbol(componentName);
    component.setImports(currentImports);
    component.setPackageName(componentPackageName);
    
    // generic type parameters
    addTypeParametersToComponent(component, node.getHead().getGenericTypeParametersOpt(),
        currentScope().get());
    
    // parameters
    setParametersOfComponent(component, node.getHead());
    
    // stereotype
    if (node.getStereotypeOpt().isPresent()) {
      for (ASTStereoValue st : node.getStereotype().getValuesList()) {
        component.addStereotype(st.getName(), Optional.of(st.getValue()));
      }
    }
    
    // check if this component is an inner component
    if (!componentStack.isEmpty()) {
      component.setIsInnerComponent(true);
    }
    
    // timing
    component.setBehaviorKind(Timing.getBehaviorKind(node));
    
    componentStack.push(component);
    addToScopeAndLinkWithNode(component, node);
    
    // Transform SimpleConncetors to normal qaualified connectors
    for (ASTSubComponent astSubComponent : node.getSubComponents()) {
      for (ASTSubComponentInstance astSubComponentInstance : astSubComponent.getInstancesList()) {
        simpleConnectorTrafo.transform(astSubComponentInstance, component);
      }
    }
    
    autoConnectionTrafo.transformAtStart(node, component);
  }
  
  @Override
  public void visit(ASTMontiArcAutoConnect node) {
    autoConnectionTrafo.transform(node, componentStack.peek());
  }
  
  protected void setParametersOfComponent(final ComponentSymbol componentSymbol,
      final ASTComponentHead astMethod) {
    for (ASTParameter astParameter : astMethod.getParameterList()) {
      final String paramName = astParameter.getName();
      astParameter.setEnclosingScope(currentScope().get());
      setEnclosingScopeOfNodes(astParameter);
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astParameter.getType());
      JTypeReference<? extends JTypeSymbol> paramTypeSymbol = new JavaTypeSymbolReference(
          TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astParameter
              .getType()),
          currentScope().get(), dimension);
      
      addTypeArgumentsToTypeSymbol(paramTypeSymbol, astParameter.getType());
      final JFieldSymbol parameterSymbol = javaSymbolFactory.createFormalParameterSymbol(paramName,
          (JavaTypeSymbolReference) paramTypeSymbol);
      componentSymbol.addConfigParameter(parameterSymbol);
    }
  }
  
  /**
   * Determine whether the component should be instantiated.
   * 
   * @param node ASTComponent node to analyze
   * @param symbol Symbol of the {@param node}
   * @return true, iff an instance should be created.
   */
  private boolean needsInstanceCreation(ASTComponent node, ComponentSymbol symbol) {
    boolean instanceNameGiven = node.getInstanceNameOpt().isPresent();
    boolean autoCreationPossible = symbol.getFormalTypeParameters().size() == 0;
    boolean noParametersRequired = symbol.getConfigParameters().size() == 0;
    // TODO: Check if the component only contains default parameters
    
    return instanceNameGiven || (autoCreationPossible && noParametersRequired);
  }
  
  @Override
  public void endVisit(ASTComponent node) {
    ComponentSymbol component = componentStack.pop();
    autoConnectionTrafo.transformAtEnd(node, component);
    
    // super component
    if (node.getHead().isPresentSuperComponent()) {
      ASTReferenceType superCompRef = node.getHead().getSuperComponent();
      String superCompName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(superCompRef);
      
      ComponentSymbolReference ref = new ComponentSymbolReference(superCompName,
          currentScope().get());
      ref.setAccessModifier(BasicAccessModifier.PUBLIC);
      // actual type arguments
      addTypeArgumentsToComponent(ref, superCompRef);
      
      component.setSuperComponent(Optional.of(ref));
    }
    
    removeCurrentScope();
    
    // for inner components the symbol must be fully created to reference it.
    // Hence, in endVisit we
    // can reference it and put the instance of the inner component into its
    // parent scope.
    
    if (component.isInnerComponent()) {
      String referencedComponentTypeName = component.getFullName();
      ComponentSymbolReference refEntry = new ComponentSymbolReference(
          referencedComponentTypeName, component.getSpannedScope());
      refEntry.setReferencedComponent(Optional.of(component));
      
      if (needsInstanceCreation(node, component)) {
        // create instance
        String instanceName = node.getInstanceNameOpt()
            .orElse(StringTransformations.uncapitalize(component.getName()));
        
        if (node.getActualTypeArgumentOpt().isPresent()) {
          setActualTypeArgumentsOfCompRef(refEntry,
              node.getActualTypeArgument().getTypeArgumentList());
        }
        
        ComponentInstanceSymbol instanceSymbol = new ComponentInstanceSymbol(instanceName,
            refEntry);
        Log.debug("Created component instance " + instanceSymbol.getName()
            + " referencing component type " + referencedComponentTypeName,
            MontiArcSymbolTableCreator.class.getSimpleName());
        
        addToScope(instanceSymbol);
      }
      
      // check whether there are already instances of the inner component type
      // defined in the component type. We then have to set the referenced
      // component.
      Collection<ComponentInstanceSymbol> instances = component.getEnclosingScope()
          .resolveLocally(ComponentInstanceSymbol.KIND);
      
      for (ComponentInstanceSymbol instance : instances) {
        if (instance.getComponentType().getName().equals(component.getName())) {
          instance.getComponentType().setReferencedComponent(Optional.of(component));
        }
      }
    }
  }
  
  private void setActualTypeArgumentsOfCompRef(ComponentSymbolReference typeReference,
      List<ASTTypeArgument> astTypeArguments) {
    List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
    for (ASTTypeArgument astTypeArgument : astTypeArguments) {
      if (astTypeArgument instanceof ASTWildcardType) {
        ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;
        
        // Three cases can occur here: lower bound, upper bound, no bound
        if (astWildcardType.isPresentLowerBound() || astWildcardType.isPresentUpperBound()) {
          // We have a bound.
          // Examples: Set<? extends Number>, Set<? super Integer>
          
          // new bound
          boolean lowerBound = astWildcardType.isPresentLowerBound();
          ASTType typeBound = lowerBound
              ? astWildcardType.getLowerBound()
              : astWildcardType.getUpperBound();
          int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
          JTypeReference<? extends JTypeSymbol> typeBoundSymbolReference = new JavaTypeSymbolReference(
              TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound),
              currentScope().get(), dimension);
          ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
              typeBoundSymbolReference);
          
          // init bound
          addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound);
          
          actualTypeArguments.add(actualTypeArgument);
        }
        else {
          // No bound. Example: Set<?>
          actualTypeArguments.add(new ActualTypeArgument(false, false, null));
        }
      }
      else if (astTypeArgument instanceof ASTType) {
        // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
        ASTType astTypeNoBound = (ASTType) astTypeArgument;
        int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound);
        JTypeReference<? extends JTypeSymbol> typeArgumentSymbolReference = new JavaTypeSymbolReference(
            TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astTypeNoBound),
            currentScope().get(), dimension);
        
        addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound);
        
        actualTypeArguments.add(new ActualTypeArgument(typeArgumentSymbolReference));
      }
      else {
        Log.error("0xMA073 Unknown type argument " + astTypeArgument + " of type "
            + typeReference);
      }
    }
    typeReference.setActualTypeArguments(actualTypeArguments);
  }
  
  protected JTypeReferenceFactory<JavaTypeSymbolReference> typeRefFactory = (name, scope,
      dim) -> new JavaTypeSymbolReference(name, scope, dim);
  
  protected void addTypeArgumentsToComponent(ComponentSymbolReference typeReference,
      ASTType astType) {
    if (astType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
      if (!astSimpleReferenceType.getTypeArgumentsOpt().isPresent()) {
        return;
      }
      setActualTypeArgumentsOfCompRef(typeReference,
          astSimpleReferenceType.getTypeArguments().getTypeArgumentList());
    }
    else if (astType instanceof ASTComplexReferenceType) {
      ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
      for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
          .getSimpleReferenceTypeList()) {
        /* ASTComplexReferenceType represents types like class or interface
         * types which always have ASTSimpleReferenceType as qualification. For
         * example: a.b.c<Arg>.d.e<Arg> */
        setActualTypeArgumentsOfCompRef(typeReference,
            astSimpleReferenceType.getTypeArguments().getTypeArgumentList());
      }
    }
    
  }
  
  /**
   * Adds the TypeParameters to the ComponentSymbol if it declares
   * TypeVariables. Since the restrictions on TypeParameters may base on the
   * JavaDSL its the actual recursive definition of bounds is respected and its
   * implementation within the JavaDSL is reused. Example:
   * <p>
   * component Bla<T, S extends SomeClass<T> & SomeInterface>
   * </p>
   * T and S are added to Bla.
   *
   * @param componentSymbol
   * @param optionalTypeParameters
   * @return currentScope
   * @see JTypeSymbolsHelper
   */
  protected static List<JTypeSymbol> addTypeParametersToComponent(
      ComponentSymbol componentSymbol, Optional<ASTTypeParameters> optionalTypeParameters,
      Scope currentScope) {
    if (optionalTypeParameters.isPresent()) {
      // component has type parameters -> translate AST to Java Symbols and add
      // these to the
      // componentSymbol.
      ASTTypeParameters astTypeParameters = optionalTypeParameters.get();
      for (ASTTypeVariableDeclaration astTypeParameter : astTypeParameters
          .getTypeVariableDeclarationList()) {
        // TypeParameters/TypeVariables are seen as type declarations.
        JavaTypeSymbol javaTypeVariableSymbol = javaSymbolFactory
            .createTypeVariable(astTypeParameter.getName());
        
        List<ASTType> types = new ArrayList<ASTType>(astTypeParameter.getUpperBoundList());
        // reuse JavaDSL
        JTypeSymbolsHelper.addInterfacesToType(javaTypeVariableSymbol, types, currentScope,
            javaTypeRefFactory);
        
        componentSymbol.addFormalTypeParameter(javaTypeVariableSymbol);
      }
    }
    return componentSymbol.getFormalTypeParameters();
  }
  
  /***************************************
   * Java/P integration
   ***************************************/
  
  @Override
  public void visit(ASTJavaPBehavior node) {
    MutableScope javaPScope = new CommonScope(true);
    javaPScope.setResolvingFilters(currentScope().get().getResolvingFilters());
    javaPScope.setEnclosingScope(currentScope().get());
    node.setEnclosingScope(currentScope().get());
    this.scopeStack.addLast(javaPScope);
  }
  
  @Override
  public void endVisit(ASTJavaPBehavior node) {
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  public void visit(ASTValueInitialization init) {
    Scope enclosingScope = currentScope().get();
    String qualifiedName = init.getQualifiedName().toString();
    Optional<VariableSymbol> var = enclosingScope
        .<VariableSymbol> resolve(qualifiedName, VariableSymbol.KIND);
    if (var.isPresent()) {
      var.get().setValuation(Optional.of(init.getValuation()));
    }
    
  }
  
  /**
   * Visits ASTLocalVariableDeclaration nodes that occur in AJava compute
   * blocks. A new VariableSymbol is created for declared variables and added to
   * the scope
   *
   * @param variableDeclaration ASTNode that is a variable declaration.
   */
  public void visit(ASTLocalVariableDeclaration variableDeclaration) {
    final ASTType type = variableDeclaration.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(type);
    
    List<String> names = new ArrayList<>();
    
    for (ASTVariableDeclarator astVariableDeclarator : variableDeclaration
        .getVariableDeclaratorList()) {
      final String variableName = astVariableDeclarator.getDeclaratorId().getName();
      names.add(variableName);
    }
    
    if (names.isEmpty()) {
      names.add(StringTransformations.uncapitalize(typeName));
    }
    
    for (String name : names) {
      VariableSymbol variableSymbol = new VariableSymbol(name);
      
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(type);
      JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
          currentScope().get(), dimension);
      addTypeArgumentsToTypeSymbol(typeRef, type);
      
      variableSymbol.setTypeReference(typeRef);
      
      addToScopeAndLinkWithNode(variableSymbol, variableDeclaration);
    }
  }
  
  /**
   * Checks whether the passed name references to a configuration parameter
   * 
   * @param name Name of the parameter to look up
   * @return true, iff the currently processed node has a parameter of the
   * passed name
   */
  private boolean isConfigurationArgument(String name) {
    for (ASTParameter param : this.currentComponent.getHead().getParameterList()) {
      if (name.equals(param.getName())) {
        return true;
      }
    }
    return false;
  }
  
  /***************************************
   * I/O Automaton integration
   ***************************************/
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#visit(de.monticore.lang.montiarc.montiarc._ast.ASTAutomatonBehavior)
   */
  @Override
  public void visit(ASTAutomatonBehavior node) {
    MutableScope automatonScope = new CommonScope(true);
    automatonScope.setResolvingFilters(currentScope().get().getResolvingFilters());
    automatonScope.setEnclosingScope(currentScope().get());
    node.setEnclosingScope(currentScope().get());
    this.scopeStack.addLast(automatonScope);
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#endVisit(de.monticore.lang.montiarc.montiarc._ast.ASTAutomatonBehavior)
   */
  @Override
  public void endVisit(ASTAutomatonBehavior node) {
    removeCurrentScope();
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#visit(de.monticore.lang.montiarc.montiarc._ast.ASTAutomaton)
   */
  @Override
  public void visit(ASTAutomaton node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void endVisit(ASTAutomaton node) {
    setEnclosingScopeOfNodes(node);
    // automaton core loaded & all enclosing scopes set, so we can reconstruct
    // the missing assignment names
    node.accept(new AssignmentNameCompleter(currentScope().get()));
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#visit(de.monticore.lang.montiarc.montiarc._ast.ASTState)
   */
  @Override
  public void visit(ASTState node) {
    StateSymbol state = new StateSymbol(node.getName());
    if (node.getStereotypeOpt().isPresent()) {
      for (ASTStereoValue value : node.getStereotype().getValuesList()) {
        state.addStereoValue(value.getName());
      }
    }
    addToScopeAndLinkWithNode(state, node);
  }
  
  /**
   * @see montiarc._visitor.MontiArcVisitor#endVisit(montiarc._ast.ASTState)
   */
  @Override
  public void endVisit(ASTState node) {
    super.endVisit(node);
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#visit(de.monticore.lang.montiarc.montiarc._ast.ASTInitialStateDeclaration)
   */
  @Override
  public void visit(ASTInitialStateDeclaration node) {
    MutableScope scope = currentScope().get();
    for (String name : node.getNameList()) {
      scope.<StateSymbol> resolveMany(name, StateSymbol.KIND).forEach(c -> {
        c.setInitial(true);
        c.setInitialReactionAST(node.getBlockOpt());
        if (node.isPresentBlock()) {
          for (ASTIOAssignment assign : node.getBlock().getIOAssignmentList()) {
              if (assign.isPresentName()) {
                Optional<VariableSymbol> var = currentScope().get()
                    .<VariableSymbol> resolve(assign.getName(), VariableSymbol.KIND);
                if (var.isPresent()) {
                  if (assign.getValueListOpt().isPresent()
                      && !assign.getValueList().getAllValuations().isEmpty()) {
                    // This only covers the case "var i = somevalue"
                    ASTValuation v = assign.getValueList().getAllValuations().get(0);
                    var.get().setValuation(Optional.of(v));
                  }
                }
            }
          }
        }
      });
    }
  }
  
  @Override
  public void visit(ASTTransition node) {
    // get target name, if there is no get source name (loop to itself)
    // TODO what about same transitions with other stimulus? -> name clash
    String targetName = node.getTargetOpt().orElse(node.getSource());
    
    StateSymbolReference source = new StateSymbolReference(node.getSource(), currentScope().get());
    StateSymbolReference target = new StateSymbolReference(targetName, currentScope().get());
    
    TransitionSymbol transition = new TransitionSymbol(node.getSource() + " -> " + targetName);
    transition.setSource(source);
    transition.setTarget(target);
    
    transition.setGuardAST(node.getGuardOpt());
    transition.setReactionAST(node.getReactionOpt());
    
    addToScopeAndLinkWithNode(transition, node); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTTransition node) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTIOAssignment node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  /**
   * @see montiarc._symboltable.MontiArcSymbolTableCreatorTOP#visit(montiarc._ast.ASTStateDeclaration)
   */
  @Override
  public void visit(ASTStateDeclaration ast) {
    // to prevent creation of unnecessary scope we override with nothing
  }
  
  /**
   * @see montiarc._symboltable.MontiArcSymbolTableCreatorTOP#endVisit(montiarc._ast.ASTStateDeclaration)
   */
  @Override
  public void endVisit(ASTStateDeclaration ast) {
    // to prevent deletion of not existing scope we override with nothing
  }
  
}
