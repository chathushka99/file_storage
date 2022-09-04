package com.chathushka.file.storage.controller;

import com.chathushka.file.storage.component.Validator;
import com.chathushka.file.storage.dto.ApiResponse;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;
import com.chathushka.file.storage.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
@Validated
public class FileStorageController {

  private FileStorageService fileStorageService;
  private Validator validator;

  @GetMapping("/files/{fileid}")
  public ResponseEntity<?> getFileById(@PathVariable("fileid") String fileId) {
    FileEntity file = fileStorageService.getFileById(fileId);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(file.getFileType()))
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
        .body(new ByteArrayResource(file.getFile()));
  }

  @DeleteMapping("/files/{fileid}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT) // 204 no body will be shown
  public ApiResponse<?> deleteFileById(@PathVariable("fileid") String fileId) {
    fileStorageService.deleteFileById(fileId);
    return ApiResponse.builder().description("File was successfully removed").build();
  }

  @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(value = HttpStatus.CREATED)
  public ApiResponse<?> saveFile(@RequestPart("file") MultipartFile multipartFile) {
    validator.validateFile(multipartFile);
    FileDto fileDto = fileStorageService.saveFile(multipartFile);
    return ApiResponse.builder().description("File uploaded").result(fileDto).build();
  }

  @GetMapping("/files")
  @ResponseStatus(value = HttpStatus.OK)
  public ApiResponse<?> getFiles(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) @Max(10) Integer size) {
    List<FileDto> fileDtoList = fileStorageService.getAllFiles(page, size);
    return ApiResponse.builder().result(fileDtoList).description("File list").build();
  }
}
