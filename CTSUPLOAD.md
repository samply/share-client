# Uploading a FHIR-Bundle to the CTS

The Connector provides an API that allows a user to upload a FHIR-Bundle file
to the CTS.

This is basically a two-step process: first, you upload the file to the
Connector's API. If that is successful, the file will then be automatically
uploaded from the Connector to the CTS.

## Configuration

The following needs to be configured before building the Connector:

- A URL for the CTS file upload API.
- Credentials information for CTS login.

The URL should be entered into the <com:ctsUrl> element of the following file:

```
src/main/resources/dktk_common_urls.xml
```

E.g.:

```
<com:ctsUrl>https://nngm-qat.staging.healex.systems/trial/nNGMv09ccf/import-fhir</com:ctsUrl>
```

Credentials need to be added to the file:

```
src/main/resources/cts_credentials.properties
```

E.g., if the CTS username is "admin", with a password "admin", then you would add:


```
cts.credentials.username=admin
cts.credentials.password=admin
```

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
- Media type.  This should be set in the "Content-Type" header, which takes the value
  "application/xml" or "application/json", depending on the bundle, which may be in
  either XML or JSON.

## Notes on uploaded data

The file should be a FHIR documentation Bundle, in either XML or JSON format.
This type of Bundle always has a Composition as its first entry.
 
Before transmitting the file to the CTS, the Connector will pseudonymize any patient
data it contains. If you have set up a Mainzelliste to work together with the Connector,
this will be used for the pseudonymization, which means that record linkage will be
possible, even though detailed IDATs will not be available to the CTS. If you don't
have a Mainzelliste, then pseudo-random pseudonyms will be used. In this case, no
record linkage will be possible.
