<#-- (c) https://github.com/MontiCore/monticore -->
# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

# Build Image -----------------------------
<#if config.getMessageBroker().toString() == "DDS">
    FROM montithings/mtcmakedds AS build
<#else>
    FROM montithings/mtcmake AS build
</#if>

# Switch into our apps working directory
WORKDIR /usr/src/app/

# Copy all the app source into docker context
COPY . /usr/src/app/

# In case it was build without docker before
RUN rm -rf build

# Build our binary/binaries
<#-- comp is the application -->
<#if config.getSplittingMode().toString() == "OFF">
RUN ./build.sh ${comp.getPackageName()}
<#else>
RUN ./build.sh ${comp.getFullName()}
</#if>
# -----------------------------------------

<#if config.getSplittingMode().toString() == "OFF">
    # COMPONENT: ${comp.getFullName()}
    <#-- the dds build image is based on ubuntu, thus we have to distinguish -->
    <#if config.getMessageBroker().toString() == "DDS">
    FROM ubuntu:groovy AS ${comp.getFullName()?lower_case}
    <#else>
    FROM alpine AS ${comp.getFullName()?lower_case}
    
    RUN apk add --update-cache libgcc libstdc++
    </#if>

    <#if config.getMessageBroker().toString() == "MQTT">
    RUN apk add --update-cache mosquitto-libs++
    </#if>

    COPY --from=build /usr/src/app/build/bin/${comp.getFullName()} /usr/src/app/build/bin/

    WORKDIR /usr/src/app/build/bin

    RUN echo './${comp.getFullName()} "$@"' > entrypoint.sh

    # Run our binary on container startup
    ENTRYPOINT [ "sh", "entrypoint.sh" ]

<#else>
    <#-- helper list to detect duplicated keys -->
    <#assign processedInstances = [] />

    <#list instances as pair >
        <#if ! processedInstances?seq_contains(pair.getKey().fullName)>
            <#assign processedInstances = processedInstances + [pair.getKey().fullName] />

            # COMPONENT: ${pair.getKey().fullName}
            <#-- the dds build image is based on ubuntu, thus we have to distinguish -->
            <#if config.getMessageBroker().toString() == "DDS">
            FROM debian:buster AS ${pair.getKey().fullName}
            <#else>
            FROM alpine AS ${pair.getKey().fullName}

            RUN apk add --update-cache libgcc libstdc++
            </#if>

            <#if config.getMessageBroker().toString() == "MQTT">
            RUN apk add --update-cache mosquitto-libs++
            </#if>

            COPY --from=build /usr/src/app/build/bin/${pair.getKey().fullName} /usr/src/app/build/bin/

            WORKDIR /usr/src/app/build/bin

            RUN echo './${pair.getKey().fullName} "$@"' > entrypoint.sh

            # Run our binary on container startup
            ENTRYPOINT [ "sh", "entrypoint.sh" ]
        </#if>
    </#list>
</#if>
