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
COPY src/docker/jmx_prometheus_javaagent-0.3.1.jar /samply/jmx_prometheus_javaagent-0.3.1.jar

ADD src/docker/start.sh                         /samply/
RUN chmod +x                                    /samply/start.sh

EXPOSE 8080
EXPOSE 8009

CMD ["/samply/start.sh"]
