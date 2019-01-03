FROM tomcat:8.5.32-jre8-alpine

ADD target/connector /usr/local/tomcat/webapps/ROOT/

ADD src/docker/context.xml /usr/local/tomcat/conf/Catalina/localhost/gba-connector.xml
ADD src/docker/server.xml /usr/local/tomcat/conf

ADD src/docker/samply_common_urls.xml /root/.config/samply/
ADD src/docker/samply_common_operator.xml /root/.config/samply/
ADD src/docker/samply_common_config.xml /root/.config/samply/
ADD src/docker/samply_bridgehead_info.xml /root/.config/samply/
ADD src/docker/mailSending.xml /root/.config/samply/

ADD src/docker/start.sh /samply/
RUN chmod +x /samply/start.sh

CMD ["/samply/start.sh"]

