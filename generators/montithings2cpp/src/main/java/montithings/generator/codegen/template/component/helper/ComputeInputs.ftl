<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","isMonitor")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if !ComponentHelper.usesBatchMode(comp)>
  ${compname}Input${Utils.printFormalTypeParameters(comp)} ${Identifier.getInputName()}<#if comp.getAllIncomingPorts()?has_content>(<#list comp.getAllIncomingPorts() as inPort >
  <#if ComponentHelper.isSIUnitPort(inPort)>
    tl::make_optional(${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}<#else>this->uuid</#if>)
    .value()  * this->${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}ConversionFactor()
  <#else>
      ${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}<#else>this->uuid</#if>
  </#if>
)<#sep>,</#sep>
</#list>)</#if>;
<#else>
  ${compname}Input${Utils.printFormalTypeParameters(comp)} ${Identifier.getInputName()};
  <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
    while(${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->hasValue(this->uuid)){
    ${Identifier.getInputName()}.add${inPort.getName()?cap_first}Element(${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(
      <#if isMonitor>
        portMonitorUuid${inPort.getName()?cap_first}
      <#else>
        this->uuid
      </#if>));
    }
  </#list>
  <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort >
    ${Identifier.getInputName()}.add${inPort.getName()?cap_first}Element(${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(
    <#if isMonitor>
      portMonitorUuid${inPort.getName()?cap_first}
    <#else>
      this->uuid
    </#if>));
  </#list>
</#if>