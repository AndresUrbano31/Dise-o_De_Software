package com.university.shop.domain.exception;

/**
 * Se lanza cuando se busca un producto por ID y no existe.
 * El GlobalExceptionHandler la mapea a HTTP 404 Not Found.
 */
public class ProductNotFoundException extends RuntimeException {

    private final String productId;

    public ProductNotFoundException(String productId) {
        super("El producto con ID " + productId + " no fue encontrado.");
        this.productId = productId;
    }

    public String getProductId() { return productId; }
}
