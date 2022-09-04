package com.chathushka.file.storage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class used for sending response from API
 *
 * @param <T> data that sends as response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ApiResponse<T> {
  private String errorCode;
  private String title;
  private String description;
  private T result;
  private List<String> errorList;
  private final LocalDateTime timeStamp = LocalDateTime.now();
}
