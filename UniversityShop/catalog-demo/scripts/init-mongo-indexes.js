/**
 * Script de inicialización de índices para MongoDB Atlas.
 * Ejecutar UNA SOLA VEZ contra el cluster de producción:
 *
 *   mongosh "mongodb+srv://<user>:<pass>@biblioteca-cluster.xxxxx.mongodb.net/catalog_demo" \
 *     --file scripts/init-mongo-indexes.js
 *
 * En desarrollo (auto-index-creation: true) estos índices
 * se crean automáticamente al arrancar la aplicación.
 */

use catalog_demo;

// ── Colección: categories ──────────────────────────────────────────
// Unicidad de nombre de categoría (regla de negocio: CategoryNameAlreadyExistsException)
db.categories.createIndex({ name: 1 }, { unique: true, name: "idx_category_name_unique" });

// ── Colección: products ────────────────────────────────────────────
// Unicidad de SKU (regla de negocio: SkuAlreadyExistsException)
db.products.createIndex({ sku: 1 }, { unique: true, name: "idx_product_sku_unique" });

// Búsquedas por categoría con paginación (ProductServiceImpl.searchProducts)
db.products.createIndex({ categoryId: 1 }, { name: "idx_product_categoryId" });

// Búsqueda full-text por nombre (mejora findByNameContainingIgnoreCase)
db.products.createIndex({ name: "text" }, { name: "idx_product_name_text" });

// Índice compuesto para filtro simultáneo nombre + categoría
db.products.createIndex({ categoryId: 1, name: 1 }, { name: "idx_product_category_name" });

print("Índices creados exitosamente en catalog_demo.");
