package com.chathushka.file.storage.dto;

/**
 * Represents file content and response metadata needed to serve a download.
 *
 * @param fileName original filename
 * @param fileType stored media type
 * @param content  file bytes
 */
public record FileContentDto(String fileName, String fileType, byte[] content) {
}
