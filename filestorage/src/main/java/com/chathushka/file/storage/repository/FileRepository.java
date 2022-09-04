package com.chathushka.file.storage.repository;

import com.chathushka.file.storage.entity.FileEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends PagingAndSortingRepository<FileEntity, String> {}
