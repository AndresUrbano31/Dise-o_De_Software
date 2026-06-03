package com.university.shop.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * =========================================================
 *  Filtro JWT — Intercepta cada petición HTTP
 * =========================================================
 *
 * OncePerRequestFilter garantiza que este filtro se ejecute
 * exactamente UNA VEZ por petición HTTP.
 *
 * Flujo por cada petición:
 *   1. Lee el header "Authorization"
 *   2. Extrae el token (quita "Bearer ")
 *   3. Valida el token con JwtService
 *   4. Si es válido, crea la autenticación en SecurityContext
 *   5. Pasa la petición al siguiente filtro / controller
 *
 * Si el token no viene o es inválido, la petición continúa
 * SIN autenticación → Spring Security la rechazará si el
 * endpoint requiere autenticación (403 Forbidden).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Leer el header de autorización
        String authHeader = request.getHeader("Authorization");

        // 2. Si no hay token o no empieza con "Bearer ", pasar la petición
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (sin el prefijo "Bearer ")
        String token = authHeader.substring(7);

        // 4. Validar el token y autenticar
        if (jwtService.isValid(token)) {
            String username = jwtService.extractUsername(token);

            // Crear objeto de autenticación con rol ADMIN
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    );

            // Registrar la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. Pasar al siguiente eslabón de la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
