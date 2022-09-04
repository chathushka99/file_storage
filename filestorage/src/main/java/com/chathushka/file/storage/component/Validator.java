package com.chathushka.file.storage.component;

import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Validates user requests */
@Component
@Slf4j
public class Validator {

  @Value("${allowed.file.extensions}")
  private List<String> allowedExtensions;

  @Value("${max.file.size}")
  private long maxFileSize;

  /**
   * validate file when uploaded
   *
   * @param multipartFile file
   * @throws ApiException when validation was failed
   */
  public void validateFile(MultipartFile multipartFile) {
    if (multipartFile == null || multipartFile.isEmpty()) {
      throw new ApiException(ExceptionCode.FILE_EMPTY);
    }
    if (multipartFile.getSize() > maxFileSize) {
      throw new ApiException(ExceptionCode.FILE_TOO_LARGE);
    }
    if (!allowedExtensions.contains(multipartFile.getContentType())) {
      throw new ApiException(ExceptionCode.FILE_EXTENSION_NOT_ALLOWED);
    }
  }
}
