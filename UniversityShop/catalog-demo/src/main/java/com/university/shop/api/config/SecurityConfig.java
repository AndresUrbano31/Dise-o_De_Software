package com.university.shop.api.config;

import com.university.shop.infrastructure.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.Instant;

/**
 * =========================================================
 *  Configuración de Seguridad Spring Security + JWT
 * =========================================================
 *
 * Reglas de acceso:
 *
 *  PÚBLICO (sin token):
 *    GET  /api/v1/products/**   → catálogo de productos (lectura)
 *    GET  /api/v1/categories/** → categorías (lectura)
 *    ANY  /api/v1/auth/**       → login
 *    GET  /api/v1/health        → health check para el frontend
 *    ANY  /swagger-ui/**, /v3/api-docs/** → documentación OpenAPI
 *
 *  PROTEGIDO (requiere "Authorization: Bearer <token>"):
 *    POST/PUT/DELETE /api/v1/products/**   → gestión de productos
 *    POST/PUT/DELETE /api/v1/categories/** → gestión de categorías
 *
 * Usuario de demo:  admin / admin123
 * CORS habilitado via CorsConfig (orígenes: app.cors.allowed-origins en application.yml)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CORS: delega al bean CorsConfig para origins/methods/headers
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // CSRF deshabilitado: API REST stateless, tokens JWT en header
                .csrf(csrf -> csrf.disable())

                // Sin sesiones HTTP: cada request se autentica con el JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Reglas de autorización por endpoint
                .authorizeHttpRequests(auth -> auth

                        // Público: lectura del catálogo
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/products/**",
                                "/api/v1/categories/**").permitAll()

                        // Público: autenticación, health y documentación
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/health",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/v3/api-docs/**").permitAll()

                        // Todo lo demás requiere JWT válido
                        .anyRequest().authenticated()
                )

                // 401 para no-autenticado; 403 queda reservado para autenticado-sin-permisos
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authException) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write(String.format(
                                    "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\"," +
                                    "\"message\":\"Token ausente, inválido o expirado.\"," +
                                    "\"path\":\"%s\"}",
                                    Instant.now().toString(),
                                    req.getRequestURI()
                            ));
                        })
                )

                // Filtro JWT antes del filtro de usuario/contraseña estándar
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * Gestor de autenticación: Spring lo usa para verificar
     * usuario/contraseña en el endpoint de login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Usuario en memoria para demo académico.
     * Credenciales: admin / admin123
     *
     * En producción: implementar UserDetailsService con BD de usuarios.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    /** BCrypt es el algoritmo estándar para hashear contraseñas. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
