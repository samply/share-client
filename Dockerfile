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
ENV POSTGRES_DB="share_v2"
ENV POSTGRES_USER="samplyweb"
ENV PROTOCOL="http"
ENV PORT="8080"
ENV DEPLOYMENT_CONTEXT="dktk-connector"
ENV FEATURE_BBMRI_DIRECTORY_SYNC="false"
ENV FEATURE_DKTK_CENTRAL_SEARCH="false"
ENV FEATURE_NNGM_CTS="false"
ENV LOG_LEVEL="info"
ENV MDR_URL="https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr"
ENV POSTGRES_HOST="bridgehead-dktk-connector-db"
ENV QUERY_LANGUAGE="QUERY"
ENV PATIENTLIST_URL="http://bridgehead-patientlist:8080/Patientlist"
ENV PROJECTPSEUDONYMISATION_URL="http://bridgehead-id-manager:8080/html/projectSelection.html"
ENV http_proxy="http://bridgehead-squid:3128"
ENV https_proxy="http://bridgehead-squid:3128"
ENV HTTP_PROXY="http://bridgehead-squid:3128"
ENV HTTPS_PROXY="http://bridgehead-squid:3128"
ENV ID_MANAGER_URL="http://bridgehead-id-manager:8080"

# Stage used by ci for gbn images (--target=gbn)
FROM docker-build as gbn
ENV TEST_PROJECT="gbn"
ENV CONNECTOR_ENABLE_METRICS="false"
ENV POSTGRES_DB="samply.connector"
ENV POSTGRES_HOST="bridgehead-gbn-connector-db"
ENV POSTGRES_USER="samply"
ENV QUERY_LANGUAGE="CQL"
ENV DEPLOYMENT_CONTEXT="gbn-connector"
ENV LOG_LEVEL="info"
ENV STORE_URL="http://bridgehead-gbn-blaze-store:8080"
ENV MDR_URL="https://mdr.germanbiobanknode.de/v3/api/mdr"
ENV PROTOCOL="http"
ENV PORT="8080"
ENV http_proxy="http://bridgehea-squid:3128"
ENV https_proxy="http://bridgehead-squid:3128"
ENV HTTP_PROXY="http://bridgehead-squid:3128"
ENV HTTPS_PROXY="http://bridgehead-squid:3128"

# Stage used by ci for c4 images (--target=c4)
FROM docker-build as c4
ENV TEST_PROJECT="c4"
ENV POSTGRES_PORT="5432"
ENV CCP_CENTRALSEARCH_URL="https://centralsearch-test.dktk.dkfz.de/"
ENV CCP_DECENTRALSEARCH_URL="https://decentralsearch-test.ccp-it.dktk.dkfz.de/"
ENV CONNECTOR_ENABLE_METRICS="false"
ENV POSTGRES_DB="share_v2"
ENV POSTGRES_USER="samplyweb"
ENV CONNECTOR_SHARE_URL="${PROTOCOL}://${HOST}:${PORT}"
ENV DEPLOYMENT_CONTEXT="dktk-connector"
ENV FEATURE_BBMRI_DIRECTORY_SYNC="false"
ENV FEATURE_DKTK_CENTRAL_SEARCH="false"
ENV FEATURE_NNGM_CTS="false"
ENV LOG_LEVEL="info"
ENV MDR_URL="https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr"
ENV POSTGRES_HOST="bridgehead-dktk-connector-db"
ENV PROTOCOL="http"
ENV QUERY_LANGUAGE="QUERY"
ENV http_proxy="http://bridgehead-squid:3128"
ENV https_proxy="http://bridgehead-squid:3128"
ENV HTTP_PROXY="http://bridgehead-squid:3128"
ENV HTTPS_PROXY="http://bridgehead-squid:3128"

FROM docker-build as nngm
ENV TEST_PROJECT="nngm"
ENV POSTGRES_PORT="5432"
ENV CONNECTOR_ENABLE_METRICS="false"
ENV POSTGRES_DB="share_v2"
ENV POSTGRES_USER="samplyweb"
ENV CONNECTOR_SHARE_URL="${PROTOCOL}://${HOST}:${PORT}"
ENV DEPLOYMENT_CONTEXT="nngm-connector"
ENV FEATURE_BBMRI_DIRECTORY_SYNC="false"
ENV FEATURE_DKTK_CENTRAL_SEARCH="false"
ENV FEATURE_NNGM_CTS="true"
ENV FEATURE_NNGM_ENCRYPT_ID="false"
ENV LOG_LEVEL="info"
ENV POSTGRES_HOST="bridgehead-nngm-connector-db"
ENV PROTOCOL="http"
ENV QUERY_LANGUAGE="CQL"
ENV http_proxy="http://bridgehead-squid:3128"
ENV https_proxy="http://bridgehead-squid:3128"
ENV HTTP_PROXY="http://bridgehead-squid:3128"
ENV HTTPS_PROXY="http://bridgehead-squid:3128"
ENV NNGM_CTS_URL="https://nngm-test.medicalsyn.com/api/v1.0/hl7/patient"
ENV NNGM_PROFILE="http://uk-koeln.de/fhir/StructureDefinition/Patient/nNGM/pseudonymisiert"
ENV NNGM_MAGICPL_URL="https://test.verbis.dkfz.de/nngm/magicpl"
ENV NNGM_MAINZELLISTE_URL="https://test.verbis.dkfz.de/nngm/mainzelliste"


# This stage is build when defining no target
FROM docker-build as vanilla
