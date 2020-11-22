// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import montithings._ast.ASTMTComponentType;
import montithings._visitor.MontiThingsVisitor;
import montithings.generator.helper.GeneratorHelper;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds all ports with templates
 */
public class FindTemplatedPortsVisitor implements MontiThingsVisitor {

  protected Set<PortSymbol> templatedPorts = new HashSet<>();

  protected Path hwcTemplatePath;

  protected Set<PortSymbol> findTemplatedPorts(ComponentTypeSymbol compSymbol, Path hwcTemplatePath) {
    Set<PortSymbol> templatedPorts = new HashSet<>();

    // Go through all ports ...
    for (PortSymbol portSymbol : compSymbol.getAllPorts()) {
      // ... and check if they have template code
      if (GeneratorHelper.portHasHwcTemplate(portSymbol, hwcTemplatePath)) {
        templatedPorts.add(portSymbol);
      }
    }

    return templatedPorts;
  }

  @Override public void visit(ASTMTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(),
      "ASTComponent node '%s' has no symbol. "
        + "Did you forget to run the SymbolTableCreator?", node.getName());
    final ComponentTypeSymbol compSymbol = node.getSymbol();
    templatedPorts.addAll(findTemplatedPorts(compSymbol, hwcTemplatePath));
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public FindTemplatedPortsVisitor(Path hwcTemplatePath) {
    this.hwcTemplatePath = hwcTemplatePath;
  }

  public Set<PortSymbol> getTemplatedPorts() {
    return templatedPorts;
  }

  public void setTemplatedPorts(Set<PortSymbol> templatedPorts) {
    this.templatedPorts = templatedPorts;
  }

  public Path getHwcTemplatePath() {
    return hwcTemplatePath;
  }

  public void setHwcTemplatePath(Path hwcTemplatePath) {
    this.hwcTemplatePath = hwcTemplatePath;
  }
}