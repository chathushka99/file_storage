package com.chathushka.file.storage.advice;

import com.chathushka.file.storage.contant.Constant;
import com.chathushka.file.storage.dto.ApiResponse;
import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts application and request failures into consistent API responses.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * Handles unexpected server errors and logs an identifier for support.
     *
     * @param exception unexpected error
     * @return API response with HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleInternalServerErrors(Exception exception) {
        final String errorId = ProcessHandle.current().pid() + String.valueOf(System.currentTimeMillis());
        log.error("Internal Server Error Occurred. Error ID: {}", errorId, exception);

        final String description = // ls
                ExceptionCode.UNHANDLED_SERVER_EXCEPTION // ls
                        .getErrorDescription() // ls
                        .replace(Constant.ERROR_ID_PLACEHOLDER, errorId);
        return ApiResponse.builder() // ls
                .title(HttpStatus.INTERNAL_SERVER_ERROR.toString()) // ls
                .errorCode(ExceptionCode.UNHANDLED_SERVER_EXCEPTION.getErrorCode()) // ls
                .description(description) // ls
                .build();
    }

    /**
     * Handles requests that use an unsupported HTTP method.
     *
     * @param exception unsupported-method exception
     * @param request   HTTP request that caused the exception
     * @return API response with HTTP status 400
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleUnsupportedHttpMethod( // ls
                                                       HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        log.info("Bad request for {}", request.getRequestURI());
        final String errorMessage = exception.getLocalizedMessage() + " for " + request.getRequestURI();

        return ApiResponse.builder() // ls
                .errorCode(ExceptionCode.UNSUPPORTED_HTTP_METHOD.getErrorCode()) // ls
                .title(HttpStatus.BAD_REQUEST.toString()) // ls
                .description(HttpStatus.BAD_REQUEST.getReasonPhrase()) // ls
                .errorList(List.of(errorMessage)) // ls
                .build();
    }

    /**
     * Handles expected application errors with their declared HTTP status.
     *
     * @param exception application error to expose
     * @return API error response and its associated status
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException exception) {
        final ApiResponse<?> response = // ls
                ApiResponse.builder() // ls
                        .errorCode(exception.getErrorCode()) // ls
                        .description(exception.getErrorDescription()) // ls
                        .build();
        return new ResponseEntity<>(response, exception.getHttpStatus());
    }

    /**
     * Handles invalid request parameters.
     *
     * @param exception validation failure
     * @return API response with HTTP status 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleParameterConstraintViolation(ConstraintViolationException exception) {
        final String errorMessage = exception.getLocalizedMessage();

        return ApiResponse.builder() // ls
                .errorCode(ExceptionCode.PARAMETER_CONSTRAINT_VIOLATION.getErrorCode()) // ls
                .title(HttpStatus.BAD_REQUEST.toString()) // ls
                .description(HttpStatus.BAD_REQUEST.getReasonPhrase()) // ls
                .errorList(List.of(errorMessage)) // ls
                .build();
    }
}
