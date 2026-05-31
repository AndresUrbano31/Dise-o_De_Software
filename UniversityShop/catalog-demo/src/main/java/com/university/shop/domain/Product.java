package com.university.shop.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * =========================================================
 *  CAPA DE DOMINIO — Entidad Product (Aggregate Root, MongoDB)
 * =========================================================
 *
 * Product es el Aggregate Root del Bounded Context "Catálogo".
 * Colección MongoDB: "products"
 *
 * RELACIÓN CON CATEGORY (cambio arquitectural):
 *   En MongoDB no se usa @ManyToOne. Se almacena solo el ID
 *   de la categoría (referencia por clave). Cuando el servicio
 *   construye ProductResponseDTO hace un findById en CategoryRepository
 *   y embebe el CategoryResponseDTO en la respuesta.
 *
 *   Diagrama lógico:
 *     categories { _id } ◄──── products { categoryId }
 *
 * @CreatedDate: asignado automáticamente por Spring Data MongoDB
 *   Auditing al persistir el documento por primera vez.
 *   Requiere @EnableMongoAuditing en MongoConfig.
 */
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    /** Código de referencia único del artículo en el catálogo. */
    @Indexed(unique = true)
    private String sku;

    private String name;

    private BigDecimal price;

    /** Fecha de alta en el catálogo. Inmutable: solo se asigna al crear. */
    @CreatedDate
    private LocalDateTime dateCreated;

    /**
     * Referencia a la categoría por su ID (String / ObjectId).
     * Indexado para acelerar filtros por categoría en searchProducts.
     */
    @Indexed
    private String categoryId;

    // ── Getters & Setters ─────────────────────────────────────────

    public String getId()                          { return id; }
    public void setId(String id)                   { this.id = id; }

    public String getSku()                         { return sku; }
    public void setSku(String sku)                 { this.sku = sku; }

    public String getName()                        { return name; }
    public void setName(String name)               { this.name = name; }

    public BigDecimal getPrice()                   { return price; }
    public void setPrice(BigDecimal price)          { this.price = price; }

    public LocalDateTime getDateCreated()          { return dateCreated; }
    public void setDateCreated(LocalDateTime d)    { this.dateCreated = d; }

    public String getCategoryId()                  { return categoryId; }
    public void setCategoryId(String categoryId)   { this.categoryId = categoryId; }
}
