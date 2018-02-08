package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTValueList;
import montiarc._cocos.MontiArcASTValueListCoCo;

/**
 * Context condition that forbids the usage of value lists.
 *
 * @author Gerrit Leonhardt, Andreas Wortmann
 * @implements Not found in literature
 */
public class UseOfValueLists implements MontiArcASTValueListCoCo {
  
  @Override
  public void check(ASTValueList node) {
    if (!node.getValuation().isPresent() && !node.getValuations().isEmpty()) {
      Log.error("0xMA081 Value lists are not supported.", node.get_SourcePositionStart());
    }
  }  
}
