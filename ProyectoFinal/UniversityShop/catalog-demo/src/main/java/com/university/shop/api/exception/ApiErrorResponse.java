package com.university.shop.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Estructura unificada de respuesta de error que el frontend puede tipar con confianza.
 *
 * JSON resultante:
 * {
 *   "timestamp": "2026-05-30T12:00:00Z",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "El producto con ID abc no fue encontrado.",
 *   "path": "/api/v1/products/abc",
 *   "fieldErrors": [...]   // solo en 400 de validación
 * }
 *
 * fieldErrors se omite del JSON cuando es null (@JsonInclude.NON_NULL).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDetail> fieldErrors
) {
    /** Detalle de un campo inválido (usado en MethodArgumentNotValidException). */
    public record FieldErrorDetail(String field, String message) {}

    /** Constructor de conveniencia para errores sin fieldErrors. */
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(Instant.now().toString(), status, error, message, path, null);
    }

    /** Constructor de conveniencia para errores de validación con fieldErrors. */
    public static ApiErrorResponse ofValidation(String message, String path,
                                                 List<FieldErrorDetail> fieldErrors) {
        return new ApiErrorResponse(Instant.now().toString(), 400, "Bad Request",
                message, path, fieldErrors);
    }
}
