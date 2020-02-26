#!/usr/bin/env bash


export FILES_TO_PARSE="$CATALINA_DIR/ROOT.xml $TOMCAT_CONF_DIR/${CONTEXT}_common_config.xml $TOMCAT_CONF_DIR/${CONTEXT}_common_urls.xml $TOMCAT_CONF_DIR/${CONTEXT}_common_operator.xml $TOMCAT_CONF_DIR/mailSending.xml $TOMCAT_CONF_DIR/${CONTEXT}_bridgehead_info.xml"

set -e

for file in $FILES_TO_PARSE
do

sed -i "s/{site}/${SITE}/"                                $file

sed -i "s/{postgres-host}/${POSTGRES_HOST}/"              $file
sed -i "s/{postgres-port}/${POSTGRES_PORT:-5432}/"        $file
sed -i "s/{postgres-db}/${POSTGRES_DB}/"                  $file
sed -i "s/{postgres-user}/${POSTGRES_USER}/"              $file
sed -i "s/{postgres-pass}/${POSTGRES_PASSWORD}/"          $file

sed -i "s~{proxy-url}~${HTTP_PROXY}~"                     $file
sed -i "s/{proxy-user}/${PROXY_USER}/"                    $file
sed -i "s/{proxy-pass}/${PROXY_PASS}/"                    $file

sed -i "s/{operator-first-name}/${OPERATOR_FIRST_NAME}/"  $file
sed -i "s/{operator-last-name}/${OPERATOR_LAST_NAME}/"    $file
sed -i "s/{operator-email}/${OPERATOR_EMAIL}/"            $file
sed -i "s#{operator-phone}#${OPERATOR_PHONE}#"            $file

sed -i "s%{mail-host}%${MAIL_HOST}%"                      $file
sed -i "s%{mail-port}%${MAIL_PORT:-25}%"                  $file
sed -i "s%{mail-protocol}%${MAIL_PROTOCOL:-smtp}%"        $file
sed -i "s%{mail-from-address}%${MAIL_FROM_ADDRESS}%"      $file
sed -i "s%{mail-from-name}%${MAIL_FROM_NAME}%"            $file

sed -i "s#{query-language}#${QUERY_LANGUAGE:-QUERY}#"     $file

sed -i "s/{operator-first-name}/${OPERATOR_FIRST_NAME}/"  $file
sed -i "s/{operator-last-name}/${OPERATOR_LAST_NAME}/"    $file

sed -i "s/{update-server}/${UPDATE_SERVER}/"              $file
sed -i "s/{monitor-interval}/${MONITOR_INTERVAL}/"        $file
sed -i "s/{monitor-url}/${MONITOR_URL}/"                  $file

sed -i "s~{share-url}~${SHARE_URL}~"                      $file
sed -i "s~{id-manager-url}~${ID_MANAGER_URL}~"            $file
sed -i "s~{central-search}~${CENTRAL_SEARCH}~"            $file
sed -i "s~{decentral-search}~${DECENTRAL_SEARCH}~"        $file
sed -i "s~{ldm-url}~${LDM_URL}~"                          $file
sed -i "s~{mdr-url}~${MDR_URL}~"                          $file


done


# Replace start.sh with catalina.sh
exec /usr/local/tomcat/bin/catalina.sh run
