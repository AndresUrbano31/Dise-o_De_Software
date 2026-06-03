package com.university.shop.application.dto;

/**
 * DTO de salida para el endpoint de login.
 * Contiene el token JWT que el cliente debe enviar
 * en el header "Authorization: Bearer <token>" para
 * acceder a los endpoints protegidos.
 */
public class AuthResponseDTO {

    private final String token;
    private final String type = "Bearer";
    private final String message = "Autenticación exitosa. Incluye este token en el header Authorization.";

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken()   { return token; }
    public String getType()    { return type; }
    public String getMessage() { return message; }
}
