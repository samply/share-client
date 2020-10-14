#!/usr/bin/env bash

set -e

sed -i "s/{postgres-host}/${POSTGRES_HOST}/"              /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-port}/${POSTGRES_PORT:-5432}/"        /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-db}/${POSTGRES_DB}/"                  /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-user}/${POSTGRES_USER}/"              /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-pass}/${POSTGRES_PASS}/"              /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml

sed -i "s~{proxy-url}~${HTTP_PROXY}~"                     /usr/local/tomcat/conf/samply_common_config.xml
sed -i "s/{proxy-user}/${PROXY_USER}/"                    /usr/local/tomcat/conf/samply_common_config.xml
sed -i "s/{proxy-pass}/${PROXY_PASS}/"                    /usr/local/tomcat/conf/samply_common_config.xml

sed -i "s#{store-url}#${STORE_URL}#"                      /usr/local/tomcat/conf/samply_common_urls.xml
sed -i "s#{mdr-url}#${MDR_URL}#"                          /usr/local/tomcat/conf/samply_common_urls.xml
sed -i "s#{directory-url}#${DIRECTORY_URL}#"              /usr/local/tomcat/conf/samply_common_urls.xml

sed -i "s/{operator-first-name}/${OPERATOR_FIRST_NAME}/"  /usr/local/tomcat/conf/samply_common_operator.xml
sed -i "s/{operator-last-name}/${OPERATOR_LAST_NAME}/"    /usr/local/tomcat/conf/samply_common_operator.xml
sed -i "s/{operator-email}/${OPERATOR_EMAIL}/"            /usr/local/tomcat/conf/samply_common_operator.xml
sed -i "s#{operator-phone}#${OPERATOR_PHONE}#"            /usr/local/tomcat/conf/samply_common_operator.xml

sed -i "s%{mail-host}%${MAIL_HOST}%"                      /usr/local/tomcat/conf/mailSending.xml
sed -i "s%{mail-port}%${MAIL_PORT:-25}%"                  /usr/local/tomcat/conf/mailSending.xml
sed -i "s%{mail-protocol}%${MAIL_PROTOCOL:-smtp}%"        /usr/local/tomcat/conf/mailSending.xml
sed -i "s%{mail-from-address}%${MAIL_FROM_ADDRESS}%"      /usr/local/tomcat/conf/mailSending.xml
sed -i "s%{mail-from-name}%${MAIL_FROM_NAME}%"            /usr/local/tomcat/conf/mailSending.xml

sed -i "s#{query-language}#${QUERY_LANGUAGE:-QUERY}#"     /usr/local/tomcat/conf/samply_bridgehead_info.xml

sed -i "s/{level}/${LOG_LEVEL:-info}/"                    /usr/local/tomcat/conf/log4j2.xml

sed -i "s/{feature_BBMRI_DIRECTORY_SYNC}/${feature_BBMRI_DIRECTORY_SYNC:-false}/" /usr/local/tomcat/conf/features.properties.xml
sed -i "s/{feature_DKTK_CENTRAL_SEARCH}/${feature_DKTK_CENTRAL_SEARCH:-false}/"   /usr/local/tomcat/conf/features.properties.xml
sed -i "s/{feature_NNGM_CTS}/${feature_NNGM_CTS:-false}/"                         /usr/local/tomcat/conf/features.properties.xml

export CATALINA_OPTS="${CATALINA_OPTS} -javaagent:/samply/jmx_prometheus_javaagent-0.3.1.jar=9100:/samply/jmx-exporter.yml"

# Replace start.sh with catalina.sh
exec /usr/local/tomcat/bin/catalina.sh run
