package com.university.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * =========================================================
 *  PUNTO DE ENTRADA  —  Spring Boot Application
 * =========================================================
 *
 * @SpringBootApplication activa automáticamente:
 *  - @ComponentScan    → escanea todos los beans del paquete
 *  - @EnableAutoConfiguration → configura JPA, Web, Validation…
 *  - @Configuration    → esta clase puede declarar @Bean adicionales
 *
 * El contenedor IoC de Spring gestiona el ciclo de vida de:
 *  - ProductController  (@RestController → bean singleton)
 *  - ProductService     (@Service        → bean singleton)
 *  - ProductRepository  (@Repository     → proxy generado por Spring Data)
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class CatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
        System.out.println("""
            \n========================================
             Catálogo de Productos — API Lista
            ========================================
             Swagger:  http://localhost:8080/swagger-ui.html
             Health:   http://localhost:8080/api/v1/health
             Products: http://localhost:8080/api/v1/products
             Login:    POST http://localhost:8080/api/v1/auth/login
            ========================================
            """);
    }
}
