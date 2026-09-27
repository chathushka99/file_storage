# Functional Specification - File Storage

## 1. Purpose
Provide an HTTP API for storing and managing small video files.

## 2. Actors & Stakeholders
API clients upload videos, list metadata, download file content, and delete stored files.

## 3. User Stories / Use Cases
- A client uploads a supported video and receives a URL for later retrieval.
- A client lists stored-file metadata, optionally using pagination.
- A client downloads a stored file by its identifier.
- A client deletes a stored file by its identifier.

## 4. Business Rules
Only `video/mp4` and `video/mpeg` uploads with matching recognized file signatures are accepted. Files must be non-empty and no larger than the configured maximum (100 MiB by default). Duplicate filename, media-type, and size combinations are rejected. Pagination parameters must be supplied together; page is zero-based and page size is between 1 and 100.

## 5. Inputs & Outputs
Uploads use multipart form data with the `data` part. Listing returns file identifier, name, download location, size, and creation timestamp. Downloads return bytes with the stored media type and an attachment disposition.

## 6. Edge Cases & Error Handling
Empty, oversized, unsupported, or malformed uploads produce API errors. Missing file identifiers return not found. Duplicate uploads return conflict. Invalid pagination returns bad request. Unexpected server errors are logged with an error identifier.

## 7. Acceptance Criteria
- Valid MP4 and MPEG uploads are stored and can be listed and downloaded.
- Unsupported, empty, oversized, and invalid-signature files are rejected.
- Missing files cannot be downloaded or deleted successfully.
- Listing supports default and explicitly requested pagination.
- API error responses preserve the documented error code and HTTP status behavior.
