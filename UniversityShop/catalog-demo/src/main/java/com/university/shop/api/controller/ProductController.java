package com.university.shop.api.controller;

import com.university.shop.application.dto.PagedResponseDTO;
import com.university.shop.application.dto.ProductRequestDTO;
import com.university.shop.application.dto.ProductResponseDTO;
import com.university.shop.application.dto.ProductUpdateDTO;
import com.university.shop.application.port.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * =========================================================
 *  REST Controller de Productos — 6 endpoints CRUD completos
 * =========================================================
 *
 *  PÚBLICO   (sin token):  GET /api/v1/products, GET /api/v1/products/{id}
 *  PROTEGIDO (con JWT):    POST, PUT, DELETE
 *
 *  Para endpoints protegidos, incluir en el header:
 *    Authorization: Bearer <token_obtenido_en_/api/v1/auth/login>
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "CRUD completo del catálogo de productos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ── GET /api/v1/products?name=&categoryId=&page=0&size=10 ────

    @GetMapping
    @Operation(
        summary     = "Listar/buscar productos con paginación",
        description = "Retorna productos paginados. Filtros opcionales: nombre (parcial) y categoría."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada de productos")
    public ResponseEntity<PagedResponseDTO<ProductResponseDTO>> searchProducts(
            @Parameter(description = "Filtro por nombre (parcial)")
            @RequestParam(required = false) String name,

            @Parameter(description = "Filtro por ID de categoría")
            @RequestParam(required = false) String categoryId,

            @Parameter(description = "Número de página (empieza en 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Cantidad de resultados por página")
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                productService.searchProducts(name, categoryId, page, size));
    }

    // ── GET /api/v1/products/all ──────────────────────────────────

    @GetMapping("/all")
    @Operation(summary = "Listar todos los productos sin paginación",
               description = "Útil para poblar selects/dropdowns en el frontend.")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ── GET /api/v1/products/{id} ─────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                     content = @Content)
    })
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ── POST /api/v1/products  (🔒 requiere JWT) ──────────────────

    @PostMapping
    @Operation(
        summary   = "Crear un nuevo producto",
        security  = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado",
                     content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no existe", content = @Content),
        @ApiResponse(responseCode = "409", description = "SKU duplicado", content = @Content)
    })
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    // ── PUT /api/v1/products/{id}  (🔒 requiere JWT) ─────────────

    @PutMapping("/{id}")
    @Operation(
        summary      = "Actualizar un producto existente",
        description  = "Modifica nombre, precio y/o categoría. El SKU NO es modificable.",
        security     = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Producto o categoría no encontrado",
                     content = @Content)
    })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductUpdateDTO request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // ── DELETE /api/v1/products/{id}  (🔒 requiere JWT) ──────────

    @DeleteMapping("/{id}")
    @Operation(
        summary   = "Eliminar un producto",
        security  = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}
