package com.chathushka.file.storage.component;

import com.chathushka.file.storage.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import(Validator.class)
@SpringBootTest
class ValidatorTest {

  @Autowired private Validator validator;

  @Test
  void validateFileTest_File_Null() {
    // prepare test data
    MultipartFile multipartFile = null;
    // execute test method
    ApiException apiException =
        assertThrows(
            ApiException.class,
            () -> {
              validator.validateFile(multipartFile);
            });
    // verify
    assertEquals("FSA005", apiException.getErrorCode());
  }

  @Test
  void validateFileTest_File_Empty() {
    // prepare test data
    MockMultipartFile emptyFile =
        new MockMultipartFile("empty_file", "empty_file.mp4", "video/mp4", new byte[0]);
    // execute test method
    ApiException apiException =
        assertThrows(
            ApiException.class,
            () -> {
              validator.validateFile(emptyFile);
            });
    // verify
    assertEquals("FSA005", apiException.getErrorCode());
  }

  @Test
  void validateFileTest_File_Too_Large() {
    // prepare test data
    MockMultipartFile largeFile =
        new MockMultipartFile("large_file", "large_file.mp4", "video/mp4", new byte[104857602]);
    // execute test method
    ApiException apiException =
        assertThrows(
            ApiException.class,
            () -> {
              validator.validateFile(largeFile);
            });
    // verify
    assertEquals("FSA004", apiException.getErrorCode());
  }

  @Test
  void validateFileTest_File_Invalid_type() {
    // prepare test data
    MockMultipartFile textFile =
        new MockMultipartFile("text_file", "file_file.txt", "file/text", new byte[120]);
    // execute test method
    ApiException apiException =
        assertThrows(
            ApiException.class,
            () -> {
              validator.validateFile(textFile);
            });
    // verify
    assertEquals("FSA006", apiException.getErrorCode());
  }

  @Test
  void validateFileTest_File_All_Ok() {
    // prepare test data
    MockMultipartFile file =
        new MockMultipartFile("file", "file.mp4", "video/mp4", new byte[120]);
    // execute test method && verify
    assertDoesNotThrow(() -> validator.validateFile(file));
  }
}
