${tc.params("de.montiarcautomaton.ajava.generator.helper.AJavaHelper helper", "String _package", "java.util.Collection<de.monticore.symboltable.ImportStatement> imports",
"String name", "String resultName", "String inputName", "String implName",
"java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol> portsIn",
"java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol> portsOut",
"java.util.Collection<de.monticore.symboltable.types.JFieldSymbol> configParams",
"String ajava")}
package ${_package};

import ${_package}.${resultName};
import ${_package}.${inputName};
<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;

public class ${implName} implements IComputable<${inputName}, ${resultName}> {
  
  
  
  // config parameters
  <#list configParams as param>
  private final ${helper.getParamTypeName(param)} ${param.getName()};
  </#list>
  
  public ${implName}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
    <#list configParams as param>
    this.${param.getName()} = ${param.getName()};
    </#list>
  }   

  @Override
  public ${resultName} getInitialValues() {
    final ${resultName} result = new ${resultName}();

    return result;
  }

  @Override
  public ${resultName} compute(${inputName} input) {
    // inputs
    <#list portsIn as portIn>
  	final ${helper.getPortTypeName(portIn)} ${portIn.getName()} = input.get${portIn.getName()?cap_first}();
  	</#list>
  
    final ${resultName} result = new ${resultName}();
    
    <#list portsOut as portOut>
    ${helper.getPortTypeName(portOut)} ${portOut.getName()} = result.get${portOut.getName()?cap_first}();
    </#list>
    
    <#-- print methodbody here -->
    ${ajava}
    
    <#-- add always all outgoing values to result -->
    <#list portsOut as portOut>
    result.set${portOut.getName()?cap_first}(${portOut.getName()});
    </#list>
    
    
    return result;
  }
  
}
