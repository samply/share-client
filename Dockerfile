FROM tomcat:8.5.32-jre8-alpine

RUN apk update && apk add --no-cache curl

RUN ["rm", "-fr", "/usr/local/tomcat/webapps"]
ADD target/connector.war                        /usr/local/tomcat/webapps/ROOT.war

ADD src/docker/context.xml                      /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml

ADD src/docker/samply_common_urls.xml           /usr/local/tomcat/conf/
ADD src/docker/samply_common_operator.xml       /usr/local/tomcat/conf/
ADD src/docker/samply_common_config.xml         /usr/local/tomcat/conf/
ADD src/docker/samply_bridgehead_info.xml       /usr/local/tomcat/conf/
ADD src/docker/mailSending.xml                  /usr/local/tomcat/conf/
ADD src/docker/log4j2.xml                       /usr/local/tomcat/conf/

# JMX Exporter
ENV JMX_EXPORTER_VERSION 0.3.1
COPY src/docker/jmx-exporter.yml                /samply/jmx-exporter.yml
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/$JMX_EXPORTER_VERSION/jmx_prometheus_javaagent-$JMX_EXPORTER_VERSION.jar /samply/

ADD src/docker/start.sh                         /samply/
RUN chmod +x                                    /samply/start.sh
CMD ["/samply/start.sh"]
