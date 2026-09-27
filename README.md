# file_storage
This is a file storage API for video files up to 100 MiB, persisted in an H2 database. It uses Java 21 and Spring Boot 3.5.

## Run locally

Install a Java 21 JDK.

1. `chmod +x start.sh` (first time only)
2. `./start.sh`

Windows:
1. `cd filestorage`
2. `mvnw.cmd spring-boot:run`

API documentation is available at `http://localhost:8080/swagger-ui.html`, with the OpenAPI document at `http://localhost:8080/v3/api-docs`.

## Test

1. `cd filestorage`
2. `./mvnw test` (Windows: `mvnw.cmd test`)
