/* generated from model null*/
/* generated by template symboltable.ModelNameCalculator*/




package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import java.util.LinkedHashSet;
import java.util.Set;

import de.monticore.symboltable.SymbolKind;

public class MontiArcAutomatonModelNameCalculator extends de.monticore.CommonModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(final String name, final SymbolKind kind) {
    final Set<String> calculatedModelNames = new LinkedHashSet<>();

      if (BehaviorEmbeddingSymbol.KIND.isKindOf(kind)) {
        calculatedModelNames.addAll(calculateModelNameForBehaviorEmbedding(name));
      }

    return calculatedModelNames;
  }

  protected Set<String> calculateModelNameForBehaviorEmbedding(String name) {
    final Set<String> modelNames = new LinkedHashSet<>();
    modelNames.add(name);
    return modelNames;
  }


}
