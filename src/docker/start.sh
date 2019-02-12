#!/usr/bin/env bash

set -e

sed -i "s/{postgres-host}/${POSTGRES_HOST}/" /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-port}/${POSTGRES_PORT}/" /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-db}/${POSTGRES_DB}/" /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-user}/${POSTGRES_USER}/" /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-pass}/${POSTGRES_PASS}/" /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml

sed -i "s/{postgres-host}/${POSTGRES_HOST}/" /usr/local/tomcat/webapps/ROOT/META-INF/context.xml
sed -i "s/{postgres-port}/${POSTGRES_PORT}/" /usr/local/tomcat/webapps/ROOT/META-INF/context.xml
sed -i "s/{postgres-db}/${POSTGRES_DB}/" /usr/local/tomcat/webapps/ROOT/META-INF/context.xml
sed -i "s/{postgres-user}/${POSTGRES_USER}/" /usr/local/tomcat/webapps/ROOT/META-INF/context.xml
sed -i "s/{postgres-pass}/${POSTGRES_PASS}/" /usr/local/tomcat/webapps/ROOT/META-INF/context.xml

sed -i "s~{proxy-url}~${HTTP_PROXY}~" /root/.config/samply/samply_common_config.xml
sed -i "s/{proxy-user}/${PROXY_USER}/" /root/.config/samply/samply_common_config.xml
sed -i "s/{proxy-pass}/${PROXY_PASS}/" /root/.config/samply/samply_common_config.xml

sed -i "s#{store-url}#${STORE_URL}#" /root/.config/samply/samply_common_urls.xml
sed -i "s#{mdr-url}#${MDR_URL}#" /root/.config/samply/samply_common_urls.xml

sed -i "s/{operator-first-name}/${OPERATOR_FIRST_NAME}/" /root/.config/samply/samply_common_operator.xml
sed -i "s/{operator-last-name}/${OPERATOR_LAST_NAME}/" /root/.config/samply/samply_common_operator.xml
sed -i "s/{operator-email}/${OPERATOR_EMAIL}/" /root/.config/samply/samply_common_operator.xml
sed -i "s#{operator-phone}#${OPERATOR_PHONE}#" /root/.config/samply/samply_common_operator.xml

sed -i "s%{mail-host}%${MAIL_HOST}%" /root/.config/samply/mailSending.xml
sed -i "s%{mail-port}%${MAIL_PORT}%" /root/.config/samply/mailSending.xml
sed -i "s%{mail-protocol}%${MAIL_PROTOCOL}%" /root/.config/samply/mailSending.xml
sed -i "s%{mail-from-address}%${MAIL_FROM_ADDRESS}%" /root/.config/samply/mailSending.xml
sed -i "s%{mail-from-name}%${MAIL_FROM_NAME}%" /root/.config/samply/mailSending.xml

export CATALINA_OPTS="${CATALINA_OPTS} -javaagent:/samply/jmx_prometheus_javaagent-0.3.1.jar=9100:/samply/jmx-exporter.yml"

# Replace start.sh with catalina.sh
exec /usr/local/tomcat/bin/catalina.sh run
