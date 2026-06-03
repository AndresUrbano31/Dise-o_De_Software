package com.university.shop.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * =========================================================
 *  CAPA DE DOMINIO — Entidad Category (MongoDB Document)
 * =========================================================
 *
 * Category agrupa productos bajo una clasificación comercial.
 * Colección MongoDB: "categories"
 *
 * Relación con Product: 1 Category → N Products
 * La referencia vive en cada Product como campo "categoryId" (String).
 * Navegación unidireccional: Product → Category (por ID).
 */
@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    /**
     * Nombre único de la categoría (ej: "Electrónica", "Ropa").
     * @Indexed(unique=true) refuerza la unicidad a nivel de BD,
     * complementando la validación en CategoryServiceImpl.
     */
    @Indexed(unique = true)
    private String name;

    private String description;

    // ── Getters & Setters ──────────────────────────────────────────

    public String getId()                        { return id; }
    public void setId(String id)                 { this.id = id; }

    public String getName()                      { return name; }
    public void setName(String name)             { this.name = name; }

    public String getDescription()               { return description; }
    public void setDescription(String desc)      { this.description = desc; }
}
