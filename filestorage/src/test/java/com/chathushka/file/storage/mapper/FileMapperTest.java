package com.chathushka.file.storage.mapper;

import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileMapperTest {
  private final FileMapper fileMapper = Mappers.getMapper(FileMapper.class);

  @Test
  void fileEntityListToFileDtoList_MapsSizeField() {
    FileEntity entity = new FileEntity();
    entity.setFileId("id-1");
    entity.setFileName("video.mp4");
    entity.setFileSize(123L);
    entity.setCreatedAt(LocalDateTime.now());

    List<FileDto> result = fileMapper.fileEntityListToFileDtoList(List.of(entity));

    assertEquals(1, result.size());
    assertEquals(123L, result.get(0).getSize());
    assertEquals("id-1", result.get(0).getFileId());
  }
}
