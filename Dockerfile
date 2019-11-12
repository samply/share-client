#FROM tomcat:8.5.32-jre8-alpine
FROM tomcat:9.0.27-jdk8-openjdk

RUN ["rm", "-fr", "/usr/local/tomcat/webapps"]
RUN mkdir /usr/local/tomcat/reports
ADD target/connector.war                        /usr/local/tomcat/webapps/ROOT.war

ADD src/docker/context.xml                      /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml

ADD src/docker/dktk_common_urls.xml           /usr/local/tomcat/conf/
ADD src/docker/dktk_common_operator.xml       /usr/local/tomcat/conf/
ADD src/docker/dktk_common_config.xml         /usr/local/tomcat/conf/
ADD src/docker/dktk_bridgehead_info.xml       /usr/local/tomcat/conf/

#ADD src/docker/samply_common_urls.xml           /usr/local/tomcat/conf/
#ADD src/docker/samply_common_operator.xml       /usr/local/tomcat/conf/
#ADD src/docker/samply_common_config.xml         /usr/local/tomcat/conf/
#ADD src/docker/samply_bridgehead_info.xml       /usr/local/tomcat/conf/
ADD src/docker/mailSending.xml                  /usr/local/tomcat/conf/
ADD src/docker/log4j2.xml                       /usr/local/tomcat/conf/
ADD src/docker/dktkmds-db-key-public.der        /usr/local/tomcat/conf/
ADD src/docker/tomcat.jks                       /usr/local/tomcat/conf/

ADD src/docker/quality-report-statistics.txt   /etc/dktk/reports/
ADD src/docker/quality-report-info.xlsx        /etc/dktk/reports/

# JMX Exporter
ENV JMX_EXPORTER_VERSION 0.3.1
COPY src/docker/jmx-exporter.yml                /dktk/jmx-exporter.yml
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/$JMX_EXPORTER_VERSION/jmx_prometheus_javaagent-$JMX_EXPORTER_VERSION.jar /dktk/

ADD src/docker/start.sh                         /dktk/
RUN chmod +x                                    /dktk/start.sh
CMD ["/dktk/start.sh"]