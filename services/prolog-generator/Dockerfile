FROM gradle:6.1-jdk11 as build

COPY . /src/main/app
WORKDIR /src/main/app/services/prolog-generator
RUN gradle --no-daemon clean build



FROM openjdk:11-jre

COPY --from=build /src/main/app/services/prolog-generator/server/target/libs /src/main/app

WORKDIR /src/main/app
ENTRYPOINT [ "java", "-cp", "PrologGeneratorServer.jar", "-Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StdErrLog", "Main" ]
