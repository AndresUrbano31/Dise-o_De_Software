package com.university.shop.domain.exception;

/**
 * Indica que ya existe una categoría con el mismo nombre (regla de negocio).
 */
public class CategoryNameAlreadyExistsException extends RuntimeException {

    private final String name;

    public CategoryNameAlreadyExistsException(String name) {
        super("Ya existe una categoría con el nombre '" + name + "'.");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
