[![Docker Pulls](https://img.shields.io/docker/pulls/akiel/samply.connector.svg)](https://hub.docker.com/r/akiel/samply.connector/)
[![Image Layers](https://images.microbadger.com/badges/image/akiel/samply.connector.svg)](https://microbadger.com/images/akiel/samply.connector)

# Docker Samply.Connector

A Samply.Connector Docker image based on [tomcat:8.5.32-jre8-alpine][1]. The Samply.Connector is configured alike that one from the windows installer. The Postgres database has to be supplied in another Docker container. The connection settings are given by environment variables which are documented below.

## Environment

* POSTGRES_HOST - the host name of the Postgres DB
* POSTGRES_PORT - the port of the Postgres DB, defaults to `5432`
* POSTGRES_DB - the database name, defaults to `samplyconnector`
* POSTGRES_USER - the database username, defaults to `samplyconnector`
* POSTGRES_PASS - the database password
* STORE_URL - the URL of the store to connect to
* MDR_URL - the URL of the mdr to connect to
* CATALINA_OPTS - JVM options for Tomcat like `-Xmx8g`
* ENABLE_METRICS - `true` to enable metrics (see below), defaults to `false`
* OPERATOR_FIRST_NAME - the IT staff which runs the connector
* OPERATOR_LAST_NAME - the IT staff which runs the connector
* OPERATOR_EMAIL - the IT staff which runs the connector
* OPERATOR_PHONE - the IT staff which runs the connector
* HTTP_PROXY - the URL of the HTTP proxy to use for outgoing connections; enables proxy usage if set
* PROXY_USER - the user of the proxy account (optional)
* PROXY_PASS - the password of the proxy account (optional)
* MAIL_HOST - mail host which is able to send mails, defaults to ``
* MAIL_PORT - port to mail host, defaults to ``
* MAIL_PROTOCOL - protocol for mail, defaults to ``
* MAIL_FROM_ADDRESS - mail address from which mails are sent, defaults to ``
* MAIL_FROM_NAME - subject of mails, defaults to ``

### Proxy

You can configure your proxy in `~/.docker/config.json` as described [here][4].

## Usage

```sh
docker run -p 8080:8080 -e POSTGRES_HOST=<host> -e POSTGRES_PASS=<password> akiel/samply.connector:latest
```

Open the following URL in a Browser:

```
http://localhost:8080/gba-connector/login.xhtml
```

You should see the login page. The default username and password is `admin` and `adminpass`.

You can register your Connector with the Search Broker under the following address: `https://search.germanbiobanknode.de/broker`.

## Metrics

The Docker image contains an [agent][3] which exports various metrics of the JVM like memory statistics in a text format on port `9100`. After enabling metrics by setting `ENABLE_METRICS` to `true` and exporting port `9100`, the following command shows the metrics:

```sh
curl http://localhost:9100/metrics
```

The metrics should be polled by a [Prometheus][2] instance.

[1]: <https://hub.docker.com/_/tomcat/>
[2]: <https://prometheus.io>
[3]: <https://github.com/prometheus/jmx_exporter>
[4]: <https://docs.docker.com/network/proxy/>
