package com.chathushka.file.storage.repository;

import com.chathushka.file.storage.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides CRUD and paging access to stored file records.
 */
public interface FileRepository extends JpaRepository<FileEntity, String> {
}
