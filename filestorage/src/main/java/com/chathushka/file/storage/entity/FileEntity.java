package com.chathushka.file.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Persists uploaded file metadata, content, and audit timestamps.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "file", uniqueConstraints = @UniqueConstraint(columnNames = {"fileName", "fileType", "fileSize"}))
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String fileId;

    private String fileName;
    private String fileType;
    private long fileSize;

    @Lob
    private byte[] file;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Returns the generated identifier for this file.
     *
     * @return the file identifier
     */
    public String getFileId() {
        return this.fileId;
    }

    /**
     * Sets the file identifier.
     *
     * @param fileId the identifier to assign
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * Returns the original filename.
     *
     * @return the filename
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Sets the original filename.
     *
     * @param fileName the filename to assign
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the stored media type.
     *
     * @return the media type
     */
    public String getFileType() {
        return this.fileType;
    }

    /**
     * Sets the stored media type.
     *
     * @param fileType the media type to assign
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * Returns the file size in bytes.
     *
     * @return the size in bytes
     */
    public long getFileSize() {
        return this.fileSize;
    }

    /**
     * Sets the file size in bytes.
     *
     * @param fileSize the size to assign
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Returns the stored file content.
     *
     * @return the file bytes
     */
    public byte[] getFile() {
        return this.file;
    }

    /**
     * Sets the stored file content.
     *
     * @param file the file bytes to assign
     */
    public void setFile(byte[] file) {
        this.file = file;
    }

    /**
     * Returns when the file record was created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Sets the record creation timestamp.
     *
     * @param createdAt the timestamp to assign
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns when the file record was last updated.
     *
     * @return the update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Sets the record update timestamp.
     *
     * @param updatedAt the timestamp to assign
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
