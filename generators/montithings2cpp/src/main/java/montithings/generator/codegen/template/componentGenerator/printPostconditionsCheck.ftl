${tc.signature("comp","compname")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
    <#assign postconditions = ComponentHelper.getPostconditions(comp)>
    <#list postconditions as statement>
      if (
        <#list ComponentHelper.getPortsInGuardExpression(statement.guard) as port>
            <#if !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.getName())>
                <#if port.isIncoming>
                  input.get${port.getName()?cap_first}()
                <#else>
                  result.get${port.getName()?cap_first}()
                </#if>
            <#else>
              true // presence of value on port ${port.getName()} not checked as it is compared to NoData
            </#if>
            <#sep>&&</#sep>
        </#list>
        <#if ComponentHelper.getPortsInGuardExpression(statement.guard)?size == 0>
          true // presence of value on ports not checked as they are not used in precondition
        </#if>
      &&
      !(
        ${Utils.printExpression(statement.guard)}
      )) {
        <#if ComponentHelper.getCatch(comp, statement)??>
            ${ComponentHelper.printJavaBlock(ComponentHelper.getCatch(comp, statement).get().handler)}
        <#else>
          std::stringstream error;
          error << "Violated postcondition ${Utils.printExpression(statement.guard, false)} on component ${comp.packageName}.${compname}" << std::endl;
          error << "Port values: " << std::endl;
            <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort>
              if (input.get${inPort.getName()?cap_first} ().has_value()) {
              error << "In port \"${inPort.getName()}\": " << input.get${inPort.getName()?cap_first} ().value() << std::endl;
              } else {
              error << "In port \"${inPort.getName()}\": No data." << std::endl;
              }
            </#list>
            <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
              if (input.get${inPort.getName()?cap_first} ().has_value()) {
              error << "In port \"${inPort.getName()}\": " << input.get${inPort.getName()?cap_first} () << std::endl;
              } else {
              error << "In port \"${inPort.getName()}\": No data." << std::endl;
              }
            </#list>
            <#list comp.getAllOutgoingPorts() as outPort>
              if (result.get${outPort.getName()?cap_first} ().has_value()) {
              error << "Out port \"${outPort.getName()}\": " << result.get${outPort.getName()?cap_first} ().value() << std::endl;
              } else {
              error << "Out port \"${outPort.getName()}\": No data." << std::endl;
              }
            </#list>
          throw std::runtime_error(error.str ());
        </#if>
      }
    </#list>