package com.university.shop.domain.exception;

/**
 * =========================================================
 *  CAPA DE DOMINIO — Excepción de Dominio
 *  Vista Lógica (Logical View — 4+1 Model)
 * =========================================================
 *
 * CategoryNotFoundException se lanza cuando se busca una
 * categoría por ID y no existe en el catálogo.
 *
 * El GlobalExceptionHandler la mapea a HTTP 404 Not Found,
 * desacoplando la lógica HTTP de la lógica de dominio.
 */
public class CategoryNotFoundException extends RuntimeException {

    private final String categoryId;

    public CategoryNotFoundException(String categoryId) {
        super("La categoría con ID " + categoryId + " no fue encontrada.");
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
