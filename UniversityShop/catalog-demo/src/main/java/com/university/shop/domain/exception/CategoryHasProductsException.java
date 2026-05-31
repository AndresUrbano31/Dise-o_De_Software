package com.university.shop.domain.exception;

/**
 * No se puede eliminar una categoría que aún tiene productos asociados.
 */
public class CategoryHasProductsException extends RuntimeException {

    private final String categoryId;

    public CategoryHasProductsException(String categoryId) {
        super("La categoría con id " + categoryId
                + " tiene productos asociados y no puede eliminarse.");
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
