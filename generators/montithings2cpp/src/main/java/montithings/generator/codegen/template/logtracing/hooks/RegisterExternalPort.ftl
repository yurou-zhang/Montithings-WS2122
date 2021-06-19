<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON" && !(comp.getPorts()?size == 0)>
  <#list comp.getPorts() as port>
    <#assign additionalPort = GeneratorHelper.getPortHwcTemplateName(port, config)>
    <#if config.getTemplatedPorts()?seq_contains(port) && additionalPort!="Optional.empty">
      this->logTracer->registerExternalPort("${port.getName()}");
    </#if>
  </#list>
</#if>
