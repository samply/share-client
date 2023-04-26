FROM alpine:latest as extract

RUN apk add --no-cache unzip

ADD target/connector.war /connector/connector.war

RUN mkdir -p /connector/extracted && \
       unzip /connector/connector.war -d /connector/extracted/

FROM tomcat:9-jre8-temurin

## Define for which project this image is build
ARG PROJECT=dktk
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

ENV POSTGRES_HOST=bridgehead-connector-db
ENV POSTGRES_PORT=5432
ENV POSTGRES_USER=samplyconnector
ENV POSTGRES_DB=samplyconnector

#TODO:
ENV LOG_LEVEL=error
ENV TEST_PROJECT=dktk
ENV PROJECT_DIR=/dktk
ENV PROTOCOL=http
ENV CONTEXT=dktk
ENV NO_PROXY=localhost,connector,connector_db
ENV CATALINA_DIR=/usr/local/tomcat/conf/Catalina/localhost
ENV DEPLOYMENT_CONTEXT=nngm-connector

ENV FEATURE_DKTK_CENTRAL_SEARCH=false
ENV FEATURE_NNGM_CTS=true
ENV FEATURE_NNGM_ENCRYPT_ID=false
ENV FEATURE_BBMRI_DIRECTORY_SYNC=false
ENV CONNECTOR_ENABLE_METRICS=false
ENV SOURCE_PROJECT_DIR=src/docker/dktk

ENV NNGM_MAINZELLISTE_URL=https://test.verbis.dkfz.de/nngm/mainzelliste
ENV PATIENTLIST_URL="http://bridgehead-patientlist:8080/Patientlist"
ENV PROJECTPSEUDONYMISATION_URL="http://bridgehead-id-manager:8080/html/projectSelection.html"
ENV CCP_CENTRALSEARCH_URL="https://centralsearch-test.dktk.dkfz.de/"
ENV CCP_DECENTRALSEARCH_URL="https://decentralsearch-test.ccp-it.dktk.dkfz.de/"
ENV NNGM_MAGICPL_URL=https://test.verbis.dkfz.de/nngm/magicpl
ENV NNGM_PROFILE=http://uk-koeln.de/fhir/StructureDefinition/Patient/nNGM/pseudonymisiert

ENV PORT="8080"
ENV MDR_URL="https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr"
ENV QUERY_LANGUAGE="QUERY"
ENV ID_MANAGER_URL="http://bridgehead-id-manager:8080"
ENV HOST="bridgehead-connector"
ENV CONNECTOR_SHARE_URL="${PROTOCOL}://${HOST}:${PORT}"
ENV SHARE_URL="${PROTOCOL}://${HOST}:${PORT}"