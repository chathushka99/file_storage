package com.chathushka.file.storage.mapper;

import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

  @Mapping(target = "size", constant = "fileSize")
  List<FileDto> fileEntityListToFileDtoList(List<FileEntity> fileEntityList);
}
