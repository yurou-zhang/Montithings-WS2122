/* generated from model null*/
/* generated by template symboltable.SymbolReference*/



package de.monticore.automaton.ioautomaton._symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.references.SymbolReference;
import java.util.Collection;

/**
 * Represents a reference of {@link TransitionSymbol}.
 */
public class TransitionSymbolReference extends TransitionSymbol implements SymbolReference<TransitionSymbol> {
  protected final SymbolReference<TransitionSymbol> reference;

  public TransitionSymbolReference(final String name, final Scope enclosingScopeOfReference) {
    super(name);
    reference = new CommonSymbolReference<>(name, TransitionSymbol.KIND, enclosingScopeOfReference);
  }

  /*
   * Methods of SymbolReference interface
   */

  @Override
  public TransitionSymbol getReferencedSymbol() {
    return reference.getReferencedSymbol();
  }

  @Override
  public boolean existsReferencedSymbol() {
    return reference.existsReferencedSymbol();
  }

  @Override
  public boolean isReferencedSymbolLoaded() {
    return reference.isReferencedSymbolLoaded();
  }

  /*
  * Methods of Symbol interface
  */

  @Override
  public String getName() {
    return getReferencedSymbol().getName();
  }

  @Override
  public String getFullName() {
    return getReferencedSymbol().getFullName();
  }

  @Override
  public void setEnclosingScope(MutableScope scope) {
    getReferencedSymbol().setEnclosingScope(scope);
  }

  @Override
  public Scope getEnclosingScope() {
    return getReferencedSymbol().getEnclosingScope();
  }

  @Override
  public AccessModifier getAccessModifier() {
    return getReferencedSymbol().getAccessModifier();
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    getReferencedSymbol().setAccessModifier(accessModifier);
  }

  /*
   * Methods of ScopeSpanningSymbol interface
   */
  //@Override
  //public Scope getSpannedScope() {
  //  return getReferencedSymbol().getSpannedScope();
  //}

  /*
  * Methods of TransitionSymbol class
  */


  @Override
  public Collection<GuardSymbol> getGuard() {
    return getReferencedSymbol().getGuard();
  }


}
