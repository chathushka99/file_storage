package com.chathushka.file.storage.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests construction of the shared API response envelope.
 */
class ApiResponseTest {

    /**
     * Confirms builder values are retained in the response.
     */
    @Test
    @DisplayName("Should retain values supplied to the response builder")
    void shouldRetainBuilderValues() {
        // prepare //
        final List<String> errors = List.of("invalid input");

        // act //
        final ApiResponse<String> response =
                ApiResponse.<String>builder() // ls
                        .errorCode("FSA008") // ls
                        .title("Bad Request") // ls
                        .description("Request parameters are invalid") // ls
                        .result("result") // ls
                        .errorList(errors) // ls
                        .build();

        // assert //
        assertThat(response.getErrorCode()).isEqualTo("FSA008");
        assertThat(response.getTitle()).isEqualTo("Bad Request");
        assertThat(response.getDescription()).isEqualTo("Request parameters are invalid");
        assertThat(response.getResult()).isEqualTo("result");
        assertThat(response.getErrorList()).containsExactly("invalid input");
        assertThat(response.getTimeStamp()).isNotNull();
    }
}
