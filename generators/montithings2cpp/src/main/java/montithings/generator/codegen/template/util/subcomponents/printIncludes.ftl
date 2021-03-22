<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

<#if config.getSplittingMode().toString() == "OFF">
  <#list comp.getSubComponents() as subcomponent>
      <#if Utils.getGenericParameters(comp)?seq_contains(subcomponent.getGenericType().getName())>
        <#assign type = subcomponent.getGenericType().getName()>
      <#else>
        <#assign type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config)>
      </#if>
      ${Utils.printPackageNamespace(comp, subcomponent)}${type} ${subcomponent.getName()};
  </#list>
<#elseif config.getMessageBroker().toString() == "OFF">
  <#list comp.getSubComponents() as subcomponent >
    std::string subcomp${subcomponent.getName()?cap_first}IP;
  </#list>
</#if>


