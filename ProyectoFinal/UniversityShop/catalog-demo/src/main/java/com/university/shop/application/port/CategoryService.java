package com.university.shop.application.port;

import com.university.shop.application.dto.CategoryRequestDTO;
import com.university.shop.application.dto.CategoryResponseDTO;

import java.util.List;

/**
 * =========================================================
 *  CAPA DE APLICACIÓN — Puerto de entrada (Inbound Port)
 *  Vista Funcional (Functional View — Ports & Adapters)
 * =========================================================
 *
 * CategoryService define el contrato de los casos de uso
 * relacionados con la gestión de categorías del catálogo.
 *
 * Al igual que ProductService, el Controller depende de
 * ESTA INTERFAZ, no de la implementación concreta.
 *
 * Implementación concreta: CategoryServiceImpl (en infrastructure/service)
 */
public interface CategoryService {

    /**
     * Caso de uso: registrar una nueva categoría.
     *
     * @param  request  datos de la categoría a crear
     * @return          representación de la categoría persistida
     */
    CategoryResponseDTO createCategory(CategoryRequestDTO request);

    /**
     * Caso de uso: consultar todas las categorías.
     *
     * @return lista de categorías (vacía si no hay ninguna)
     */
    List<CategoryResponseDTO> getAllCategories();

    /**
     * Caso de uso: obtener una categoría por su ID.
     *
     * @param  id  identificador de la categoría
     * @return     representación de la categoría encontrada
     * @throws com.university.shop.domain.exception.CategoryNotFoundException si no existe
     */
    CategoryResponseDTO getCategoryById(String id);

    /**
     * Actualiza nombre y descripción de una categoría existente.
     *
     * @throws com.university.shop.domain.exception.CategoryNotFoundException si no existe
     * @throws com.university.shop.domain.exception.CategoryNameAlreadyExistsException si el nombre está en uso
     */
    CategoryResponseDTO updateCategory(String id, CategoryRequestDTO request);

    /**
     * Elimina una categoría sin productos asociados.
     *
     * @throws com.university.shop.domain.exception.CategoryNotFoundException si no existe
     * @throws com.university.shop.domain.exception.CategoryHasProductsException si tiene productos
     */
    void deleteCategory(String id);
}
