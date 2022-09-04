package com.chathushka.file.storage.service;

import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;
import com.chathushka.file.storage.exception.ApiException;
import com.chathushka.file.storage.repository.FileRepository;
import com.chathushka.file.storage.mapper.FileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

  @Mock private FileRepository fileRepository;
  @Mock private FileMapper fileMapper;
  @Mock private MockHttpServletRequest mockRequest;
  @InjectMocks private FileStorageService fileStorageService;

  @BeforeEach
  public void init() {
    mockRequest.setContextPath("localhost:8080");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
  }

  @Test
  void saveFile_all_ok() {
    // prepare test data
    FileEntity file = new FileEntity();
    file.setFileSize(123);
    file.setFileName("video_file.mp4");
    file.setFileType("video/mp4");
    FileEntity returnFile = file;
    returnFile.setFileId("unit-test-id");
    FileDto fileDto = new FileDto();
    fileDto.setLocation("/v1/files/unit-test-id");
    when(fileRepository.save(any(FileEntity.class))).thenReturn(file);
    MockMultipartFile fileFile =
        new MockMultipartFile("video_file", "video_file.mp4", "video/mp4", new byte[120]);
    // execute test method
    FileDto created = fileStorageService.saveFile(fileFile);
    // verify results
    assertEquals("/v1/files/unit-test-id", created.getLocation());
    verify(fileRepository).save(any(FileEntity.class));
  }

  @Test
  void saveFile_file_already_exist() {
    // prepare test data
    MockMultipartFile fileFile =
            new MockMultipartFile("video_file", "video_file.mp4", "video/mp4", new byte[120]);
    doThrow(new DataIntegrityViolationException("File Exist")).when(fileRepository).save(any(FileEntity.class));
    // execute test method
    ApiException apiException =
            assertThrows(
                    ApiException.class,
                    () -> {
                      fileStorageService.saveFile(fileFile);
                    });
    // verify
    assertEquals("FSA007", apiException.getErrorCode());
    // verify
    verify(fileRepository).save(any(FileEntity.class));
  }

  @Test
  void getFileById_All_ok() {
    // prepare test data
    FileEntity file = new FileEntity();
    file.setFileId("unit-test-1");
    when(fileRepository.findById(file.getFileId())).thenReturn(Optional.of(file));
    // execute test method
    FileEntity expected = fileStorageService.getFileById(file.getFileId());
    // verify
    assertEquals(expected, file);
    verify(fileRepository).findById(file.getFileId());
  }

  @Test
  void deleteFileById_All_OK() {
    // prepare test data
    FileEntity file = new FileEntity();
    file.setFileId("unit-test-1");
    doNothing().when(fileRepository).deleteById(anyString());
    // execute test method
    fileStorageService.deleteFileById(file.getFileId());
    // verify
    verify(fileRepository).deleteById(file.getFileId());
  }

  @Test
  void deleteFileById_No_File() {
    // prepare test data
    FileEntity file = new FileEntity();
    file.setFileId("unit-test-1");
    doThrow(new EmptyResultDataAccessException(0)).when(fileRepository).deleteById(anyString());
    // execute test method
    ApiException apiException =
        assertThrows(
            ApiException.class,
            () -> {
              fileStorageService.deleteFileById(file.getFileId());
            });
    // verify
    assertEquals("FSA003", apiException.getErrorCode());
    // verify
    verify(fileRepository).deleteById(anyString());
  }

  @Test
  void getAllFiles_ALL_OK() {
    // prepare test data
    List<FileEntity> files = new ArrayList();
    FileEntity file = new FileEntity();
    file.setCreatedAt(LocalDateTime.now().minusDays(1));
    file.setFileName("video_file_ut");
    file.setFileId("unit-test-1");
    file.setFileSize(123);
    files.add(file);
    List<FileDto> fileDtos = new ArrayList();
    FileDto fileDto = new FileDto();
    fileDto.setFileName("video_file_ut");
    fileDto.setFileId("unit-test-1");
    fileDtos.add(fileDto);
    when(fileRepository.findAll(any(Sort.class))).thenReturn(files);
    when(fileMapper.fileEntityListToFileDtoList(anyList())).thenReturn(fileDtos);
    // execute test method
    List<FileDto> actual = fileStorageService.getAllFiles(null, 1);
    // verify
    assertEquals("video_file_ut", actual.get(0).getFileName());
    verify(fileRepository).findAll(any(Sort.class));
    verify(fileMapper).fileEntityListToFileDtoList(anyList());
  }

  @Test
  void getAllFiles_ALL_OK_Pageable() {
    // prepare test data
    List<FileEntity> files = new ArrayList();
    FileEntity file = new FileEntity();
    file.setCreatedAt(LocalDateTime.now().minusDays(1));
    file.setFileName("video_file_ut");
    file.setFileId("unit-test-1");
    file.setFileSize(123);
    files.add(file);
    List<FileDto> fileDtos = new ArrayList();
    FileDto fileDto = new FileDto();
    fileDto.setFileName("video_file_ut");
    fileDto.setFileId("unit-test-1");
    fileDtos.add(fileDto);
    var pageRequest = PageRequest.of(0, 5, Sort.by("createdAt").descending());
    Page<FileEntity> fileEntityPage = new PageImpl(files);
    when(fileRepository.findAll(any(PageRequest.class))).thenReturn(fileEntityPage);
    when(fileMapper.fileEntityListToFileDtoList(anyList())).thenReturn(fileDtos);
    // execute test method
    List<FileDto> actual = fileStorageService.getAllFiles(0, 5);
    // verify
    assertEquals("video_file_ut", actual.get(0).getFileName());
    verify(fileRepository).findAll(any(PageRequest.class));
    verify(fileMapper).fileEntityListToFileDtoList(anyList());
  }

}
