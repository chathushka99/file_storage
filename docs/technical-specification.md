# Technical Specification - File Storage

## 1. Overview
The File Storage module exposes a REST API for storing and retrieving video files in an H2 database.

## 2. Architecture
The application uses a controller, service, Spring Data JPA repository, and MapStruct mapper. File uploads are validated before the service persists entity data; controllers expose response DTOs and binary file content rather than JPA entities.

## 3. Technology Stack
Java 21, Spring Boot 3.5, Spring MVC, Spring Data JPA, Hibernate, H2, MapStruct, springdoc-openapi, Maven, and JUnit 5.

## 4. Data Model
`FileEntity` stores a UUID string identifier, original filename, media type, byte size, file bytes, and creation/update timestamps. A unique constraint on filename, media type, and size prevents duplicate entries with the same metadata.

## 5. API Contracts
- `POST /v1/files` accepts a multipart part named `data` and returns `201 Created` with a download URL in the `Location` header.
- `GET /v1/files` returns file metadata and accepts optional paired `page` and `size` query parameters.
- `GET /v1/files/{fileid}` returns the file bytes with its media type and attachment filename.
- `DELETE /v1/files/{fileid}` deletes a file and returns `204 No Content`.
- OpenAPI JSON and Swagger UI are available at `/v3/api-docs` and `/swagger-ui.html`.

## 6. Design Decisions
The existing Spring Data JPA persistence approach is retained. DTO mapping at the service boundary prevents persistence entities from becoming part of the API contract. Upload size and media-type/signature validation are enforced before persistence.

## 7. Non-Functional Requirements
Uploads are limited to 100 MiB. JPA Open Session in View is disabled; service transactions load required content before returning to the web layer. Actuator health is exposed at `/v1/health`.

## 8. Dependencies & Integrations
H2 provides the database, MapStruct maps entities to DTOs, and springdoc generates interactive API documentation from controller annotations.
