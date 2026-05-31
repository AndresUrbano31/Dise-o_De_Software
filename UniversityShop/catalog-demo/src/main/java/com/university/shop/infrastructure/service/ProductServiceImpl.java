package com.university.shop.infrastructure.service;

import com.university.shop.application.dto.CategoryResponseDTO;
import com.university.shop.application.dto.PagedResponseDTO;
import com.university.shop.application.dto.ProductRequestDTO;
import com.university.shop.application.dto.ProductResponseDTO;
import com.university.shop.application.dto.ProductUpdateDTO;
import com.university.shop.application.port.ProductService;
import com.university.shop.domain.Category;
import com.university.shop.domain.Product;
import com.university.shop.domain.exception.CategoryNotFoundException;
import com.university.shop.domain.exception.ProductNotFoundException;
import com.university.shop.domain.exception.SkuAlreadyExistsException;
import com.university.shop.infrastructure.CategoryRepository;
import com.university.shop.infrastructure.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * =========================================================
 *  Implementación completa de los 6 casos de uso de Producto
 * =========================================================
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                               CategoryRepository categoryRepository) {
        this.productRepository  = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // ── 1. Crear ──────────────────────────────────────────────────

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new SkuAlreadyExistsException(request.getSku());
        }
        // Verifica que la categoría existe antes de asignarla
        validateCategoryExists(request.getCategoryId());

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategoryId(request.getCategoryId());

        return toResponseDTO(productRepository.save(product));
    }

    // ── 2. Listar todos (sin paginación) ─────────────────────────

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── 3. Buscar con filtros + paginación ────────────────────────

    @Override
    public PagedResponseDTO<ProductResponseDTO> searchProducts(
            String name, String categoryId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Product> result;
        boolean hasName     = name != null && !name.isBlank();
        boolean hasCategory = categoryId != null && !categoryId.isBlank();

        if (hasName && hasCategory) {
            result = productRepository
                    .findByNameContainingIgnoreCaseAndCategoryId(name, categoryId, pageable);
        } else if (hasName) {
            result = productRepository
                    .findByNameContainingIgnoreCase(name, pageable);
        } else if (hasCategory) {
            result = productRepository
                    .findByCategoryId(categoryId, pageable);
        } else {
            result = productRepository.findAll(pageable);
        }

        List<ProductResponseDTO> content = result.getContent()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return new PagedResponseDTO<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    // ── 4. Obtener por ID ─────────────────────────────────────────

    @Override
    public ProductResponseDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponseDTO(product);
    }

    // ── 5. Actualizar ─────────────────────────────────────────────

    @Override
    public ProductResponseDTO updateProduct(String id, ProductUpdateDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Verifica que la nueva categoría existe
        validateCategoryExists(request.getCategoryId());

        // Aplicar cambios (el SKU NO se modifica)
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategoryId(request.getCategoryId());

        return toResponseDTO(productRepository.save(product));
    }

    // ── 6. Eliminar ───────────────────────────────────────────────

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    // ── Utilitarios privados ──────────────────────────────────────

    private void validateCategoryExists(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        // En MongoDB la categoría se referencia por ID; se busca para embeber en la respuesta
        Category category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(product.getCategoryId()));

        CategoryResponseDTO categoryDTO = new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
        return new ProductResponseDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getPrice(),
                product.getDateCreated(),
                categoryDTO
        );
    }
}
