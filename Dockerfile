FROM tomcat:8.5.32-jre8-alpine

ADD target/connector /usr/local/tomcat/webapps/ROOT/

ADD src/docker/context.xml /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
ADD src/docker/context.xml /usr/local/tomcat/webapps/ROOT/META-INF/context.xml
ADD src/docker/server.xml /usr/local/tomcat/conf

ADD src/docker/samply_common_urls.xml /root/.config/samply/
ADD src/docker/samply_common_operator.xml /root/.config/samply/
ADD src/docker/samply_common_config.xml /root/.config/samply/
ADD src/docker/samply_bridgehead_info.xml /root/.config/samply/
ADD src/docker/mailSending.xml /root/.config/samply/

# JMX Exporter
ENV JMX_EXPORTER_VERSION 0.3.1
COPY src/docker/jmx-exporter.yml /samply/jmx-exporter.yml
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/$JMX_EXPORTER_VERSION/jmx_prometheus_javaagent-$JMX_EXPORTER_VERSION.jar /samply/

ADD src/docker/start.sh /samply/
RUN chmod +x /samply/start.sh

CMD ["/samply/start.sh"]