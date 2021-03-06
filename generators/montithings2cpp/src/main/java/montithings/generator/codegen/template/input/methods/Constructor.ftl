<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
${className}${Utils.printFormalTypeParameters(comp, false)}::${className}(
<#list comp.getAllIncomingPorts() as port>
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>
  tl::optional<Message<${type}>> ${port.getName()}
  <#sep>,</#sep>
</#list>){
<#if comp.isPresentParentComponent()>
  super(
    <#list comp.parent().loadedSymbol.allIncomingPorts as port >
      port.getName()
    </#list>);
</#if>
<#list comp.getIncomingPorts() as port >
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>

  if(${port.getName()}.has_value()) {this->${port.getName()} = std::move(${port.getName()}.value());}
  else {this->${port.getName()} = Message<${type}>(tl::nullopt);}
    <#if ComponentHelper.hasAgoQualification(comp, port)>
      auto nowOf__${port.getName()?cap_first} = std::chrono::system_clock::now();
      dequeOf__${port.getName()?cap_first}.push_back(std::make_pair(nowOf__${port.getName()?cap_first}, ${port.getName()}.value()));
      cleanDequeOf${port.getName()?cap_first}(nowOf__${port.getName()?cap_first});
    </#if>
</#list>
}