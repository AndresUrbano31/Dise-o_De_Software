package com.university.shop.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el endpoint de login.
 * El cliente envía usuario y contraseña, recibe un JWT.
 */
public class AuthRequestDTO {

    @NotBlank(message = "El usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }
}
