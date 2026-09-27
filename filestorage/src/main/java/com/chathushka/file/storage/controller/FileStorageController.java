package com.chathushka.file.storage.controller;

import com.chathushka.file.storage.component.Validator;
import com.chathushka.file.storage.dto.FileContentDto;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Exposes operations for storing, listing, downloading, and deleting files.
 */
@RestController
@RequestMapping("/v1")
@Validated
@Tag(name = "File Storage", description = "Upload and manage stored video files")
public class FileStorageController {

    private final FileStorageService fileStorageService;
    private final Validator validator;

    /**
     * Creates the controller with its service and upload validator.
     *
     * @param fileStorageService service for file operations
     * @param validator          validator for uploaded file content
     */
    public FileStorageController(FileStorageService fileStorageService, Validator validator) {
        this.fileStorageService = fileStorageService;
        this.validator = validator;
    }

    /**
     * Downloads a stored file as an attachment.
     *
     * <p>Example request:
     *
     * <pre>{@code
     * curl -X GET "http://localhost:8080/v1/files/id-1" -o video.mp4
     * }</pre>
     *
     * @param fileId identifier of the stored file
     * @return file content with its stored media type and filename
     */
    @Operation(summary = "Download a file", description = "Returns the stored file content.")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @ApiResponse(responseCode = "404", description = "No file exists with the supplied identifier")
    @GetMapping("/files/{fileid}")
    public ResponseEntity<ByteArrayResource> getFileById( // ls
                                                          @Parameter(description = "Identifier of the stored file") @PathVariable("fileid") String fileId) {
        final FileContentDto fileContentDto = this.fileStorageService.getFileById(fileId);
        final String contentDisposition = // ls
                ContentDisposition.builder("attachment") // ls
                        .filename(fileContentDto.fileName(), StandardCharsets.UTF_8) // ls
                        .build() // ls
                        .toString();

        return ResponseEntity.ok() // ls
                .contentType(MediaType.parseMediaType(fileContentDto.fileType())) // ls
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) // ls
                .body(new ByteArrayResource(fileContentDto.content()));
    }

    /**
     * Deletes a stored file.
     *
     * <p>Example request:
     *
     * <pre>{@code
     * curl -X DELETE "http://localhost:8080/v1/files/id-1"
     * }</pre>
     *
     * @param fileId identifier of the stored file
     * @return an empty response with HTTP status 204
     */
    @Operation(summary = "Delete a file", description = "Deletes a stored file by identifier.")
    @ApiResponse(responseCode = "204", description = "File deleted successfully")
    @ApiResponse(responseCode = "404", description = "No file exists with the supplied identifier")
    @DeleteMapping("/files/{fileid}")
    public ResponseEntity<Void> deleteFileById( // ls
                                                @Parameter(description = "Identifier of the stored file") @PathVariable("fileid") String fileId) {
        this.fileStorageService.deleteFileById(fileId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Stores a validated video upload and returns its download location.
     *
     * <p>Example request:
     *
     * <pre>{@code
     * curl -X POST "http://localhost:8080/v1/files" -F "data=@video.mp4;type=video/mp4"
     * }</pre>
     *
     * @param multipartFile multipart upload supplied in the {@code data} part
     * @return an empty response with HTTP status 201 and a download URL
     */
    @Operation(summary = "Upload a file", description = "Validates and stores a supported video file.")
    @ApiResponse(responseCode = "201", description = "File stored successfully")
    @ApiResponse(responseCode = "400", description = "File is empty or exceeds the size limit")
    @ApiResponse(responseCode = "409", description = "A file with the same metadata already exists")
    @ApiResponse(responseCode = "415", description = "File type or signature is not supported")
    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveFile(@RequestPart("data") MultipartFile multipartFile) {
        this.validator.validateFile(multipartFile);
        final FileDto fileDto = this.fileStorageService.saveFile(multipartFile);
        return ResponseEntity.created(URI.create(fileDto.location())).build();
    }

    /**
     * Lists file metadata, optionally using a page and page size.
     *
     * <p>Example request:
     *
     * <pre>{@code
     * curl -X GET "http://localhost:8080/v1/files?page=0&size=10"
     * }</pre>
     *
     * @param page zero-based page index, supplied together with {@code size}
     * @param size page size from 1 to 100, supplied together with {@code page}
     * @return metadata for files on the selected page
     */
    @Operation(summary = "List files", description = "Returns stored file metadata.")
    @ApiResponse(responseCode = "200", description = "File metadata returned successfully")
    @ApiResponse(responseCode = "400", description = "Pagination parameters are invalid")
    @GetMapping("/files")
    public List<FileDto> getFiles( // ls
                                   @Parameter(description = "Zero-based page index") @RequestParam(required = false) @Min(0) Integer page, // ls
                                   @Parameter(description = "Page size from 1 to 100") // ls
                                   @RequestParam(required = false) @Min(1) @Max(100) Integer size) {
        return this.fileStorageService.getAllFiles(page, size);
    }
}
