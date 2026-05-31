package com.university.shop.infrastructure;

import com.university.shop.domain.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * =========================================================
 *  CAPA DE INFRAESTRUCTURA — Repository de Category (MongoDB)
 * =========================================================
 *
 * Spring Data MongoDB genera la implementación en tiempo de
 * ejecución. Los métodos derivados se traducen a queries MongoDB.
 *
 * Métodos heredados de MongoRepository:
 *   save(), findById(), findAll(), deleteById(), existsById()…
 */
@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    /**
     * Verifica si ya existe una categoría con ese nombre.
     * Query MongoDB: { name: ?0 }
     */
    boolean existsByName(String name);

    /**
     * true si otra categoría (distinto id) ya usa este nombre.
     */
    boolean existsByNameAndIdNot(String name, String id);
}
