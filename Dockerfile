ARG TOMCAT_IMAGE_VERSION=9-jdk8-openjdk-slim
FROM tomcat:$TOMCAT_IMAGE_VERSION

## Define for which project this image is build
ARG PROJECT=samply
ENV PROJECT=$PROJECT

RUN ["rm", "-fr", "/usr/local/tomcat/webapps"]

RUN set -x ; \
    adduser --no-create-home --disabled-password --system --ingroup www-data connector

COPY --chown=connector:www-data target/connector/ /usr/local/tomcat/webapps/ROOT/

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

# JMX Exporter
ENV JMX_EXPORTER_VERSION 0.3.1
COPY src/docker/jmx-exporter.yml                /docker/jmx-exporter.yml
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/${JMX_EXPORTER_VERSION}/jmx_prometheus_javaagent-${JMX_EXPORTER_VERSION}.jar /docker/

ADD src/docker/start.sh                         /docker/
RUN chmod +x                                    /docker/start.sh
CMD ["sh", "-c", "/docker/start.sh"]
