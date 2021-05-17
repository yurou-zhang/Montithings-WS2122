<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">

    std::vector<sole::uuid> traceUUIDs = {
         <#list comp.getAllIncomingPorts() as inPort>
             ${Identifier.getInputName()}.get${inPort.getName()?cap_first}Uuid()
             <#sep>,</#sep>
         </#list>
    };

    this->logTracer->handleInput(${Identifier.getInputName()}, traceUUIDs);
</#if>
