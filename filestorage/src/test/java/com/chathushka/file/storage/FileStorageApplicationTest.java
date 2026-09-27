package com.chathushka.file.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chathushka.file.storage.dto.FileContentDto;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.exception.ApiException;
import com.chathushka.file.storage.service.FileStorageService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

/**
 * Verifies application initialization and generated API documentation.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FileStorageApplicationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private FileStorageService fileStorageService;

  /**
   * Confirms application components can be wired together.
   */
  @Test
  @DisplayName("Should load the application context")
  void shouldLoadApplicationContext() {}

  /**
   * Confirms the generated OpenAPI document exposes the configured API title.
   *
   * @throws Exception if the mock HTTP request fails
   */
  @Test
  @DisplayName("Should publish the OpenAPI document")
  void shouldPublishOpenApiDocument() throws Exception {
    // act //
    final ResultActions response = this.mockMvc.perform(get("/v3/api-docs"));

    // assert //
    response // ls
        .andExpect(status().isOk()) // ls
        .andExpect(jsonPath("$.info.title").value("File Storage API"));
  }

  /**
   * Confirms files persist with generated identifiers and duplicate metadata is rejected.
   */
  @Test
  @DisplayName("Should persist, retrieve, and reject duplicate files")
  void shouldPersistAndRetrieveFilesAndRejectDuplicates() {
    // prepare //
    final String fileName = "integration-" + UUID.randomUUID() + ".mp4";
    final byte[] content = new byte[] {0, 0, 0, 24, 'f', 't', 'y', 'p', 'i', 's', 'o', 'm'};
    final MockMultipartFile upload =
        new MockMultipartFile("data", fileName, "video/mp4", content);

    // act //
    final FileDto savedFile = this.fileStorageService.saveFile(upload);
    final String fileId = savedFile.location().substring(savedFile.location().lastIndexOf('/') + 1);
    final FileContentDto downloadedFile = this.fileStorageService.getFileById(fileId);

    // assert //
    assertThat(savedFile.fileId()).isEqualTo(fileId);
    assertThat(downloadedFile.fileName()).isEqualTo(fileName);
    assertThat(downloadedFile.content()).containsExactly(content);
    assertThatExceptionOfType(ApiException.class) // ls
        .isThrownBy(() -> this.fileStorageService.saveFile(upload)) // ls
        .extracting(ApiException::getErrorCode) // ls
        .isEqualTo("FSA007");

    // act //
    this.fileStorageService.deleteFileById(fileId);
  }
}
