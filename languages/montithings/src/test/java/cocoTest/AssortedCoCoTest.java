// (c) https://github.com/MontiCore/monticore
package cocoTest;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montithings._ast.ASTMontiThingsNode;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class AssortedCoCoTest extends AbstractCoCoTest {
  private static final String PACKAGE = "cocoTest";

  private ASTMontiArcNode node;

  @BeforeClass
  public static void setup() {
    Log.enableFailQuick(false);
  }

  @Test
  public void interfaceComponentContentTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "InterfaceComponentWithContent");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceComponentContainsOnlyPorts()),
        node,
        new ExpectedErrorInfo(3, "xMT200"));
  }

  @Test
  public void multipleBehaviorTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "MultipleExecutionBlocks");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new MaxOneBehaviorPerComponent()),
        node,
        new ExpectedErrorInfo(1, "xMT110"));
  }

  @Test
  public void defaultValueTypeCheckTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE + "." + "DefaultValueWrongType");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new DefaultValuesCorrectlyAssigned()),
        node, new ExpectedErrorInfo(1, "xMT014"));
  }

  @Test
  public void javaPBehaviorTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "JavaPBehavior");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new NoJavaPBehavior()),
        node,
        new ExpectedErrorInfo(1, "xMT125"));
  }

  @Test
  public void javaImportTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "JavaImport");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new NoJavaImportStatements()),
        node,
        new ExpectedErrorInfo(1, "xMT124"));
  }

  @Test
  public void javaValidImportTest() {
    checkValid(PACKAGE, "JavaImportValid");
  }

  @Test
  public void TimeSyncInAtomicTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "TimeSyncInAtomic");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new TimeSyncOnlyInComposedComponents()),
        node,
        new ExpectedErrorInfo(1, "xMT119"));
  }

  @Test
  public void TimeSyncInSubComponentsTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "TimeSyncInSubComps");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new TimeSyncInSubComponents()),
        node,
        new ExpectedErrorInfo(2, "xMT120"));
  }

  @Test
  public void notInterface() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." +"genericBindingTest.interfaceNotFound."+ "Bind");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface()),
        node,
        new ExpectedErrorInfo(1, "xMT141"));
  }

  @Test
  public void implementationMissing() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." +"genericBindingTest.implementationMissing."+ "Assignment");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface()),
        node,
        new ExpectedErrorInfo(2, "xMT143"));
  }

  @Test
  public void interfaceImplementsInterface() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." +"genericBindingTest.interfaceImplementsInterface."+ "Assignment");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface()),
        node,
        new ExpectedErrorInfo(2, "xMT144"));
  }

  @Test
  public void notFitsInterface() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." +"genericBindingTest.notFitsInterface."+ "Assignment");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface()),
        node,
        new ExpectedErrorInfo(2, "xMT145"));
  }

  @Ignore
  @Test
  public void genericParameterInterfaceNotFound() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." +"genericBindingTest.genericParameterInterfaceNotFound."+ "Assignment");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface()),
        node,
        new ExpectedErrorInfo(1, "xMT146"));
  }

  @Test
  public void genericParameterNotFitsInterface() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." +"genericBindingTest.genericParameterNotFitsInterface."+ "Bind");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface()),
        node,
        new ExpectedErrorInfo(2, "xMT147","xMT141"));
  }

  @Test
  public void genericParameterNeedsInterface() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." +"genericBindingTest.genericParameterNeedsInterface."+ "Bind");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface()),
        node,
        new ExpectedErrorInfo(1, "xMT148"));
  }
}
