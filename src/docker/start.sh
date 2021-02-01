#!/usr/bin/env bash

set -e

sed -i "s/{postgres-host}/${POSTGRES_HOST}/"              ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-port}/${POSTGRES_PORT:-5432}/"        ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-db}/${POSTGRES_DB}/"                  ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-user}/${POSTGRES_USER}/"              ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml
sed -i "s/{postgres-pass}/${POSTGRES_PASS}/"              ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml

sed -i "s~{proxy-url}~${HTTP_PROXY}~"                     ${CATALINA_HOME}/conf/${PROJECT}_common_config.xml
sed -i "s/{proxy-user}/${PROXY_USER}/"                    ${CATALINA_HOME}/conf/${PROJECT}_common_config.xml
sed -i "s/{proxy-pass}/${PROXY_PASS}/"                    ${CATALINA_HOME}/conf/${PROJECT}_common_config.xml

sed -i "s#{store-url}#${STORE_URL}#"                      ${CATALINA_HOME}/conf/${PROJECT}_common_urls.xml
sed -i "s#{id-manager-url}#${ID_MANAGER_URL}#"            ${CATALINA_HOME}/conf/${PROJECT}_common_urls.xml
sed -i "s#{mdr-url}#${MDR_URL}#"                          ${CATALINA_HOME}/conf/${PROJECT}_common_urls.xml
sed -i "s#{directory-url}#${DIRECTORY_URL}#"              ${CATALINA_HOME}/conf/${PROJECT}_common_urls.xml
sed -i "s#{share-url}#${SHARE_URL}#"                      ${CATALINA_HOME}/conf/${PROJECT}_common_urls.xml

sed -i "s/{operator-first-name}/${OPERATOR_FIRST_NAME}/"  ${CATALINA_HOME}/conf/${PROJECT}_common_operator.xml
sed -i "s/{operator-last-name}/${OPERATOR_LAST_NAME}/"    ${CATALINA_HOME}/conf/${PROJECT}_common_operator.xml
sed -i "s/{operator-email}/${OPERATOR_EMAIL}/"            ${CATALINA_HOME}/conf/${PROJECT}_common_operator.xml
sed -i "s#{operator-phone}#${OPERATOR_PHONE}#"            ${CATALINA_HOME}/conf/${PROJECT}_common_operator.xml

sed -i "s%{mail-host}%${MAIL_HOST}%"                      ${CATALINA_HOME}/conf/mailSending.xml
sed -i "s%{mail-port}%${MAIL_PORT:-25}%"                  ${CATALINA_HOME}/conf/mailSending.xml
sed -i "s%{mail-protocol}%${MAIL_PROTOCOL:-smtp}%"        ${CATALINA_HOME}/conf/mailSending.xml
sed -i "s%{mail-from-address}%${MAIL_FROM_ADDRESS}%"      ${CATALINA_HOME}/conf/mailSending.xml
sed -i "s%{mail-from-name}%${MAIL_FROM_NAME}%"            ${CATALINA_HOME}/conf/mailSending.xml

file=${CATALINA_HOME}/conf/${PROJECT}_bridgehead_info.xml
sed -i "s#{site}#${SITE}#"                                "$file"
sed -i "s#{siteid}#${SITEID}#"                            "$file"
sed -i "s#{query-language}#${QUERY_LANGUAGE:-QUERY}#"     "$file"
sed -i "s#{central-search}#${CENTRAL_SEARCH}#"            "$file"
sed -i "s#{decentral-search}#${DECENTRAL_SEARCH}#"        "$file"
sed -i "s#{query-language}#${QUERY_LANGUAGE:-QUERY}#"     "$file"
sed -i "s#{monitor-interval}#${MONITOR_INTERVAL}#"        "$file"
sed -i "s#{monitor-url}#${MONITOR_URL}#"                  "$file"
sed -i "s#{update-server}#${UPDATE_SERVER}#"              "$file"

sed -i "s/{level}/${LOG_LEVEL:-info}/"                    ${CATALINA_HOME}/conf/log4j2.xml

sed -i "s/{feature_BBMRI_DIRECTORY_SYNC}/${feature_BBMRI_DIRECTORY_SYNC:-false}/" ${CATALINA_HOME}/conf/features.properties
sed -i "s/{feature_DKTK_CENTRAL_SEARCH}/${feature_DKTK_CENTRAL_SEARCH:-false}/"   ${CATALINA_HOME}/conf/features.properties
sed -i "s/{feature_NNGM_CTS}/${feature_NNGM_CTS:-false}/"                         ${CATALINA_HOME}/conf/features.properties

export CATALINA_OPTS="${CATALINA_OPTS} -javaagent:/samply/jmx_prometheus_javaagent-0.3.1.jar=9100:/samply/jmx-exporter.yml"

# Replace start.sh with catalina.sh
exec /usr/local/tomcat/bin/catalina.sh run
