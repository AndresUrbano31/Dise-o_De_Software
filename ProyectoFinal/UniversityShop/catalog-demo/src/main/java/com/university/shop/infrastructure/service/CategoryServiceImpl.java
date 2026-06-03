package com.university.shop.infrastructure.service;

import com.university.shop.application.dto.CategoryRequestDTO;
import com.university.shop.application.dto.CategoryResponseDTO;
import com.university.shop.application.port.CategoryService;
import com.university.shop.domain.Category;
import com.university.shop.domain.exception.CategoryHasProductsException;
import com.university.shop.domain.exception.CategoryNameAlreadyExistsException;
import com.university.shop.domain.exception.CategoryNotFoundException;
import com.university.shop.infrastructure.CategoryRepository;
import com.university.shop.infrastructure.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * =========================================================
 *  CAPA DE INFRAESTRUCTURA — Implementación de CategoryService
 *  Vista Funcional (Functional View — Ports & Adapters)
 * =========================================================
 *
 * CategoryServiceImpl implementa el puerto CategoryService.
 * Gestiona el ciclo de vida de las categorías del catálogo.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // ── Caso de uso 1: Crear Categoría ────────────────────────────

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new CategoryNameAlreadyExistsException(request.getName());
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        return toResponseDTO(saved);
    }

    // ── Caso de uso 2: Listar Categorías ──────────────────────────

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Caso de uso 3: Obtener Categoría por ID ───────────────────

    @Override
    public CategoryResponseDTO getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return toResponseDTO(category);
    }

    // ── Caso de uso 4: Actualizar categoría ───────────────────────

    @Override
    public CategoryResponseDTO updateCategory(String id, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getName().equals(request.getName())
                && categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new CategoryNameAlreadyExistsException(request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return toResponseDTO(categoryRepository.save(category));
    }

    // ── Caso de uso 5: Eliminar categoría ─────────────────────────

    @Override
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        if (productRepository.countByCategoryId(id) > 0) {
            throw new CategoryHasProductsException(id);
        }
        categoryRepository.deleteById(id);
    }

    // ── Mapeo privado ─────────────────────────────────────────────

    private CategoryResponseDTO toResponseDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
