package com.chathushka.file.storage.service;

import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.exception.ApiException;
import com.chathushka.file.storage.repository.FileRepository;
import com.chathushka.file.storage.entity.FileEntity;
import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.mapper.FileMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FileStorageService {

  private FileRepository fileRepository;
  private FileMapper fileMapper;

  /**
   * Save the file in database
   *
   * @param file file
   * @return file
   */
  @SneakyThrows
  public FileDto saveFile(@NonNull MultipartFile file) {
    // prepare entity
    var fileEntity = new FileEntity();
    fileEntity.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
    fileEntity.setFileType(file.getContentType());
    fileEntity.setFileSize(file.getSize());
    fileEntity.setFile(file.getBytes());
    // save fileEntity in db and get generated ID
    try {
      fileEntity = fileRepository.save(fileEntity);
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      throw new ApiException(ExceptionCode.FILE_ALREADY_EXISTS);
    }
    // prepare download location path
    var location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/v1/files/")
            .path(fileEntity.getFileId())
            .toUriString();
    // prepare DTO
    var fileDto = new FileDto();
    fileDto.setLocation(location);
    return fileDto;
  }

  /**
   * Gets file from database by ID
   *
   * @param fileId ID of the file saved in database
   * @return file entity
   */
  public FileEntity getFileById(@NonNull String fileId) {
    return fileRepository
        .findById(fileId)
        .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND));
   }

  /**
   * Deletes the file by file ID
   *
   * @param fileId ID of the file saved in database
   */
  public void deleteFileById(@NonNull String fileId) {
    try {
      fileRepository.deleteById(fileId);
    } catch (EmptyResultDataAccessException e) {
      throw new ApiException(ExceptionCode.NOT_FOUND);
    }
  }

  /**
   * Return file details in database
   *
   * @param page page number
   * @param size size of the page
   * @return list of file dto
   */
  public List<FileDto> getAllFiles(Integer page, Integer size) {
    List<FileEntity> fileEntities;
    if (!ObjectUtils.isEmpty(page) && !ObjectUtils.isEmpty(size)) {
      var pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
      fileEntities = new ArrayList<>(size);
      fileRepository.findAll(pageRequest).forEach(fileEntities::add);
    } else { // not recommended to load all the data.
      fileEntities = new ArrayList<>();
      fileRepository.findAll(Sort.by("createdAt").descending()).forEach(fileEntities::add);
    }
    return fileMapper.fileEntityListToFileDtoList(fileEntities);
  }
}
