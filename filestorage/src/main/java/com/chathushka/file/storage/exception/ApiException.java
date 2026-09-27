package com.chathushka.file.storage.exception;

import com.chathushka.file.storage.enums.ExceptionCode;
import org.springframework.http.HttpStatus;

/**
 * Carries a stable API error code, description, and HTTP status.
 */
public class ApiException extends RuntimeException {

    private final String errorCode;
    private final String errorDescription;

    private final HttpStatus httpStatus;

    /**
     * Creates an API exception from a predefined error code.
     *
     * @param exceptionCode error definition to expose to the client
     */
    public ApiException(ExceptionCode exceptionCode) {
        super(exceptionCode.getErrorDescription());
        this.errorCode = exceptionCode.getErrorCode();
        this.errorDescription = exceptionCode.getErrorDescription();
        this.httpStatus = exceptionCode.getHttpStatus();
    }

    /**
     * Returns the stable API error code.
     *
     * @return API error code
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * Returns the client-facing error description.
     *
     * @return API error description
     */
    public String getErrorDescription() {
        return this.errorDescription;
    }

    /**
     * Returns the HTTP status associated with the error.
     *
     * @return HTTP status
     */
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
