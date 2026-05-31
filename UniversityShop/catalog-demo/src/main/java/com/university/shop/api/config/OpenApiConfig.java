package com.university.shop.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración programática de OpenAPI 3 / Swagger UI.
 *
 * Acceso: http://localhost:8080/swagger-ui.html
 * Flujo para endpoints protegidos:
 *   1. POST /api/v1/auth/login → copia el token
 *   2. Clic en "Authorize" → pega el token
 *   3. Los endpoints protegidos ya incluyen el header automáticamente
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catalog API")
                        .version("1.0.0")
                        .description("API REST para gestión del catálogo digital de la tienda universitaria. " +
                                "Permite administrar productos y categorías con autenticación JWT. " +
                                "Endpoints de lectura públicos; escritura requiere Bearer token.")
                        .contact(new Contact()
                                .name("Equipo catalog-demo")
                                .email("aurbano535@gmail.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Desarrollo local"))
                .addServersItem(new Server()
                        .url("https://api.catalog-demo.example.com")
                        .description("Producción (placeholder)"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido en POST /api/v1/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
