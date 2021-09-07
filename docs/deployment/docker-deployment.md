### Docker

Use the Docker-Compose of the [GBA-Bridgehead](https://github.com/samply/bridgehead-deployment) and run only the Connector with:

```
docker-compose up connector
```

#### Or build and run manually:

If postgres connection errors occur, try your ip for POSTGRES_HOST. For all Environments, see `/src/docker/start.sh`

    docker network create gba
    
    
    docker rm pg-connector
    
    docker run \
        --name pg-connector \
        --network=gba \
        -e POSTGRES_USER=samply \
        -e POSTGRES_DB=samply.connector \
        -e POSTGRES_PASSWORD=samply \
        -p 5432:5432 \
    postgres:9.6
    
    
    docker rm connector
    
    docker build . -t connector:latest
    
    docker run \
        --name=connector \
        --network=gba \
        -p 8082:8080 \
        -e POSTGRES_HOST='pg-connector' \
        -e POSTGRES_DB='samply.connector' \
        -e POSTGRES_USER='samply' \
        -e POSTGRES_PASS='samply' \
        -e MDR_URL='https://mdr.germanbiobanknode.de/v3/api/mdr' \
        -e STORE_URL='http://store:8080' \
        -e QUERY_LANGUAGE='CQL' \
        -e CATALINA_OPTS='"-Xmx2g"' \
    connector:latest

## Environment Variables

| Name           | Default | Description                                                   |
| -------------- | ------- | ------------------------------------------------------------- |
| POSTGRES_HOST* |         | Base URI of Postgres                                          |
| POSTGRES_PORT  | *5432*  | Port of Postgres                                              |
| POSTGRES_DB*   |         | Database name in Postgres                                     |
| POSTGRES_USER* |         | Authorized username for database                              |
| POSTGRES_PASS* |         | Password of authorized user                                   |
| HTTP_PROXY_URL |         | Proxy server and port for outbound HTTP requests, e.g. "proxy.example.de:8080" |
| HTTP_PROXY_USERNAME |         | Username for authentication with proxy server on outbound HTTP requests, if authentication is required |
| HTTP_PROXY_PASSWORD |         | Password for authentication with proxy server on outbound HTTP requests, if authentication is required |
| HTTPS_PROXY_URL |         | Proxy server and port for outbound HTTPS requests, e.g. "proxy.example.de:8080" |
| HTTPS_PROXY_USERNAME |         | Username for authentication with proxy server on outbound HTTPS requests, if authentication is required |
| HTTPS_PROXY_PASSWORD |         | Password for authentication with proxy server on outbound HTTPS requests, if authentication is required |
| NO_PROXY_HOSTS |         | List of hosts, for that the proxy should be bypassed |
| PROXY_USER     |         | Proxy server user, if authentication is needed.               |
| PROXY_PASS     |         | Proxy server password, if authentication is needed.           |
| STORE_URL*     |         | The URL under which the Store is accessible by Connector      |
| QUERY_LANGUAGE | *QUERY* | `QUERY` for Classic Store, `CQL` for Blaze                    |
| MDR_URL*       |         | The URL under which the Metadata Repository is accessible     |
| DIRECTORY_URL  |         | The URL under which the BBMRI Directory is accessible         |
| OPERATOR_FIRST_NAME  |   | The first name from the connector admin                       |
| OPERATOR_LAST_NAME   |   | The last name from the connector admin                        |
| OPERATOR_EMAIL |         | The email from the connector admin                            |
| OPERATOR_PHONE |         | The phone number from the connector admin                     |
| MAIL_HOST      |         | The URL of the mail server                                    |
| MAIL_PORT      |    25   | The port of the mail server                                   |
| MAIL_PROTOCOL  |   smtp  | The protocol of the mail server                               |
| MAIL_FROM_ADDRESS |      | The email address that appears as sender in the email         |
| MAIL_FROM_NAME |         | The name that appears as sender in the email                  |
| LOG_LEVEL      |   info  | Log level of tomcat                                           |
| feature_BBMRI_DIRECTORY_SYNC  |   false | Feature toggle for the BBMRI directory sync    | 
| feature_DKTK_CENTRAL_SEARCH   |   false | Feature toggle for the DKTK central search     |
| feature_NNGM_CTS              |   false | Feature toggle for the NNGM CTS                | 
| CATALINA_OPTS  |         | JVM options                                                   |
| DEPLOYMENT_CONTEXT |          | The subpath of the bridgehead installation. Normally bridgehead will be deployed to ROOT, but with this variable you can change it for example to "connector". It is not possible to pass multiple subdirectories like "connector/samply".|

*necessary
