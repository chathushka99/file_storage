package com.chathushka.file.storage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class used for sending response from API
 *
 * @param <T> data that sends as response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private final String errorCode;
  private final String title;
  private final String description;
  private final T result;
  private final List<String> errorList;
  private final LocalDateTime timeStamp;

  private ApiResponse(Builder<T> builder) {
    this.errorCode = builder.errorCode;
    this.title = builder.title;
    this.description = builder.description;
    this.result = builder.result;
    this.errorList = builder.errorList;
    this.timeStamp = LocalDateTime.now();
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public T getResult() {
    return result;
  }

  public List<String> getErrorList() {
    return errorList;
  }

  public LocalDateTime getTimeStamp() {
    return timeStamp;
  }

  public static final class Builder<T> {
    private String errorCode;
    private String title;
    private String description;
    private T result;
    private List<String> errorList;

    public Builder<T> errorCode(String errorCode) {
      this.errorCode = errorCode;
      return this;
    }

    public Builder<T> title(String title) {
      this.title = title;
      return this;
    }

    public Builder<T> description(String description) {
      this.description = description;
      return this;
    }

    public Builder<T> result(T result) {
      this.result = result;
      return this;
    }

    public Builder<T> errorList(List<String> errorList) {
      this.errorList = errorList;
      return this;
    }

    public ApiResponse<T> build() {
      return new ApiResponse<>(this);
    }
  }
}
