package com.chathushka.file.storage.controller;

import com.chathushka.file.storage.component.Validator;
import com.chathushka.file.storage.dto.FileDto;
import com.chathushka.file.storage.entity.FileEntity;
import com.chathushka.file.storage.service.FileStorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Validated
public class FileStorageController {

  private final FileStorageService fileStorageService;
  private final Validator validator;

  public FileStorageController(FileStorageService fileStorageService, Validator validator) {
    this.fileStorageService = fileStorageService;
    this.validator = validator;
  }

  @GetMapping("/files/{fileid}")
  public ResponseEntity<ByteArrayResource> getFileById(@PathVariable("fileid") String fileId) {
    FileEntity file = fileStorageService.getFileById(fileId);
    var contentDisposition =
        ContentDisposition.builder("attachment")
            .filename(file.getFileName(), StandardCharsets.UTF_8)
            .build()
            .toString();
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(file.getFileType()))
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .body(new ByteArrayResource(file.getFile()));
  }

  @DeleteMapping("/files/{fileid}")
  public ResponseEntity<Void> deleteFileById(@PathVariable("fileid") String fileId) {
    fileStorageService.deleteFileById(fileId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> saveFile(@RequestPart("data") MultipartFile multipartFile) {
    validator.validateFile(multipartFile);
    FileDto fileDto = fileStorageService.saveFile(multipartFile);
    return ResponseEntity.created(URI.create(fileDto.getLocation())).build();
  }

  @GetMapping("/files")
  public List<FileDto> getFiles(
      @RequestParam(required = false) @Min(0) Integer page,
      @RequestParam(required = false) @Min(1) @Max(100) Integer size) {
    return fileStorageService.getAllFiles(page, size);
  }
}
