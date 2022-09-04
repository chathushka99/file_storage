package com.chathushka.file.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** API error codes */
@Getter
@AllArgsConstructor
public enum ExceptionCode {
  UNHANDLED_SERVER_EXCEPTION(
      "FSA000",
      "An error occurred in server side. Please contact Dev team with ID ERROR_ID_PLACEHOLDER",
      HttpStatus.INTERNAL_SERVER_ERROR),
  UNSUPPORTED_HTTP_METHOD("FSA001", "Unsupported Http Method.", HttpStatus.BAD_REQUEST),
  NO_HANDLER("FSA002", "No suitable handler found for the request.", HttpStatus.BAD_REQUEST),
  NOT_FOUND("FSA003", "File not found", HttpStatus.NOT_FOUND),
  FILE_TOO_LARGE(
      "FSA004", "File is larger than max file size allowed.", HttpStatus.BAD_REQUEST),
  FILE_EMPTY("FSA005", "File is empty.", HttpStatus.BAD_REQUEST),
  FILE_EXTENSION_NOT_ALLOWED(
      "FSA006", "File extension not allowed.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
  FILE_ALREADY_EXISTS("FSA007", "File exists", HttpStatus.CONFLICT),
  PARAMETER_CONSTRAINT_VIOLATION("FSA008", "Invalid Parameter", HttpStatus.BAD_REQUEST);

  private final String errorCode;
  private final String errorDescription;
  private final HttpStatus httpStatus;
}
