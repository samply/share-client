[![Docker Pulls](https://img.shields.io/docker/pulls/bbmride/samply-connector.svg)](https://hub.docker.com/r/bbmride/samply-connector/)
[![CircleCI](https://circleci.com/gh/bbmride/samply-connector/tree/master.svg?style=svg)](https://circleci.com/gh/bbmride/samply-connector/tree/master)

# Connector

You can access the Connector under http://localhost:8082 and login to it under http://localhost:8082/login.xhtml The login credentials are **admin**, **adminpass**.

Add a Searchbroker to get and answer queries at http://localhost:8082/admin/broker_list.xhtml 
* Broker Adresse = https://search.germanbiobanknode.de/broker/
* Ihre Email Adresse = your email address to get the API-Key for registration
* Automatisch antworten = Nur Anzahl (default, so you answer automatically with number of samples)

You will receive an email with API-Key from Searchbroker Backend, paste these eight numbers and press "ok". Call an ITC to validate your request.

Create a new user at http://localhost:8082/admin/user_list.xhtml

Logout and login as normal user to see all handled queries.











A Samply.Connector Docker image based on [tomcat:8.5.32-jre8-alpine][1]. The Samply.Connector is configured alike that one from the windows installer. The Postgres database has to be supplied in another Docker container. The connection settings are given by environment variables which are documented below.

## Environment

* POSTGRES_HOST - the host name of the Postgres DB
* POSTGRES_PORT - the port of the Postgres DB, defaults to `5432`
* POSTGRES_DB - the database name, defaults to `samply`
* POSTGRES_USER - the database username, defaults to `samply`
* POSTGRES_PASS - the database password, defaults to `samply`
* STORE_URL - the URL of the store to connect to
* MDR_URL - the URL of the mdr to connect to
* CATALINA_OPTS - JVM options for Tomcat like `-Xmx8g`
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




# Build

In order to build this project, you need to configure maven properly and use the maven profile that
fits to your project.

During the build process, the jooq plugin will update the database-related files according to the database
configured in _pom.xml_. So you have to create the necessary tables and relations first. This
can be done via the flyway plugin.

```
mvn flyway:migrate
```

Make sure to generate the Manifest file before, so that the project context will be set. Do so with

``` 
mvn war:manifest -P<projectname>
```

Then, to build the war:

``` 
mvn clean package -P<projectname>
```

# Configuration

To configure your instance of Samply Share Client, you have to take care of the following settings.
The necessary tables, sequences and types will be created upon startup, given the configured user (see
below in _context.xml_) has the respecitve privileges.

### Database Connection

The database connection uses a connection pool, for which the datasource is defined in
 _src/main/webapp/META-INF/context.xml_ and _src/main/webapp/WEB-INF/web.xml_. Samply Share Client uses 
its own database schema, named "samply".

Your context.xml should resemble

```
<?xml version="1.0" encoding="UTF-8"?>
<Context path="/">
    <Resource name="jdbc/postgres" auth="Container"
              type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
              url="jdbc:postgresql://<database_url>/<database_name>"
              username="<username>" password="<password>" maxActive="50" maxIdle="20"
              maxWait="-1" />
</Context>
```

You have to set the parameters "database_url", "database_name", "username" and "password" according to
your setup

Your web.xml has to contain the following snippet
```
<!--Datasource definition-->
<resource-ref>
    <description>postgreSQL Datasource</description>
    <res-ref-name>jdbc/postgres</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
</resource-ref>
```
where the element "res-ref-name" has to be the exact same as the "name" Attribute from context.xml

### Network Settings

Network settings (i.e. proxy settings) are read from _<project-name>-common-config.xml_.

### URLs to other interfaces

#### Local Components

The URLs to other local components (ID-Management, Local Datamanagement) are read from 
_<project-name>\_common\_urls.xml_.

#### Central Components

The connections to central components (Central MDS-Database, MDR, Decentral Searchbrokers) are 
stored in the database. Some of them are read-only in the UI since they should rarely (or never) be
changed. You have to change them directly in the database.

# Jobs / Tasks / Triggers

Samply.Share.Client uses [Quartz](http://www.quartz-scheduler.org) to schedule Jobs. However,
an actual task (e.g. "execute an inquiry") consists of multiple jobs that are chained (in this case:
post inquiry to local datamanagement, check status, if necessary post again...).

All Jobs are defined in _/src/main/resources/quartz-jobs.xml_ without any triggers. Thus, all jobs have to be
declared as _durable_, to make sure the scheduler knows them. For jobs that run "on their own"
(e.g. inquiry collection), the cron expression has to be set in the database (table _job_schedule_).
To make the cron-expression changeable by the admin, set the "SHOW" entry of the job data map to true (like it is already 
done for some jobs in the xml file).
 
For jobs that are spawned by other means (e.g. jobs or user interaction), the triggers are set programmatically.

To learn more about jobs and triggers in quartz, check
 [this tutorial page](https://www.quartz-scheduler.net/documentation/quartz-2.x/tutorial/jobs-and-triggers.html)





