package de.monticore.automaton.ioautomaton;

import java.util.HashSet;
import java.util.Set;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

// TODO remove this class. Currently required because of missing functionality in the Scope class
public class ScopeHelper {
  
  /**
   * Resolves all Symbols of the given kind within the given scope tree.
   * 
   * @param scope the scope tree
   * @param kind
   * @return
   */
  public static <T extends Symbol> Set<T> resolveManyDown(Scope scope, SymbolKind kind) {
    Set<T> set = new HashSet<>();
    resolveManyDown(scope, kind, set);
    return set;
  }
  
  private static void resolveManyDown(Scope scope, SymbolKind kind, Set<? extends Symbol> set) {
    set.addAll(scope.resolveLocally(kind));
    for (Scope s : scope.getSubScopes()) {
      resolveManyDown(s, kind, set);
    }
  }
  
}