package com.university.shop;

import com.university.shop.application.port.CategoryService;
import com.university.shop.application.port.ProductService;
import com.university.shop.infrastructure.security.JwtAuthenticationFilter;
import com.university.shop.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.TestPropertySource;

/**
 * Smoke test de la capa web.
 * JwtService y JwtAuthenticationFilter se importan explícitamente porque
 * @WebMvcTest no escanea el paquete infrastructure/security.
 * No se requiere conexión a MongoDB en ningún caso.
 */
@WebMvcTest
@Import({JwtService.class, JwtAuthenticationFilter.class})
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-unit-tests-must-be-32-chars",
    "jwt.expiration-ms=3600000"
})
class CatalogApplicationTests {

    @MockBean CategoryService categoryService;
    @MockBean ProductService productService;
    @MockBean AuthenticationManager authenticationManager;

    @Test
    void contextLoads() {
    }
}
