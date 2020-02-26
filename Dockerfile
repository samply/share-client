## --build-arg PROJECT=dktk
FROM tomcat:9.0.27-jdk8-openjdk

RUN ["rm", "-fr", "/usr/local/tomcat/webapps"]

ARG PROJECT=dktk

ENV CONTEXT=$PROJECT
ENV SOURCE_DIR src/docker
ENV SOURCE_PROJECT_DIR $SOURCE_DIR/$PROJECT
ENV SOURCE_COMMON_DIR $SOURCE_DIR/common

ENV PROJECT_DIR /$PROJECT
ENV TOMCAT_DIR /usr/local/tomcat
ENV TOMCAT_CONF_DIR $TOMCAT_DIR/conf
ENV REPORTS_DIR /etc/$PROJECT/reports
ENV CATALINA_DIR $TOMCAT_CONF_DIR/Catalina/localhost
ENV JMX_EXPORTER_VERSION 0.3.1


ADD target/connector.war                               $TOMCAT_DIR/webapps/ROOT.war

ADD $SOURCE_PROJECT_DIR                                $TOMCAT_CONF_DIR

ADD $SOURCE_COMMON_DIR/context.xml                     $CATALINA_DIR/ROOT.xml
ADD $SOURCE_COMMON_DIR/mailSending.xml                 $TOMCAT_CONF_DIR/
ADD $SOURCE_COMMON_DIR/log4j2.xml                      $TOMCAT_CONF_DIR/
ADD $SOURCE_COMMON_DIR/dktkmds-db-key-public.der       $TOMCAT_CONF_DIR/
ADD $SOURCE_COMMON_DIR/tomcat.jks                      $TOMCAT_CONF_DIR/

RUN apt-get update && apt-get install -y dos2unix
RUN for file in $TOMCAT_CONF_DIR/*.xml ; do dos2unix $file $file ; done

ADD $SOURCE_COMMON_DIR/quality-report-statistics.txt   $REPORTS_DIR/
ADD $SOURCE_COMMON_DIR/quality-report-info.xlsx        $REPORTS_DIR/

# JMX Exporter

COPY $SOURCE_COMMON_DIR/jmx-exporter.yml                $PROJECT_DIR/jmx-exporter.yml
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/$JMX_EXPORTER_VERSION/jmx_prometheus_javaagent-$JMX_EXPORTER_VERSION.jar $PROJECT_DIR

ADD $SOURCE_COMMON_DIR/start.sh                         $PROJECT_DIR/

RUN chmod +x                                            $PROJECT_DIR/start.sh
RUN echo $PROJECT_DIR
CMD ["sh", "-c", "$PROJECT_DIR/start.sh"]
