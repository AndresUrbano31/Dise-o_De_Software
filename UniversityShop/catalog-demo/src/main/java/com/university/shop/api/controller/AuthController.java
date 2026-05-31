package com.university.shop.api.controller;

import com.university.shop.application.dto.AuthRequestDTO;
import com.university.shop.application.dto.AuthResponseDTO;
import com.university.shop.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * =========================================================
 *  Controlador de Autenticación — Login con JWT
 * =========================================================
 *
 * Credenciales de demo: admin / admin123
 *
 * Uso:
 *   POST /api/v1/auth/login
 *   Body: { "username": "admin", "password": "admin123" }
 *   Respuesta: { "token": "eyJhbGci...", "type": "Bearer" }
 *
 *   Luego en peticiones protegidas:
 *   Header → Authorization: Bearer eyJhbGci...
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login y obtención de token JWT")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService  = jwtService;
    }

    @PostMapping("/login")
    @Operation(
        summary     = "Iniciar sesión",
        description = "Autentica al usuario y devuelve un token JWT. " +
                      "Credenciales de demo: admin / admin123"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso — incluye el token"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody AuthRequestDTO request) {

        // Spring Security verifica el usuario/contraseña contra el UserDetailsService
        // Si son incorrectos, lanza BadCredentialsException (HTTP 401 desde GlobalHandler)
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Si llegamos aquí, las credenciales son correctas → generar token
        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
