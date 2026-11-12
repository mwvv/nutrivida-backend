package com.nutrivida.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nutrividaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NutriVida API")
                        .version("1.0")
                        .description("API REST de NutriVida para la gestión de productos, usuarios y ventas."));
    }
}
