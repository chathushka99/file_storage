package com.chathushka.file.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
//@EnableAspectJAutoProxy
public class FileStorageApplication {
  public static void main(String[] args) {
    SpringApplication.run(FileStorageApplication.class, args);
  }
}
