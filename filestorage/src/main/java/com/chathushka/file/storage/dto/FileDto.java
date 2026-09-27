package com.chathushka.file.storage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Represents file metadata returned by the API.
 *
 * @param fileId    unique file identifier
 * @param fileName  original filename
 * @param location  relative URL for downloading the file
 * @param size      file size in bytes
 * @param createdAt record creation timestamp
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FileDto( // ls
                       @JsonProperty("fileid") String fileId, // ls
                       @JsonProperty("name") String fileName, // ls
                       String location, // ls
                       @JsonProperty("size") Long size, // ls
                       @JsonProperty("created_at") LocalDateTime createdAt) {
}
