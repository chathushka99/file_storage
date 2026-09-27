package com.chathushka.file.storage.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.chathushka.file.storage.dto.FileContentDto;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/**
 * Tests entity-to-DTO mappings used by file service operations.
 */
class FileMapperTest {

    private final FileMapper fileMapper = Mappers.getMapper(FileMapper.class);

    /**
     * Confirms file sizes and identifiers are mapped into metadata DTOs.
     */
    @Test
    @DisplayName("Should map file entity metadata")
    void shouldMapFileMetadata() {
        // prepare //
        final FileEntity entity = new FileEntity();
        entity.setFileId("id-1");
        entity.setFileName("video.mp4");
        entity.setFileSize(123L);
        entity.setCreatedAt(LocalDateTime.now());

        // act //
        final List<FileDto> result = this.fileMapper.fileEntityListToFileDtoList(List.of(entity));

        // assert //
        assertThat(result).hasSize(1);
        assertThat(result.get(0).size()).isEqualTo(123L);
        assertThat(result.get(0).fileId()).isEqualTo("id-1");
        assertThat(result.get(0).fileName()).isEqualTo("video.mp4");
    }

    /**
     * Confirms stored bytes and download metadata are mapped.
     */
    @Test
    @DisplayName("Should map file content for downloads")
    void shouldMapFileContent() {
        // prepare //
        final byte[] content = new byte[]{1, 2, 3};
        final FileEntity entity = new FileEntity();
        entity.setFileName("video.mp4");
        entity.setFileType("video/mp4");
        entity.setFile(content);

        // act //
        final FileContentDto result = this.fileMapper.fileEntityToFileContentDto(entity);

        // assert //
        assertThat(result.fileName()).isEqualTo("video.mp4");
        assertThat(result.fileType()).isEqualTo("video/mp4");
        assertThat(result.content()).containsExactly(content);
    }
}
