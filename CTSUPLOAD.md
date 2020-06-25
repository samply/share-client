# Uploading a FHIR-Bundle to the CTS

The Connector provides an API that allows a user to upload a FHIR-Bundle file
to the CTS.

This is basically a two-step process: first, you upload the file to the
Connector's API. If that is successful, the file will then be automatically
uploaded from the Connector to the CTS.

## Configuration

Some configuration will probably be required in order to be able to successfully
perform uploads to CTS. The following elements can be configured via an XML file:

- Login credentials for the stored CTS user.
- The CTS upload URL.
- A profile for pseudonymized patients uploaded to the CTS.
- A URL for the pseudonymization API.
- An API key for the pseudonymization API.

The relevant file can be found here:


```
src/main/resources/dktk_cts_info.xml
```

Typically, it will look something like this:


```
<com:cts xmlns:com="http://schema.samply.de/config/CtsInfo">
    <com:username>admin</com:username>
    <com:password>admin</com:password>
    <com:url>https://nngm-qat.staging.healex.systems/trial/nNGMv09ccf/import-fhir</com:url>
    <com:profile>http://uk-koeln.de/fhir/StructureDefinition/Patient/nNGM/pseudonymisiert</com:profile>
    <com:mainzellisteUrl>https://test.verbis.dkfz.de/mpl</com:mainzellisteUrl>
    <com:mainzellisteApiKey>nngmTestKey?[8574]</com:mainzellisteApiKey>
</com:cts>
```

The "mainzellisteUrl" and "mainzellisteApiKey" should actually correspond to
the URL and key of a running MAGICPL instance.

## Building

Maven is required to build the Connector's WAR file:


```
mvn clean flyway:clean flyway:migrate jooq-codegen:generate install -P dktk
```

If you want to run the connector under Docker (recommended), you can do
it something like this:

```
docker build -t cts_upload_login/samply.share.client.v2 --no-cache .
```

The argument after the -t flag is the name of the Docker image that woull be built.
You may wish to customize this.

## Accessing the Connector Upload-API

This is accessed via the "/rest/postCTS" URL.

The following are required for a successful upload:

- Method: POST.
- Basic authentification. You will need to apply the username and password for logging
  into the Connector.
- Header: Content-Type=application/xml OR Content-Type=application/json, depending
  on whether your Bundle is in XML or JSON format.
- Body: the file to upload (mode: raw).

## Notes on uploaded data

The file should be a FHIR documentation Bundle, in either XML or JSON format.
This type of Bundle always has a Composition as its first entry.
 
Before transmitting the file to the CTS, the Connector will pseudonymize any patient
data it contains. For this to work, you need to have access to a running MAGICPL
instance. You can either set this up locally (e.g. using one of the Docker images
generously provided by VerbIS) or point to a public instance of the service.
