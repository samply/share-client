# Getting started

## Cloning the repository

Choose or create a directory for the repository to get cloned into, such as your Desktop. This is the directory we'll be working out of, so pick somewhere convenient.

Using a CLI, run the following command in the directory: `git clone  https://github.com/samply/share-client.git`

Once the command has finished executing, you should have a copy of this repository named `share-client` inside your project directory.

## Setting up the environment

### Requirements

You will need [Intelj IDEA](https://www.jetbrains.com/idea/) (the community edition is freely available for download) and [Java SDK](https://www.oracle.com/java/technologies/javase-downloads.html) to build the project, aswell as [Maven](https://maven.apache.org/).

## Working in this repository

From now on, whenever you want to make a change in the repository, you will first need to branch off the master branch. Using a CLI in the `share-client` directory, first check if the master branch is fully up to date:

```
git checkout master
git fetch
git pull
```

Now you can create a new branch. Please use the `feature-` prefix so that it's clear that your branch is a temporary, in-progress development branch. Creating your branch can be done using one of two methods:

### CLI

Simply enter `git checkout -b feature-somenamehere` to have a branch created for you.

### GitHub for Desktop

In the application, go to ``Branch -> New branch...``. Give this an appropriate name (don't forget the prefix) and ensure that the branch is based on the `master` branch.


### Test your changes
After you are done with your changes, you need to test it locally before you create a new pull request.
This can be done using [Tomcat](#tomcat) or [Docker](#docker).

#### Tomcat
If you do not have [Tomcat](http://tomcat.apache.org/) please install the newest version of it.
Then do the following steps:
* Add Tomcat to the "Run/Debug Configurations" of Intellij
* In the "Server" tab you can choose the port of Tomcat (default 8080)
* Go to the "Deployment" tab and add the "share-client:war" artifact

#### Postgres
You also need [Postgres](https://www.postgresql.org/download/) for the manual version of deploying.
The connector uses jooq and flyway to create the database.

Depends on which maven profile you use, the configuration for the postgres can change.
In the [pom.xml](https://github.com/samply/share-client/blob/master/pom.xml) you can see the configurations for each profile.
Please create the user and the database in postgres according to the selected profile.
After you created the database run in Intellij the command `mvn flyway:clean flyway:migrate jooq-codegen:generate` to initialise the empty database.

After the configuration of Tomcat and Postgres run the maven command `mvn clean install` and run Tomcat in Intellij to deploy and run the connector with your changes.

#### Docker (Only works with Maven profile "samply")
If you want to use [Docker](https://www.docker.com/) for testing your changes you can do it by running the following commands.
* `mvn clean install` to create a WAR-file for the Docker image
* Run at the directory of the share-client project the following script to deploy a Postgres container and then a connector container:
    

        
    docker rm pg-connector
    
    docker run \
        --name pg-connector \
        -e POSTGRES_USER=samply \
        -e POSTGRES_DB=samply.connector \
        -e POSTGRES_PASSWORD=samply \
        -p 5432:5432 \
    postgres:9.6
    
    docker rm connector
    
    docker build . -t connector:latest
    
    docker run \
        --name=connector \
        -p 8082:8080 \
        -e POSTGRES_HOST='pg-connector' \
        -e POSTGRES_DB='samply.connector' \
        -e POSTGRES_USER='samply' \
        -e POSTGRES_PASS='samply' \
        -e MDR_URL='https://mdr.germanbiobanknode.de/v3/api/mdr' \
        -e STORE_URL='http://store:8080' \
        -e QUERY_LANGUAGE='CQL' \
        -e CATALINA_OPTS='"-Xmx2g"' \
    connector

### Pull Request
After you tested your changes you can create on Github a new pull request. Please merge new feature branches only into develop. 
