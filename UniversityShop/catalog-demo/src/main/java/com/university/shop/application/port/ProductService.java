package com.university.shop.application.port;

import com.university.shop.application.dto.PagedResponseDTO;
import com.university.shop.application.dto.ProductRequestDTO;
import com.university.shop.application.dto.ProductResponseDTO;
import com.university.shop.application.dto.ProductUpdateDTO;

import java.util.List;

/**
 * =========================================================
 *  Puerto de entrada — Contrato completo de ProductService
 * =========================================================
 *
 * Define los 6 casos de uso del módulo de Catálogo:
 *   1. Crear producto
 *   2. Listar todos (sin filtros)
 *   3. Buscar con filtros y paginación  ← NUEVO
 *   4. Obtener uno por ID               ← NUEVO
 *   5. Actualizar (editar)              ← NUEVO
 *   6. Eliminar                         ← NUEVO
 */
public interface ProductService {

    /** Caso de uso 1: registrar un nuevo producto */
    ProductResponseDTO createProduct(ProductRequestDTO request);

    /** Caso de uso 2: listar todos (sin paginación, para selects simples) */
    List<ProductResponseDTO> getAllProducts();

    /**
     * Caso de uso 3: buscar productos con filtros opcionales y paginación.
     *
     * @param name       filtro por nombre (parcial, insensible a mayúsculas)
     * @param categoryId filtro por categoría (null = todas las categorías)
     * @param page       número de página (0-indexed)
     * @param size       cantidad de elementos por página
     */
    PagedResponseDTO<ProductResponseDTO> searchProducts(String name, String categoryId,
                                                         int page, int size);

    /** Caso de uso 4: obtener un producto por su ID */
    ProductResponseDTO getProductById(String id);

    /** Caso de uso 5: actualizar nombre, precio y/o categoría de un producto */
    ProductResponseDTO updateProduct(String id, ProductUpdateDTO request);

    /** Caso de uso 6: eliminar un producto del catálogo */
    void deleteProduct(String id);
}
