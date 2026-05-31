package com.university.shop.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para actualizar un producto existente (PUT).
 *
 * NOTA: el SKU NO es actualizable (es el código único de negocio
 * e inmutable por definición — cambiar el SKU rompería integraciones
 * con sistemas externos, facturas, etc.).
 */
public class ProductUpdateDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 255)
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoryId;

    // ── Getters & Setters ──────────────────────────────────────────
    public String getName()                        { return name; }
    public void setName(String name)               { this.name = name; }

    public BigDecimal getPrice()                   { return price; }
    public void setPrice(BigDecimal price)          { this.price = price; }

    public String getCategoryId()                  { return categoryId; }
    public void setCategoryId(String categoryId)   { this.categoryId = categoryId; }
}
