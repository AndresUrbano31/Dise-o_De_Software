package com.university.shop.api.exception;

import com.university.shop.domain.exception.CategoryHasProductsException;
import com.university.shop.domain.exception.CategoryNameAlreadyExistsException;
import com.university.shop.domain.exception.CategoryNotFoundException;
import com.university.shop.domain.exception.ProductNotFoundException;
import com.university.shop.domain.exception.SkuAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Traduce excepciones de dominio y de seguridad a respuestas HTTP
 * con el formato uniforme ApiErrorResponse que el frontend puede tipar.
 *
 * Mapeo:
 *   ProductNotFoundException / CategoryNotFoundException  → 404
 *   SkuAlreadyExistsException / CategoryNameAlreadyExists → 409
 *   CategoryHasProductsException                          → 409
 *   DataIntegrityViolationException                       → 409
 *   MethodArgumentNotValidException                       → 400 + fieldErrors
 *   BadCredentialsException / AuthenticationException     → 401
 *   AccessDeniedException                                 → 403
 *   Exception (fallback)                                  → 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ProductNotFoundException.class, CategoryNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException ex,
                                                            HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(404, "Not Found", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler({SkuAlreadyExistsException.class,
                        CategoryNameAlreadyExistsException.class,
                        CategoryHasProductsException.class,
                        DataIntegrityViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleConflict(RuntimeException ex,
                                                            HttpServletRequest req) {
        String message = ex instanceof DataIntegrityViolationException
                ? "Violación de integridad de datos (posible valor duplicado)."
                : ex.getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(409, "Conflict", message, req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest req) {
        List<ApiErrorResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ApiErrorResponse.FieldErrorDetail(fe.getField(),
                        fe.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.ofValidation("Validación de datos fallida",
                        req.getRequestURI(), fieldErrors));
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(Exception ex,
                                                                HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of(401, "Unauthorized",
                        "Credenciales incorrectas o token inválido.", req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(AccessDeniedException ex,
                                                             HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of(403, "Forbidden",
                        "No tienes permisos para realizar esta operación.", req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex,
                                                          HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(500, "Internal Server Error",
                        "Error interno del servidor.", req.getRequestURI()));
    }
}
