package de.monticore.automata.ioautomata.test;

import static org.junit.Assert.assertFalse;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.automaton.ioautomaton._symboltable.StateSymbol;
import de.monticore.automaton.ioautomatonjava._symboltable.IOAutomatonJavaLanguage;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;

public class SymTabTest extends AbstractSymtabTest { 
  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
  }
  
  @Test
  public void test() {
    Scope symTab = createSymTab("src/test/resources/");
    Object a = symTab.getSymbols();
    Log.info("", "test");
    
//    Optional<JTypeSymbol> javaType = symTab.resolve("String", JTypeSymbol.KIND);
//    assertFalse(
//        "java.lang types may not be resolvable without qualification in general (e.g., global scope).",
//        javaType.isPresent());
    
    AutomatonSymbol symbol = symTab.<AutomatonSymbol>resolve("invalid.OutputInExpression", AutomatonSymbol.KIND).orElse(null);
    StateSymbol s = symbol.getSpannedScope().<StateSymbol>resolve("S1", StateSymbol.KIND).orElse(null);
    symbol = null;
  }
}
