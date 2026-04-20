package com.chathushka.file.storage.service;

import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.exception.ApiException;
import com.chathushka.file.storage.repository.FileRepository;
import com.chathushka.file.storage.entity.FileEntity;
import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.mapper.FileMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileStorageService {

  private final FileRepository fileRepository;
  private final FileMapper fileMapper;

  public FileStorageService(FileRepository fileRepository, FileMapper fileMapper) {
    this.fileRepository = fileRepository;
    this.fileMapper = fileMapper;
  }

  /**
   * Save the file in database
   *
   * @param file file
   * @return file
   */
  public FileDto saveFile(MultipartFile file) {
    if (file == null) {
      throw new ApiException(ExceptionCode.FILE_EMPTY);
    }
    // prepare entity
    var fileEntity = new FileEntity();
    fileEntity.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
    fileEntity.setFileType(file.getContentType());
    fileEntity.setFileSize(file.getSize());
    try {
      fileEntity.setFile(file.getBytes());
    } catch (IOException e) {
      throw new ApiException(ExceptionCode.UNHANDLED_SERVER_EXCEPTION);
    }
    // save fileEntity in db and get generated ID
    try {
      fileEntity = fileRepository.save(fileEntity);
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      throw new ApiException(ExceptionCode.FILE_ALREADY_EXISTS);
    }
    // prepare download location path
    var location = "/v1/files/" + fileEntity.getFileId();
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
  public FileEntity getFileById(String fileId) {
    return fileRepository
        .findById(fileId)
        .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND));
   }

  /**
   * Deletes the file by file ID
   *
   * @param fileId ID of the file saved in database
   */
  public void deleteFileById(String fileId) {
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
    if (ObjectUtils.isEmpty(page) ^ ObjectUtils.isEmpty(size)) {
      throw new ApiException(ExceptionCode.PARAMETER_CONSTRAINT_VIOLATION);
    }
    var resolvedPage = ObjectUtils.isEmpty(page) ? 0 : page;
    var resolvedSize = ObjectUtils.isEmpty(size) ? 10 : size;
    var pageRequest = PageRequest.of(resolvedPage, resolvedSize, Sort.by("createdAt").descending());
    List<FileEntity> fileEntities = fileRepository.findAll(pageRequest).getContent();
    return fileMapper.fileEntityListToFileDtoList(fileEntities);
  }
}
