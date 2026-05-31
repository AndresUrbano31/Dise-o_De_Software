package com.university.shop.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración CORS para que frontends (React/Vite en localhost:5173,
 * Angular, Vue, etc.) puedan consumir la API sin ser bloqueados por el navegador.
 *
 * Los orígenes permitidos se leen desde application.yml:
 *   app.cors.allowed-origins:
 *     - http://localhost:5173
 *     - http://localhost:3000
 *
 * Se usa @ConfigurationProperties (no @Value) para poder inyectar una lista YAML.
 */
@Configuration
@ConfigurationProperties(prefix = "app.cors")
public class CorsConfig {

    private List<String> allowedOrigins = List.of("http://localhost:5173", "http://localhost:3000");

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Location", "Content-Disposition"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
