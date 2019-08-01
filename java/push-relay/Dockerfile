# Requires docker >= 17.05 (requires support for multi-stage builds)
FROM maven:3.6.1-jdk-11-slim as build

WORKDIR /home/app
COPY m2-docker /root/.m2/
COPY pom.xml /home/app/

# build all dependencies
RUN mvn dependency:go-offline -B

COPY src src

RUN mvn package

#
# minimize
#
FROM openjdk:11-jre-slim

COPY --from=build /home/app/target/push-relay-1.0-SNAPSHOT.war /usr/local/lib/service.war

# TODO this is not working; this docker feature is simply missing
# we want /db-store to be mounted as a volume, which causes these troubles
# WORKAROUND for https://github.com/moby/moby/issues/2259 this depends on the configured db-store file path!
# RUN mkdir /db-store && chown 1001:1001 /db-store
# don't run as root
# USER 1001:1001

ENTRYPOINT ["java","-jar","/usr/local/lib/service.war"]
