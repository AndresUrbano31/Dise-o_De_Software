package com.university.shop.infrastructure;

import com.university.shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * =========================================================
 *  Repositorio de Product — Spring Data MongoDB
 * =========================================================
 *
 * Spring Data MongoDB genera la implementación en tiempo de
 * ejecución a partir de los nombres de los métodos.
 *
 * categoryId es ahora un campo String simple (no una asociación JPA),
 * por eso la notación es countByCategoryId (sin guión bajo).
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    /** Verifica unicidad de SKU antes de insertar. */
    boolean existsBySku(String sku);

    /**
     * Búsqueda por nombre parcial, insensible a mayúsculas.
     * MongoDB: { name: { $regex: ?0, $options: 'i' } }
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /** Filtra por categoryId con paginación. */
    Page<Product> findByCategoryId(String categoryId, Pageable pageable);

    /** Filtra por nombre parcial Y categoryId simultáneamente. */
    Page<Product> findByNameContainingIgnoreCaseAndCategoryId(String name,
                                                               String categoryId,
                                                               Pageable pageable);

    /** Cuenta productos de una categoría (para proteger borrado). */
    long countByCategoryId(String categoryId);
}
