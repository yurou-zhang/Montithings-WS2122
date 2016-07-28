package de.monticore.automata.ioautomatajava.coco;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.automaton.ioautomaton._ast.ASTIOAutomatonNode;
import de.se_rwth.commons.logging.Log;

public class TypeCorrectnessTest extends AbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Ignore
  @Test
  public void testGuardNotBool() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.GuardIsNotBoolean");
    checkInvalid(node, new ExpectedErrorInfo(3, "xAA401"));
  }
  
  @Test
  public void testStimulusAndEventDontFit() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.StimulusAndEventDontFit");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA405"));
  }
  
  @Test
  public void testInitialReactionAndActionDontFit() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.InitialReactionAndActionDontFit");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA402"));
  }
  
  @Test
  public void testReactionAndActionFit() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.ReactionAndActionDontFit");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA404"));
  }
  
  @Test
  public void testInitialValueFit() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.InitialValueFit");
    // input int a=5, b="Wrong";
    // variable int c="Wrong";
    // output int d="Wrong", e=5; 
    checkInvalid(node, new ExpectedErrorInfo(3, "xAA403"));
  }
  
}
