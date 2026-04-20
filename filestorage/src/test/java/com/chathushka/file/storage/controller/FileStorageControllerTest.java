package com.chathushka.file.storage.controller;

import com.chathushka.file.storage.component.Validator;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileStorageController.class)
class FileStorageControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private FileStorageService fileStorageService;
  @MockBean private Validator validator;
  @MockBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  void getFiles_ReturnsRawArray() throws Exception {
    FileDto dto = new FileDto();
    dto.setFileId("f1");
    dto.setFileName("video.mp4");
    dto.setSize(128L);
    when(fileStorageService.getAllFiles(null, null)).thenReturn(List.of(dto));

    mockMvc
        .perform(get("/v1/files"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].fileid").value("f1"))
        .andExpect(jsonPath("$[0].name").value("video.mp4"));
  }

  @Test
  void saveFile_ReturnsCreatedWithLocationHeader() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile(
            "data", "video.mp4", MediaType.valueOf("video/mp4").toString(), new byte[] {1, 2, 3});
    FileDto response = new FileDto();
    response.setLocation("/v1/files/id-1");
    doNothing().when(validator).validateFile(any());
    when(fileStorageService.saveFile(any())).thenReturn(response);

    mockMvc
        .perform(multipart("/v1/files").file(file))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/v1/files/id-1"));
  }

  @Test
  void deleteFile_ReturnsNoContent() throws Exception {
    mockMvc.perform(delete("/v1/files/id-1")).andExpect(status().isNoContent());
  }
}
