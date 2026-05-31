package com.university.shop.domain.exception;

/**
 * =========================================================
 *  CAPA DE DOMINIO — Excepción de Dominio
 *  Vista Lógica (Logical View — 4+1 Model)
 * =========================================================
 *
 * SkuAlreadyExistsException es una excepción de DOMINIO:
 * representa una violación de una regla de negocio explícita
 * ("el SKU debe ser único en el catálogo").
 *
 * Por qué extends RuntimeException (unchecked):
 *   - El llamador (Service) no tiene que declarar "throws",
 *     manteniendo el código más limpio.
 *   - El GlobalExceptionHandler la captura centralizadamente.
 *
 * Por qué NO es una excepción genérica (IllegalArgumentException):
 *   - Permite @ExceptionHandler específico → HTTP 409 Conflict.
 *   - Comunica la intención del dominio más claramente.
 */
public class SkuAlreadyExistsException extends RuntimeException {

    private final String sku;

    public SkuAlreadyExistsException(String sku) {
        super("El SKU '" + sku + "' ya está registrado en el catálogo.");
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }
}
