package com.chathushka.file.storage.advice;

import com.chathushka.file.storage.contant.Constant;
import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.exception.ApiException;
import com.chathushka.file.storage.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  /**
   * Default handler for all internal server errors
   *
   * @param e exception
   * @return API response for HTTP status 500
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<?> handleInternalServerErrors(Exception e) {
    var errorId = ProcessHandle.current().pid() + String.valueOf(System.currentTimeMillis());
    log.error("Internal Server Error Occurred. Error ID: {}", errorId, e);
    return ApiResponse.builder()
        .title(HttpStatus.INTERNAL_SERVER_ERROR.toString())
        .errorCode(ExceptionCode.UNHANDLED_SERVER_EXCEPTION.getErrorCode())
        .description(
            ExceptionCode.UNHANDLED_SERVER_EXCEPTION
                .getErrorDescription()
                .replace(Constant.ERROR_ID_PLACEHOLDER, errorId))
        .build();
  }

  /**
   * Handles HTTP Method not supported exceptions
   *
   * @param e exception
   * @param request HTTP request
   * @return API response for HTTP status 400 invalid HTTP method
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ApiResponse<?> handleUnsupportedHttpMethod(
      HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
    log.info("Bad request for {}", request.getRequestURI());
    var errorMessage = e.getLocalizedMessage() + " for " + request.getRequestURI();
    return ApiResponse.builder()
        .errorCode(ExceptionCode.UNSUPPORTED_HTTP_METHOD.getErrorCode())
        .title(HttpStatus.BAD_REQUEST.toString())
        .errorCode(ExceptionCode.UNSUPPORTED_HTTP_METHOD.getErrorCode())
        .description(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .errorList(List.of(errorMessage))
        .build();
  }

  /**
   * Handles user related input exception
   *
   * @param e exception
   * @return API response with HTTP status 4XX
   */
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<?>> handleApiException(ApiException e) {
    return new ResponseEntity<>(
        ApiResponse.builder()
            .errorCode(e.getErrorCode())
            .description(e.getErrorDescription())
            .build(),
        e.getHttpStatus());
  }

  /**
   * Handles constraint violation exceptions
   *
   * @param e exception
   * @return API response for HTTP status 400 invalid HTTP method
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ApiResponse<?> handleUnsupportedHttpMethod(ConstraintViolationException e) {
    var errorMessage = e.getLocalizedMessage();
    return ApiResponse.builder()
        .errorCode(ExceptionCode.PARAMETER_CONSTRAINT_VIOLATION.getErrorCode())
        .title(HttpStatus.BAD_REQUEST.toString())
        .errorCode(ExceptionCode.PARAMETER_CONSTRAINT_VIOLATION.getErrorCode())
        .description(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .errorList(List.of(errorMessage))
        .build();
  }
}
