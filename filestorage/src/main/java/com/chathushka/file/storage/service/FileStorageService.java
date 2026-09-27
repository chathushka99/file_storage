package com.chathushka.file.storage.service;

import com.chathushka.file.storage.dto.FileContentDto;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;
import com.chathushka.file.storage.enums.ExceptionCode;
import com.chathushka.file.storage.exception.ApiException;
import com.chathushka.file.storage.mapper.FileMapper;
import com.chathushka.file.storage.repository.FileRepository;

import java.io.IOException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Coordinates file persistence and maps stored records to API data.
 */
@Service
public class FileStorageService {

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    /**
     * Creates a service using the repository and entity mapper.
     *
     * @param fileRepository repository used to persist file records
     * @param fileMapper     mapper used to create API DTOs
     */
    public FileStorageService(FileRepository fileRepository, FileMapper fileMapper) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
    }

    /**
     * Stores an uploaded file and returns its download location.
     *
     * @param file uploaded file
     * @return metadata DTO with a download location
     * @throws ApiException if the file is empty, cannot be read, or duplicates an existing file
     */
    @Transactional
    public FileDto saveFile(MultipartFile file) {
        if (file == null) {
            throw new ApiException(ExceptionCode.FILE_EMPTY);
        }

        final FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFileSize(file.getSize());

        try {
            fileEntity.setFile(file.getBytes());
        } catch (IOException exception) {
            throw new ApiException(ExceptionCode.UNHANDLED_SERVER_EXCEPTION);
        }

        final FileEntity savedFileEntity;
        try {
            savedFileEntity = this.fileRepository.saveAndFlush(fileEntity);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(ExceptionCode.FILE_ALREADY_EXISTS);
        }

        final FileDto mappedFileDto = this.fileMapper.fileEntityToFileDto(savedFileEntity);
        return new FileDto( // ls
                mappedFileDto.fileId(), // ls
                mappedFileDto.fileName(), // ls
                "/v1/files/" + mappedFileDto.fileId(), // ls
                mappedFileDto.size(), // ls
                mappedFileDto.createdAt());
    }

    /**
     * Finds a file and returns only the content needed by the download endpoint.
     *
     * @param fileId identifier of the stored file
     * @return content and metadata for the download response
     * @throws ApiException if no file has the requested identifier
     */
    @Transactional(readOnly = true)
    public FileContentDto getFileById(String fileId) {
        final FileEntity fileEntity = // ls
                this.fileRepository // ls
                        .findById(fileId) // ls
                        .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND));

        return this.fileMapper.fileEntityToFileContentDto(fileEntity);
    }

    /**
     * Deletes a stored file by its identifier.
     *
     * @param fileId identifier of the stored file
     * @throws ApiException if no file has the requested identifier
     */
    @Transactional
    public void deleteFileById(String fileId) {
        if (!this.fileRepository.existsById(fileId)) {
            throw new ApiException(ExceptionCode.NOT_FOUND);
        }

        this.fileRepository.deleteById(fileId);
    }

    /**
     * Returns file metadata using the requested page or the default page.
     *
     * @param page zero-based page index, or {@code null} to use the default
     * @param size page size, or {@code null} to use the default
     * @return metadata for files on the selected page
     * @throws ApiException if only one pagination parameter is supplied
     */
    @Transactional(readOnly = true)
    public List<FileDto> getAllFiles(Integer page, Integer size) {
        if (ObjectUtils.isEmpty(page) ^ ObjectUtils.isEmpty(size)) {
            throw new ApiException(ExceptionCode.PARAMETER_CONSTRAINT_VIOLATION);
        }

        final int resolvedPage = ObjectUtils.isEmpty(page) ? 0 : page;
        final int resolvedSize = ObjectUtils.isEmpty(size) ? 10 : size;
        final PageRequest pageRequest = // ls
                PageRequest.of(resolvedPage, resolvedSize, Sort.by("createdAt").descending());
        final List<FileEntity> fileEntities = this.fileRepository.findAll(pageRequest).getContent();

        return this.fileMapper.fileEntityListToFileDtoList(fileEntities);
    }
}
