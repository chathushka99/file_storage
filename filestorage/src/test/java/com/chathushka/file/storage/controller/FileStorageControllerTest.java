package com.chathushka.file.storage.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chathushka.file.storage.component.Validator;
import com.chathushka.file.storage.dto.FileContentDto;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.service.FileStorageService;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Tests the HTTP contract for file storage endpoints.
 */
@WebMvcTest(FileStorageController.class)
class FileStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    /**
     * Confirms metadata listing is serialized using the public JSON property names.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    @DisplayName("Should return file metadata as a JSON array")
    void shouldReturnFilesAsJsonArray() throws Exception {
        // prepare //
        final FileDto fileDto = new FileDto("f1", "video.mp4", "/v1/files/f1", 128L, null);
        when(this.fileStorageService.getAllFiles(null, null)).thenReturn(List.of(fileDto));

        // act //
        final ResultActions response = this.mockMvc.perform(get("/v1/files"));

        // assert //
        response // ls
                .andExpect(status().isOk()) // ls
                .andExpect(jsonPath("$[0].fileid").value("f1")) // ls
                .andExpect(jsonPath("$[0].name").value("video.mp4"));
    }

    /**
     * Confirms a successful upload returns its download location.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    @DisplayName("Should return a created response with the download location")
    void shouldCreateFileAndReturnLocationHeader() throws Exception {
        // prepare //
        final MockMultipartFile upload = // ls
                new MockMultipartFile( // ls
                        "data", "video.mp4", MediaType.valueOf("video/mp4").toString(), new byte[]{1, 2, 3});
        when(this.fileStorageService.saveFile(any()))
                .thenReturn(new FileDto("id-1", "video.mp4", "/v1/files/id-1", 3L, null));

        // act //
        final ResultActions response = this.mockMvc.perform(multipart("/v1/files").file(upload));

        // assert //
        response // ls
                .andExpect(status().isCreated()) // ls
                .andExpect(header().string("Location", "/v1/files/id-1"));

        // verify //
        verify(this.validator).validateFile(upload);
        verify(this.fileStorageService).saveFile(upload);
    }

    /**
     * Confirms a download returns file bytes and attachment metadata.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    @DisplayName("Should return file content with attachment headers")
    void shouldDownloadFileContent() throws Exception {
        // prepare //
        when(this.fileStorageService.getFileById("id-1"))
                .thenReturn(new FileContentDto("video.mp4", "video/mp4", new byte[]{1, 2, 3}));

        // act //
        final ResultActions response = this.mockMvc.perform(get("/v1/files/id-1"));

        // assert //
        response // ls
                .andExpect(status().isOk()) // ls
                .andExpect(header().string("Content-Disposition", containsString("filename*=UTF-8''video.mp4"))) // ls
                .andExpect(header().string("Content-Type", "video/mp4"));
    }

    /**
     * Confirms deletion returns no content.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    @DisplayName("Should return no content after deleting a file")
    void shouldDeleteFile() throws Exception {
        // act //
        final ResultActions response = this.mockMvc.perform(delete("/v1/files/id-1"));

        // assert //
        response.andExpect(status().isNoContent());

        // verify //
        verify(this.fileStorageService).deleteFileById("id-1");
    }
}
