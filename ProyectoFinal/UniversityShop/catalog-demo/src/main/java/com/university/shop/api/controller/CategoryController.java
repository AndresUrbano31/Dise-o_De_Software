package com.university.shop.api.controller;

import com.university.shop.application.dto.CategoryRequestDTO;
import com.university.shop.application.dto.CategoryResponseDTO;
import com.university.shop.application.port.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * =========================================================
 *  CAPA DE API — REST Controller de Categorías
 *  Vista de Contexto (Context View — 4+1 Model)
 * =========================================================
 *
 * CategoryController expone los endpoints para gestionar
 * las categorías del catálogo. Lectura pública; alta/edición/baja
 * con JWT. Las categorías deben existir antes de crear productos.
 *
 * Documentación en: http://localhost:8080/swagger-ui.html
 */
@RestController
@RequestMapping("/api/v1/categories")
@Tag(
    name = "Categories",
    description = "API de gestión de categorías del catálogo. " +
                  "Las categorías deben existir antes de crear productos."
)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ── POST /api/v1/categories ───────────────────────────────────

    @PostMapping
    @Operation(
        summary     = "Crear una nueva categoría",
        description = "Registra una categoría en el catálogo. " +
                      "El nombre de la categoría es requerido.",
        security    = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201",
                     description  = "Categoría creada exitosamente",
                     content      = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
        @ApiResponse(responseCode = "401",
                     description  = "No autenticado",
                     content      = @Content),
        @ApiResponse(responseCode = "409",
                     description  = "Nombre de categoría duplicado",
                     content      = @Content),
        @ApiResponse(responseCode = "400",
                     description  = "Datos de entrada inválidos",
                     content      = @Content)
    })
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO request) {

        CategoryResponseDTO response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/v1/categories ────────────────────────────────────

    @GetMapping
    @Operation(
        summary     = "Listar todas las categorías",
        description = "Retorna todas las categorías registradas en el catálogo."
    )
    @ApiResponse(responseCode = "200", description = "Lista de categorías")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // ── GET /api/v1/categories/{id} ───────────────────────────────

    @GetMapping("/{id}")
    @Operation(
        summary     = "Obtener categoría por ID",
        description = "Retorna la información de una categoría específica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                     content      = @Content)
    })
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // ── PUT /api/v1/categories/{id}  (🔒 requiere JWT) ───────────

    @PutMapping("/{id}")
    @Operation(
        summary  = "Actualizar una categoría",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                     description  = "Categoría actualizada",
                     content      = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    // ── DELETE /api/v1/categories/{id}  (🔒 requiere JWT) ─────────

    @DeleteMapping("/{id}")
    @Operation(
        summary  = "Eliminar una categoría sin productos asociados",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
        @ApiResponse(responseCode = "409",
                     description  = "La categoría tiene productos y no puede eliminarse",
                     content      = @Content)
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
