/* generated by template templates.de.monticore.lang.tagschema.TagSchema*/


package montiarc.tagging.distribution;

import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

/**
 * generated by TagSchema.ftl
 */
public class DistributionSchema {

  protected static DistributionSchema instance = null;

  protected DistributionSchema() {}

  protected static DistributionSchema getInstance() {
    if (instance == null) {
      instance = new DistributionSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TaggingResolver tagging) {
    tagging.addTagSymbolCreator(new ConnectionSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(ConnectionSymbol.KIND));
  }

  public static void registerTagTypes(TaggingResolver tagging) {
    getInstance().doRegisterTagTypes(tagging);
  }

}