package com.university.shop.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * =========================================================
 *  CAPA DE APLICACIÓN — DTO de Entrada para Category
 * =========================================================
 *
 * Transporta los datos que el cliente envía para crear
 * una categoría. Validado por Bean Validation antes de
 * llegar al Service.
 */
public class CategoryRequestDTO {

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    // ── Getters & Setters ──────────────────────────────────────────

    public String getName()                      { return name; }
    public void setName(String name)             { this.name = name; }

    public String getDescription()               { return description; }
    public void setDescription(String desc)      { this.description = desc; }
}
