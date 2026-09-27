package com.chathushka.file.storage.component;

import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.exception.ApiException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates upload size, declared media type, and file signature.
 */
@Component
public class Validator {

    private final List<String> allowedExtensions;
    private final long maxFileSize;

    /**
     * Creates a validator using configured media types and maximum size.
     *
     * @param allowedExtensions media types accepted for upload
     * @param maxFileSize       maximum allowed upload size in bytes
     */
    public Validator( // ls
                      @Value("${allowed.file.extensions}") List<String> allowedExtensions, // ls
                      @Value("${max.file.size}") long maxFileSize) {
        this.allowedExtensions = allowedExtensions;
        this.maxFileSize = maxFileSize;
    }

    /**
     * Validates that an upload is non-empty, within limits, and has a supported signature.
     *
     * @param multipartFile file to validate
     * @throws ApiException when the upload violates a file constraint or cannot be read
     */
    public void validateFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new ApiException(ExceptionCode.FILE_EMPTY);
        }

        if (multipartFile.getSize() > this.maxFileSize) {
            throw new ApiException(ExceptionCode.FILE_TOO_LARGE);
        }

        final String contentType = multipartFile.getContentType();
        if (contentType == null // ls
                || !this.allowedExtensions.contains(contentType.toLowerCase(Locale.ROOT)) // ls
                || !this.hasAllowedSignature(multipartFile, contentType)) {
            throw new ApiException(ExceptionCode.FILE_EXTENSION_NOT_ALLOWED);
        }
    }

    /**
     * Checks the actual file header against its declared media type.
     *
     * @param multipartFile upload whose header is checked
     * @param contentType   declared media type
     * @return whether the file header matches a supported type
     * @throws ApiException if the file bytes cannot be read
     */
    private boolean hasAllowedSignature(MultipartFile multipartFile, String contentType) {
        final byte[] bytes;
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException exception) {
            throw new ApiException(ExceptionCode.UNHANDLED_SERVER_EXCEPTION);
        }

        if ("video/mp4".equalsIgnoreCase(contentType)) {
            return this.isMp4(bytes);
        }

        if ("video/mpeg".equalsIgnoreCase(contentType)) {
            return this.isMpeg(bytes);
        }

        return false;
    }

    /**
     * Checks for the ISO base media file type marker in an MP4 header.
     *
     * @param bytes file content
     * @return whether the content contains the MP4 marker
     */
    private boolean isMp4(byte[] bytes) {
        return bytes.length > 11
                && bytes[4] == 'f'
                && bytes[5] == 't'
                && bytes[6] == 'y'
                && bytes[7] == 'p';
    }

    /**
     * Checks for a recognized MPEG pack or sequence header.
     *
     * @param bytes file content
     * @return whether the content begins with a supported MPEG header
     */
    private boolean isMpeg(byte[] bytes) {
        if (bytes.length < 4) {
            return false;
        }

        final boolean hasPackHeader = // ls
                bytes[0] == 0x00 && bytes[1] == 0x00 && bytes[2] == 0x01 && bytes[3] == (byte) 0xBA;
        final boolean hasSequenceHeader = // ls
                bytes[0] == 0x00 && bytes[1] == 0x00 && bytes[2] == 0x01 && bytes[3] == (byte) 0xB3;
        return hasPackHeader || hasSequenceHeader;
    }
}
