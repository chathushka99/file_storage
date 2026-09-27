package com.chathushka.file.storage.advice;

import static org.assertj.core.api.Assertions.assertThat;

import com.chathushka.file.storage.dto.ApiResponse;
import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Tests conversion of application errors into HTTP API responses.
 */
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();

    /**
     * Confirms an application error retains its code, description, and status.
     */
    @Test
    @DisplayName("Should preserve application error details in the response")
    void shouldPreserveApplicationErrorDetails() {
        // prepare //
        final ApiException exception = new ApiException(ExceptionCode.NOT_FOUND);

        // act //
        final ResponseEntity<ApiResponse<?>> response = this.exceptionHandler.handleApiException(exception);

        // assert //
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("FSA003");
        assertThat(response.getBody().getDescription()).isEqualTo("File not found");
    }
}
