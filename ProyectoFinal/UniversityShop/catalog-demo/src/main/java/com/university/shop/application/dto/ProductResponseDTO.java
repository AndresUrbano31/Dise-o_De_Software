package com.university.shop.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * =========================================================
 *  CAPA DE APLICACIÓN — DTO de Salida para Product
 *  Vista de Desarrollo (Development View — Clean Architecture)
 * =========================================================
 *
 * CAMPO NUEVO: category (CategoryResponseDTO)
 *   Embebe la información de la categoría directamente en
 *   la respuesta del producto, exponiendo la relación del
 *   dominio sin necesidad de una segunda llamada HTTP.
 *
 *   Ejemplo de JSON devuelto:
 *   {
 *     "id": 1,
 *     "sku": "LAPTOP-001",
 *     "name": "Laptop Dell XPS",
 *     "price": 999.99,
 *     "dateCreated": "2026-05-02T01:20:00",
 *     "category": {
 *       "id": 1,
 *       "name": "Electrónica",
 *       "description": "Dispositivos electrónicos"
 *     }
 *   }
 */
public class ProductResponseDTO {

    private String id;
    private String sku;
    private String name;
    private BigDecimal price;
    private LocalDateTime dateCreated;

    /**
     * Objeto anidado que representa la categoría del producto.
     * Patrón: Embedded DTO (evita URLs adicionales para /categories/{id}).
     */
    private CategoryResponseDTO category;

    public ProductResponseDTO() {}

    public ProductResponseDTO(String id, String sku, String name,
                               BigDecimal price, LocalDateTime dateCreated,
                               CategoryResponseDTO category) {
        this.id          = id;
        this.sku         = sku;
        this.name        = name;
        this.price       = price;
        this.dateCreated = dateCreated;
        this.category    = category;
    }

    // ── Getters ────────────────────────────────────────────────────

    public String getId()                    { return id; }
    public String getSku()                   { return sku; }
    public String getName()                  { return name; }
    public BigDecimal getPrice()             { return price; }
    public LocalDateTime getDateCreated()    { return dateCreated; }
    public CategoryResponseDTO getCategory() { return category; }
}
