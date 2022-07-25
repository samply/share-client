FROM alpine:latest as extract

RUN apk add --no-cache unzip

ADD target/connector.war /connector/connector.war

RUN mkdir -p /connector/extracted && \
       unzip /connector/connector.war -d /connector/extracted/

FROM tomcat:9-jre8-temurin

## Define for which project this image is build
ARG PROJECT=samply
ENV PROJECT=$PROJECT

RUN ["rm", "-fr", "/usr/local/tomcat/webapps"]

COPY --from=extract /connector/extracted/ /usr/local/tomcat/webapps/ROOT/

# Adding fontconfig and libfreetype6 for rendering the BK Export, cf. https://stackoverflow.com/questions/55454036
RUN	apt-get update && apt-get install -y fontconfig libfreetype6 && \
    rm -rf /var/lib/apt/lists/*

ADD src/docker/context.xml                      ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml

ADD src/docker/samply_common_urls.xml           ${CATALINA_HOME}/conf/${PROJECT}_common_urls.xml
ADD src/docker/samply_common_operator.xml       ${CATALINA_HOME}/conf/${PROJECT}_common_operator.xml
ADD src/docker/samply_common_config.xml         ${CATALINA_HOME}/conf/${PROJECT}_common_config.xml
ADD src/docker/samply_bridgehead_info.xml       ${CATALINA_HOME}/conf/${PROJECT}_bridgehead_info.xml
ADD src/docker/_cts_info.xml                    ${CATALINA_HOME}/conf/${PROJECT}_cts_info.xml
ADD src/docker/mailSending.xml                  ${CATALINA_HOME}/conf/
ADD src/docker/log4j2.xml                       ${CATALINA_HOME}/conf/
ADD src/docker/features.properties              ${CATALINA_HOME}/conf/
ADD src/docker/secrets.properties               ${CATALINA_HOME}/conf/
ADD src/docker/reports                          /var/lib/samply/reports

# JMX Exporter
ENV JMX_EXPORTER_VERSION 0.16.1
COPY src/docker/jmx-exporter.yml                /docker/jmx-exporter.yml
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/${JMX_EXPORTER_VERSION}/jmx_prometheus_javaagent-${JMX_EXPORTER_VERSION}.jar /docker/

ENV JAVA_OPTS "-Dlog4j.configurationFile=${CATALINA_HOME}/conf/log4j2.xml"
ADD src/docker/start.sh                         /docker/
RUN chmod +x                                    /docker/start.sh
CMD ["sh", "-c", "/docker/start.sh"]

FROM httpd:2.4
COPY ./index.html/ /usr/local/apache2/htdocs/
