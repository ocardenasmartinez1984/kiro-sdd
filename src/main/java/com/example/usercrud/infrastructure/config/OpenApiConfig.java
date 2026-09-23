package com.example.usercrud.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userCrudOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User CRUD API")
                        .version("v1")
                        .description("API REST para el mantenimiento de usuarios (crear, consultar, "
                                + "actualizar y eliminar). Construida con Spring Boot 3, Java 21, Lombok y H2.")
                        .contact(new Contact()
                                .name("User CRUD")
                                .email("soporte@example.com"))
                        .license(new License()
                                .name("Uso educativo")));
    }
}
