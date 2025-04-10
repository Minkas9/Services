package com.example.Services.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * This class configures the OpenAPI specification for the application
 * using the springdoc-openapi library.
 * 
 * It defines:
 * - API information (title, version, description)
 * - Security requirements (JWT Bearer token authentication)
 * - Security schemes for the API
 * 
 * This configuration works in conjunction with OpenApiConfig to provide
 * comprehensive API documentation accessible via the Swagger UI.
 */
@Configuration
public class SwaggerConfig {

        /**
         * Creates a custom OpenAPI bean with API information and security
         * configuration.
         * This bean configures:
         * - Basic API information (title, version, description)
         * - Security requirement for JWT Bearer token authentication
         * - Security scheme definition for the Bearer token
         * 
         * This configuration enables the Swagger UI to:
         * - Display API information
         * - Allow users to input JWT tokens for authenticated requests
         * - Provide a clear understanding of the API's security requirements
         * 
         * @return Configured OpenAPI bean
         */
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Services API")
                                                .version("1.0")
                                                .description("API documentation for Services application"))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new Components()
                                                .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")));
        }
}