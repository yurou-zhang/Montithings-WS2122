/* generated from model null*/
/* generated by template symboltable.SymbolKind*/


package de.monticore.automaton.ioautomaton._symboltable;

import de.monticore.symboltable.SymbolKind;

public class VariableKind implements SymbolKind {

  private static final String NAME = "de.monticore.automaton.ioautomaton._symboltable.VariableKind";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }

}
