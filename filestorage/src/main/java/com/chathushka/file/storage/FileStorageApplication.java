package com.chathushka.file.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Starts the file storage service and enables JPA audit timestamps.
 */
@SpringBootApplication
@EnableJpaAuditing
public class FileStorageApplication {

    /**
     * Launches the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(FileStorageApplication.class, args);
    }
}
