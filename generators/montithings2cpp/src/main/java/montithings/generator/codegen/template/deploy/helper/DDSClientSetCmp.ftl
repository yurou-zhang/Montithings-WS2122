<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getMessageBroker().toString() == "DDS">
    cmp.setDDSCmdArgs(ddsArgc, ddsArgv);
</#if>

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
  ddsClient.setComp(&cmp);

  ddsClient.initializeOutgoingPorts();
  ddsClient.initializeConnectorConfigPortSub();
  ddsClient.publishConnectorConfig();
</#if>