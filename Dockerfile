FROM alpine:latest as extract

RUN apk add --no-cache unzip

ADD target/connector.war /connector/connector.war

RUN mkdir -p /connector/extracted && \
       unzip /connector/connector.war -d /connector/extracted/

FROM tomcat:9-jre8-temurin as docker-build

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

ENV TZ="Europe/Berlin"

# Stage used by ci for dktk images (--target=dktk)
FROM docker-build as dktk
ENV TEST_PROJECT="dktk"
ENV POSTGRES_PORT="5432"
ENV CCP_CENTRALSEARCH_URL="https://centralsearch-test.dktk.dkfz.de/"
ENV CCP_DECENTRALSEARCH_URL="https://decentralsearch-test.ccp-it.dktk.dkfz.de/"
ENV CONNECTOR_ENABLE_METRICS="false"
ENV CONNECTOR_POSTGRES_DB="share_v2"
ENV CONNECTOR_POSTGRES_USER="samplyweb"
ENV CONNECTOR_SHARE_URL="${PROTOCOL}://${HOST}:${PORT}"
ENV DEPLOYMENT_CONTEXT="dktk-connector"
ENV feature_BBMRI_DIRECTORY_SYNC="false"
ENV feature_DKTK_CENTRAL_SEARCH="false"
ENV feature_NNGM_CTS="false"
ENV LOG_LEVEL="info"
ENV MDR_URL="https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr"
ENV POSTGRES_HOST="bridgehead_dktk_connector_db"
ENV PROTOCOL="http"
ENV QUERY_LANGUAGE="QUERY"
ENV CCP_PATIENTLIST_URL="http://bridgehead_patientlist:8080/Patientlist"

# Stage used by ci for gbn images (--target=gbn)
FROM docker-build as gbn
ENV TEST_PROJECT="gbn"
ENV CONNECTOR_ENABLE_METRICS="false"
ENV POSTGRES_DB="samply.connector"
ENV POSTGRES_HOST="bridgehead_gbn_connector_db"
ENV CONNECTOR_POSTGRES_USER="samply"
ENV QUERY_LANGUAGE="CQL"
ENV DEPLOYMENT_CONTEXT="gbn-connector"
ENV LOG_LEVEL="info"
ENV STORE_URL="http://bridgehead_gbn_blaze_store:8080"
ENV PROTOCOL="http"
ENV PORT="8080"

# Stage used by ci for c4 images (--target=c4)
FROM docker-build as c4
ENV TEST_PROJECT="c4"
ENV POSTGRES_PORT="5432"
ENV CCP_CENTRALSEARCH_URL="https://centralsearch-test.dktk.dkfz.de/"
ENV CCP_DECENTRALSEARCH_URL="https://decentralsearch-test.ccp-it.dktk.dkfz.de/"
ENV CONNECTOR_ENABLE_METRICS="false"
ENV CONNECTOR_POSTGRES_DB="share_v2"
ENV CONNECTOR_POSTGRES_USER="samplyweb"
ENV CONNECTOR_SHARE_URL="${PROTOCOL}://${HOST}:${PORT}"
ENV DEPLOYMENT_CONTEXT="dktk-connector"
ENV feature_BBMRI_DIRECTORY_SYNC="false"
ENV feature_DKTK_CENTRAL_SEARCH="false"
ENV feature_NNGM_CTS="false"
ENV LOG_LEVEL="info"
ENV MDR_URL="https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr"
ENV POSTGRES_HOST="bridgehead_dktk_connector_db"
ENV PROTOCOL="http"
ENV QUERY_LANGUAGE="QUERY"

# This stage is build when defining no target
FROM docker-build as vanilla
