package com.chathushka.file.storage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {
  @JsonProperty("fileid")
  private String fileId;

  @JsonProperty("name")
  private String fileName;

  private String location;

  @JsonProperty("size")
  private Long size;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;
}
