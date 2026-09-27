package com.chathushka.file.storage.enums;

import org.springframework.http.HttpStatus;

/**
 * Defines API errors and the HTTP status returned for each error.
 */
public enum ExceptionCode {
    UNHANDLED_SERVER_EXCEPTION( // ls
            "FSA000", // ls
            "An error occurred in server side. Please contact Dev team with ID ERROR_ID_PLACEHOLDER", // ls
            HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_HTTP_METHOD("FSA001", "Unsupported Http Method.", HttpStatus.BAD_REQUEST),
    NO_HANDLER("FSA002", "No suitable handler found for the request.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("FSA003", "File not found", HttpStatus.NOT_FOUND),
    FILE_TOO_LARGE("FSA004", "File is larger than max file size allowed.", HttpStatus.BAD_REQUEST),
    FILE_EMPTY("FSA005", "File is empty.", HttpStatus.BAD_REQUEST),
    FILE_EXTENSION_NOT_ALLOWED("FSA006", "File extension not allowed.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    FILE_ALREADY_EXISTS("FSA007", "File exists", HttpStatus.CONFLICT),
    PARAMETER_CONSTRAINT_VIOLATION("FSA008", "Invalid Parameter", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String errorDescription;
    private final HttpStatus httpStatus;

    /**
     * Creates an error definition.
     *
     * @param errorCode        stable API error code
     * @param errorDescription client-facing error description
     * @param httpStatus       HTTP status returned for the error
     */
    ExceptionCode(String errorCode, String errorDescription, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.httpStatus = httpStatus;
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
