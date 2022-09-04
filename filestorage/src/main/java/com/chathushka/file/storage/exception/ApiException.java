package com.chathushka.file.storage.exception;

import com.chathushka.file.storage.enums.ExceptionCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/** Handles errors due to user input */
@Getter
@Setter
public class ApiException extends RuntimeException {
  private final String errorCode;
  private final String errorDescription;

  private final HttpStatus httpStatus;

  public ApiException(ExceptionCode exceptionCode) {
    super(exceptionCode.getErrorDescription());
    this.errorCode = exceptionCode.getErrorCode();
    this.errorDescription = exceptionCode.getErrorDescription();
    this.httpStatus = exceptionCode.getHttpStatus();
  }
}
