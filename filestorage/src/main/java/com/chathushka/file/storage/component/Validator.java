package com.chathushka.file.storage.component;

import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/** Validates user requests */
@Component
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
    var contentType = multipartFile.getContentType();
    if (contentType == null
        || !allowedExtensions.contains(contentType.toLowerCase(Locale.ROOT))
        || !hasAllowedSignature(multipartFile, contentType)) {
      throw new ApiException(ExceptionCode.FILE_EXTENSION_NOT_ALLOWED);
    }
  }

  private boolean hasAllowedSignature(MultipartFile multipartFile, String contentType) {
    try {
      byte[] bytes = multipartFile.getBytes();
      if ("video/mp4".equalsIgnoreCase(contentType)) {
        return isMp4(bytes);
      }
      if ("video/mpeg".equalsIgnoreCase(contentType)) {
        return isMpeg(bytes);
      }
      return false;
    } catch (IOException e) {
      throw new ApiException(ExceptionCode.UNHANDLED_SERVER_EXCEPTION);
    }
  }

  private boolean isMp4(byte[] bytes) {
    return bytes.length > 11
        && bytes[4] == 'f'
        && bytes[5] == 't'
        && bytes[6] == 'y'
        && bytes[7] == 'p';
  }

  private boolean isMpeg(byte[] bytes) {
    if (bytes.length < 4) {
      return false;
    }
    boolean hasPackHeader = bytes[0] == 0x00 && bytes[1] == 0x00 && bytes[2] == 0x01 && bytes[3] == (byte) 0xBA;
    boolean hasSequenceHeader = bytes[0] == 0x00 && bytes[1] == 0x00 && bytes[2] == 0x01 && bytes[3] == (byte) 0xB3;
    return hasPackHeader || hasSequenceHeader;
  }
}
