#mvn clean flyway:clean flyway:migrate jooq-codegen:generate install -P dktk
docker build -t cts_upload_login/samply.share.client.v2 --no-cache .
