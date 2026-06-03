package com.university.shop.application.dto;

/**
 * =========================================================
 *  CAPA DE APLICACIÓN — DTO de Salida para Category
 * =========================================================
 *
 * Representa la información de una categoría que la API
 * devuelve al cliente. Es un objeto de solo lectura.
 *
 * También se embebe dentro de ProductResponseDTO para mostrar
 * la categoría asociada al producto (relación del dominio).
 */
public class CategoryResponseDTO {

    private String id;
    private String name;
    private String description;

    public CategoryResponseDTO() {}

    public CategoryResponseDTO(String id, String name, String description) {
        this.id          = id;
        this.name        = name;
        this.description = description;
    }

    // ── Getters (sin setters — DTO de salida es inmutable) ─────────

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
}
