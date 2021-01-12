// (c) https://github.com/MontiCore/monticore
package cdlangextension._symboltable;

import cdlangextension._ast.ASTCDEImportStatement;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;

import java.util.Optional;

/**
 * TODO
 *
 * @since 18.12.20
 */
public class CDLangExtensionSymbolTableCreator extends CDLangExtensionSymbolTableCreatorTOP {

  @Override public void visit(ASTCDEImportStatement node) {
    super.visit(node);
    Optional<CDTypeSymbol> symbol = node.getEnclosingScope().resolveCDType(node.getCdType().getQName());
    symbol.ifPresent(node::setCdTypeSymbol);
  }
}