package com.chathushka.file.storage.mapper;

import com.chathushka.file.storage.dto.FileContentDto;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Converts persisted file records into API-facing data transfer objects.
 */
@Mapper(componentModel = "spring")
public interface FileMapper {

    /**
     * Maps a file entity to its metadata representation.
     *
     * @param fileEntity persisted file record
     * @return mapped metadata DTO
     */
    @Mapping(target = "size", source = "fileSize")
    FileDto fileEntityToFileDto(FileEntity fileEntity);

    /**
     * Maps file entities to their metadata representations.
     *
     * @param fileEntityList persisted file records
     * @return mapped metadata DTOs
     */
    @Mapping(target = "size", source = "fileSize")
    List<FileDto> fileEntityListToFileDtoList(List<FileEntity> fileEntityList);

    /**
     * Maps an entity to the content required for a file download.
     *
     * @param fileEntity persisted file record
     * @return mapped download DTO
     */
    @Mapping(target = "content", source = "file")
    FileContentDto fileEntityToFileContentDto(FileEntity fileEntity);
}
