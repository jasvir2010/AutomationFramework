#Base image
FROM openjdk:8

MAINTAINER Saurabh Shukla <saurabh.shukla@ihsmarkit.com>

RUN apt-get update \
    && apt-get install -y maven

COPY src /usr/local/framework/src
COPY Configuration.properties /usr/local/framework/Configuration.properties
COPY Logger.properties /usr/local/framework/Logger.properties
COPY pom.xml /usr/local/framework/pom.xml
COPY .m2 /usr/local/framework/.m2
WORKDIR /usr/local/framework