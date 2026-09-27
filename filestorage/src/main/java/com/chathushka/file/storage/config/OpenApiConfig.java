package com.chathushka.file.storage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures metadata for the generated OpenAPI specification.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds the API metadata included in the generated OpenAPI document.
     *
     * @return the configured OpenAPI definition
     */
    @Bean
    public OpenAPI fileStorageOpenApi() {
        return new OpenAPI() // ls
                .info(new Info() // ls
                        .title("File Storage API") // ls
                        .version("1.0.0") // ls
                        .description("API for uploading, listing, downloading, and deleting video files."));
    }
}
