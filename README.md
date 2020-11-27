# Samply Share Client

Samply Share Client (also referenced as "Teiler" in certain contexts) is part of a bridgehead
infrastructure. It poses as the interface between the local components (mainly, the local 
datamanagement) and central components like searchbrokers for the decentral search infrastructure 

# Features

- Connect to any amount of search brokers to participate in the decentral search
- Generate quality reports to monitor data quality of your local datasets
- Upload a defined set of attributes (**M**elde**d**aten**s**atz) to a central database

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