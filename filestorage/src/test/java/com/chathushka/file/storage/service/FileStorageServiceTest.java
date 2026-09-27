package com.chathushka.file.storage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chathushka.file.storage.dto.FileContentDto;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;
import com.chathushka.file.storage.exception.ApiException;
import com.chathushka.file.storage.mapper.FileMapper;
import com.chathushka.file.storage.repository.FileRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Tests file persistence, mapping, and pagination behavior.
 */
@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FileStorageService fileStorageService;

    /**
     * Confirms saving returns the generated file location.
     */
    @Test
    @DisplayName("Should save a file and return its download location")
    void shouldSaveFileAndReturnDownloadLocation() {
        // prepare //
        final FileEntity savedEntity = new FileEntity();
        savedEntity.setFileId("unit-test-id");
        savedEntity.setFileName("video_file.mp4");
        savedEntity.setFileSize(13L);
        when(this.fileRepository.saveAndFlush(any(FileEntity.class))).thenReturn(savedEntity);
        when(this.fileMapper.fileEntityToFileDto(savedEntity)) // ls
                .thenReturn(new FileDto("unit-test-id", "video_file.mp4", null, 13L, null));
        final MockMultipartFile upload = // ls
                new MockMultipartFile("data", "video_file.mp4", "video/mp4", mp4Bytes());

        // act //
        final FileDto result = this.fileStorageService.saveFile(upload);

        // assert //
        assertThat(result.location()).isEqualTo("/v1/files/unit-test-id");

        // verify //
        verify(this.fileRepository).saveAndFlush(any(FileEntity.class));
        verify(this.fileMapper).fileEntityToFileDto(savedEntity);
    }

    /**
     * Confirms duplicate persistence errors become the stable conflict error.
     */
    @Test
    @DisplayName("Should report a conflict when file metadata is duplicated")
    void shouldRejectDuplicateFile() {
        // prepare //
        final MockMultipartFile upload = // ls
                new MockMultipartFile("data", "video_file.mp4", "video/mp4", mp4Bytes());
        when(this.fileRepository.saveAndFlush(any(FileEntity.class))) // ls
                .thenThrow(new DataIntegrityViolationException("Duplicate file"));

        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.fileStorageService.saveFile(upload)) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA007");
    }

    /**
     * Confirms a stored file is mapped to a download DTO.
     */
    @Test
    @DisplayName("Should map a stored file to download content")
    void shouldGetFileContentById() {
        // prepare //
        final FileEntity fileEntity = new FileEntity();
        fileEntity.setFileId("unit-test-id");
        final FileContentDto expected = new FileContentDto("video.mp4", "video/mp4", mp4Bytes());
        when(this.fileRepository.findById("unit-test-id")).thenReturn(Optional.of(fileEntity));
        when(this.fileMapper.fileEntityToFileContentDto(fileEntity)).thenReturn(expected);

        // act //
        final FileContentDto result = this.fileStorageService.getFileById("unit-test-id");

        // assert //
        assertThat(result).isEqualTo(expected);

        // verify //
        verify(this.fileRepository).findById("unit-test-id");
        verify(this.fileMapper).fileEntityToFileContentDto(fileEntity);
    }

    /**
     * Confirms missing files cannot be downloaded.
     */
    @Test
    @DisplayName("Should report not found when downloading a missing file")
    void shouldRejectDownloadForMissingFile() {
        // prepare //
        when(this.fileRepository.findById("missing")).thenReturn(Optional.empty());

        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.fileStorageService.getFileById("missing")) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA003");
    }

    /**
     * Confirms deleting an existing file invokes the repository.
     */
    @Test
    @DisplayName("Should delete an existing file")
    void shouldDeleteExistingFile() {
        // prepare //
        when(this.fileRepository.existsById("unit-test-id")).thenReturn(true);

        // act //
        this.fileStorageService.deleteFileById("unit-test-id");

        // verify //
        verify(this.fileRepository).deleteById("unit-test-id");
    }

    /**
     * Confirms deleting a missing file returns not found.
     */
    @Test
    @DisplayName("Should report not found when deleting a missing file")
    void shouldRejectDeleteForMissingFile() {
        // prepare //
        when(this.fileRepository.existsById("missing")).thenReturn(false);

        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.fileStorageService.deleteFileById("missing")) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA003");
    }

    /**
     * Confirms an unpaged listing uses the default page and maps its content.
     */
    @Test
    @DisplayName("Should list files using the default page")
    void shouldListFilesUsingDefaultPage() {
        // prepare //
        final FileEntity fileEntity = new FileEntity();
        fileEntity.setCreatedAt(LocalDateTime.now().minusDays(1));
        fileEntity.setFileName("video_file.mp4");
        final List<FileDto> expected = List.of(new FileDto("unit-test-id", "video_file.mp4", null, 13L, null));
        when(this.fileRepository.findAll(any(PageRequest.class))) // ls
                .thenReturn(new PageImpl<>(List.of(fileEntity)));
        when(this.fileMapper.fileEntityListToFileDtoList(List.of(fileEntity))).thenReturn(expected);

        // act //
        final List<FileDto> result = this.fileStorageService.getAllFiles(null, null);

        // assert //
        assertThat(result).containsExactlyElementsOf(expected);

        // verify //
        verify(this.fileRepository) // ls
                .findAll(PageRequest.of(0, 10, Sort.by("createdAt").descending()));
        verify(this.fileMapper).fileEntityListToFileDtoList(List.of(fileEntity));
    }

    /**
     * Confirms a listing uses explicit pagination parameters.
     */
    @Test
    @DisplayName("Should list files using the requested page")
    void shouldListFilesUsingRequestedPage() {
        // prepare //
        when(this.fileRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));
        when(this.fileMapper.fileEntityListToFileDtoList(List.of())).thenReturn(List.of());

        // act //
        final List<FileDto> result = this.fileStorageService.getAllFiles(0, 5);

        // assert //
        assertThat(result).isEmpty();

        // verify //
        verify(this.fileRepository).findAll(PageRequest.of(0, 5, Sort.by("createdAt").descending()));
    }

    /**
     * Confirms pagination parameters must be supplied together.
     */
    @Test
    @DisplayName("Should reject a listing with only one pagination parameter")
    void shouldRejectIncompletePagination() {
        // assert //
        assertThatExceptionOfType(ApiException.class) // ls
                .isThrownBy(() -> this.fileStorageService.getAllFiles(0, null)) // ls
                .extracting(ApiException::getErrorCode) // ls
                .isEqualTo("FSA008");
    }

    /**
     * Returns a minimal byte sequence with a valid MP4 marker.
     *
     * @return minimal MP4 header bytes
     */
    private static byte[] mp4Bytes() {
        return new byte[]{0, 0, 0, 24, 'f', 't', 'y', 'p', 'i', 's', 'o', 'm'};
    }
}
