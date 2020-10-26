<#-- (c) https://github.com/MontiCore/monticore -->
<#--

Implementing this method is mandatory.

@return the implementation of the compute() method
-->
<#--def String printCompute(ComponentTypeSymbol comp, String compname);-->

<#--

Implementing this method is mandatory.

@return the implementation of the getInitialValues() method
-->
<#--def String printGetInitialValues(ComponentTypeSymbol comp, String compname);-->

<#--

This method can be used to add additional code to the implementation class without.
-->
<#--def String hook(ComponentTypeSymbol comp, String compname);-->

<#--

Entry point for generating a component's implementation.

-->

${tc.signature("comp","compname")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
#include "${compname}Impl.h"
${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.behavior.aBehaviorGenerator.generateBody", [comp, compname])}
</#if>
${Utils.printNamespaceEnd(comp)}