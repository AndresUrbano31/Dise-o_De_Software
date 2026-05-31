package com.university.shop.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * =========================================================
 *  CAPA DE APLICACIÓN — DTO de Entrada para Product
 *  Vista de Desarrollo (Development View — Clean Architecture)
 * =========================================================
 *
 * Movido de api/dto → application/dto para respetar la
 * regla de dependencias de Clean Architecture:
 *   - Los DTOs de la capa de Aplicación son compartidos
 *     entre la API (entrada) y la Infraestructura (salida).
 *   - La capa API depende de Application (hacia adentro).
 *   - La capa Application NO depende de API (no hacia afuera).
 *
 * CAMPO NUEVO: categoryId
 *   Permite asociar el producto a una categoría existente
 *   en el momento de la creación.
 */
public class ProductRequestDTO {

    @NotBlank(message = "El SKU no puede estar vacío")
    @Size(max = 100, message = "El SKU no puede exceder 100 caracteres")
    private String sku;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0.00")
    @Digits(integer = 10, fraction = 2,
            message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal price;

    /**
     * ID de la categoría a la que pertenece el producto.
     * La categoría debe existir previamente en el catálogo.
     * Si no existe, el Service lanza CategoryNotFoundException.
     */
    @NotBlank(message = "La categoría es obligatoria")
    private String categoryId;

    // ── Getters & Setters ──────────────────────────────────────────

    public String getSku()                         { return sku; }
    public void setSku(String sku)                 { this.sku = sku; }

    public String getName()                        { return name; }
    public void setName(String name)               { this.name = name; }

    public BigDecimal getPrice()                   { return price; }
    public void setPrice(BigDecimal price)          { this.price = price; }

    public String getCategoryId()                  { return categoryId; }
    public void setCategoryId(String categoryId)   { this.categoryId = categoryId; }
}
