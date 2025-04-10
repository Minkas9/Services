package com.example.Services.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 * This class configures the OpenAPI documentation for the application
 * using the springdoc-openapi library.
 * 
 * It defines API groups and packages to scan for API documentation.
 * This configuration works in conjunction with SwaggerConfig to provide
 * comprehensive API documentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates a GroupedOpenApi bean for the public API.
     * This bean configures:
     * - The API group name ("public")
     * - The packages to scan for API controllers
     * 
     * This configuration allows the OpenAPI UI to display the API
     * documentation in a structured and organized manner.
     * 
     * @return Configured GroupedOpenApi bean
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.example.Services.controller")
                .build();
    }
}