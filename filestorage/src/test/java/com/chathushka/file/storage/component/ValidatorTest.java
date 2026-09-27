package com.chathushka.file.storage.component;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.chathushka.file.storage.exception.ApiException;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Tests upload validation for media types, sizes, and file signatures.
 */
class ValidatorTest {

    private static final long MAX_FILE_SIZE = 100L;

    private final Validator validator = new Validator(List.of("video/mp4", "video/mpeg"), MAX_FILE_SIZE);

    /**
     * Confirms null uploads are rejected as empty.
     */
    @Test
    @DisplayName("Should reject a null upload")
    void shouldRejectNullUpload() {
        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.validator.validateFile(null)) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA005");
    }

    /**
     * Confirms zero-byte uploads are rejected as empty.
     */
    @Test
    @DisplayName("Should reject an empty upload")
    void shouldRejectEmptyUpload() {
        // prepare //
        final MultipartFile upload = new MockMultipartFile("data", "empty.mp4", "video/mp4", new byte[0]);

        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.validator.validateFile(upload)) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA005");
    }

    /**
     * Confirms files beyond the configured byte limit are rejected.
     */
    @Test
    @DisplayName("Should reject an upload larger than the configured limit")
    void shouldRejectOversizedUpload() {
        // prepare //
        final MultipartFile upload = // ls
                new MockMultipartFile("data", "large.mp4", "video/mp4", new byte[101]);

        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.validator.validateFile(upload)) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA004");
    }

    /**
     * Confirms unsupported media types are rejected.
     */
    @Test
    @DisplayName("Should reject an unsupported media type")
    void shouldRejectUnsupportedMediaType() {
        // prepare //
        final MultipartFile upload = // ls
                new MockMultipartFile("data", "document.txt", "text/plain", new byte[]{1, 2, 3});

        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.validator.validateFile(upload)) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA006");
    }

    /**
     * Confirms an accepted media type still requires a matching MP4 signature.
     */
    @Test
    @DisplayName("Should reject an MP4 upload with an invalid signature")
    void shouldRejectInvalidMp4Signature() {
        // prepare //
        final MultipartFile upload = // ls
                new MockMultipartFile("data", "video.mp4", "video/mp4", new byte[]{1, 2, 3, 4});

        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.validator.validateFile(upload)) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA006");
    }

    /**
     * Confirms valid MP4 content passes validation.
     */
    @Test
    @DisplayName("Should accept an MP4 upload with a valid signature")
    void shouldAcceptValidMp4Upload() {
        // prepare //
        final MultipartFile upload = // ls
                new MockMultipartFile( // ls
                        "data", // ls
                        "video.mp4", // ls
                        "video/mp4", // ls
                        new byte[]{0, 0, 0, 24, 'f', 't', 'y', 'p', 'i', 's', 'o', 'm'});

        // assert //
        assertThatCode(() -> this.validator.validateFile(upload)).doesNotThrowAnyException();
    }

    /**
     * Confirms valid MPEG content passes validation.
     */
    @Test
    @DisplayName("Should accept an MPEG upload with a valid signature")
    void shouldAcceptValidMpegUpload() {
        // prepare //
        final MultipartFile upload = // ls
                new MockMultipartFile("data", "video.mpg", "video/mpeg", new byte[]{0, 0, 1, (byte) 0xBA});

        // assert //
        assertThatCode(() -> this.validator.validateFile(upload)).doesNotThrowAnyException();
    }
}
