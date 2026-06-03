# Product Catalog API — catalog-demo

API REST para gestión de catálogo de productos universitarios.  
**Stack:** Spring Boot 3.2.5 · Java 17 · Spring Data MongoDB · JWT · OpenAPI 3

---

## Requisitos

- Java 17
- Maven 3.6+ (o usar `./mvnw` incluido)
- Variable de entorno `MONGODB_URI` (ver sección siguiente)
- Variable de entorno `JWT_SECRET` (clave ≥ 32 bytes UTF-8)

---

## Configurar la conexión a MongoDB Atlas

### 1. Obtener la URI desde Atlas

1. Entra a [cloud.mongodb.com](https://cloud.mongodb.com)
2. Selecciona el cluster `biblioteca-cluster`
3. Botón **Connect** → **Drivers** → copia la URI `mongodb+srv://...`
4. Reemplaza `<password>` con la contraseña del usuario de BD

### 2. Exportar la variable de entorno

```bash
# Para la sesión actual
export MONGODB_URI="mongodb+srv://<user>:<pass>@biblioteca-cluster.xxxxx.mongodb.net/catalog_demo?retryWrites=true&w=majority"
export JWT_SECRET="catalog-demo-university-secret-key-256-bits-2024"

# Para que persista entre sesiones (agrega al final de ~/.zshrc)
echo 'export MONGODB_URI="mongodb+srv://..."' >> ~/.zshrc
echo 'export JWT_SECRET="..."' >> ~/.zshrc
source ~/.zshrc
```

### 3. Configurar acceso en Atlas

- **Database Access** → Add New Database User → username/password
- **Network Access** → Add IP Address:
  - Desarrollo: `0.0.0.0/0` (cualquier IP)
  - Producción: IP fija del servidor

---

## Ejecutar el proyecto

### Desarrollo local (MongoDB local o Atlas via MONGODB_URI)

```bash
# Arrancar con perfil por defecto (usa MONGODB_URI o mongodb://localhost:27017)
./mvnw spring-boot:run

# Con MONGODB_URI de Atlas explícito
MONGODB_URI="mongodb+srv://..." ./mvnw spring-boot:run
```

### Producción con Docker

```bash
# Exporta las variables primero, luego:
docker-compose up --build
```

### Perfil de producción sin Docker

```bash
SPRING_PROFILES_ACTIVE=prod \
MONGODB_URI="mongodb+srv://..." \
JWT_SECRET="tu-clave-segura" \
./mvnw spring-boot:run
```

---

## Índices de MongoDB (primera vez en Atlas)

En producción `auto-index-creation` está deshabilitado. Ejecuta el script **una sola vez**:

```bash
mongosh "$MONGODB_URI" --file scripts/init-mongo-indexes.js
```

Esto crea:
- `categories.name` — índice único (regla de negocio: nombre de categoría único)
- `products.sku` — índice único (regla de negocio: SKU único)
- `products.categoryId` — índice simple (filtros por categoría)
- `products.name` — índice de texto (búsqueda full-text)
- `products.{categoryId, name}` — índice compuesto (búsqueda combinada)

---

## Endpoints principales

| Método | Ruta                           | Auth  | Descripción                          |
|--------|-------------------------------|-------|--------------------------------------|
| POST   | `/api/v1/auth/login`           | No    | Obtener token JWT                    |
| GET    | `/api/v1/products`             | No    | Listar/buscar productos (paginado)   |
| GET    | `/api/v1/products/all`         | No    | Todos los productos (sin paginar)    |
| GET    | `/api/v1/products/{id}`        | No    | Detalle de un producto               |
| POST   | `/api/v1/products`             | Admin | Crear producto                       |
| PUT    | `/api/v1/products/{id}`        | Admin | Actualizar producto                  |
| DELETE | `/api/v1/products/{id}`        | Admin | Eliminar producto                    |
| GET    | `/api/v1/categories`           | No    | Listar categorías                    |
| POST   | `/api/v1/categories`           | Admin | Crear categoría                      |
| PUT    | `/api/v1/categories/{id}`      | Admin | Actualizar categoría                 |
| DELETE | `/api/v1/categories/{id}`      | Admin | Eliminar categoría (sin productos)   |

**Credenciales de demo:** `admin` / `admin123`  
**Documentación interactiva:** `http://localhost:8080/swagger-ui.html`

> **Nota sobre IDs:** con MongoDB los IDs son `String` (ObjectId serializado, ej. `"683a1c..."`), no números enteros. Esto afecta los `@PathVariable` y `@RequestParam categoryId`.

---

## Ejecutar tests

```bash
./mvnw test
```

Los tests usan Mockito (sin base de datos real).  
`CategoryServiceImplTest` valida las reglas de negocio principales.

---

## Por qué este cambio demuestra la Arquitectura Hexagonal

La migración de **Spring Data JPA + PostgreSQL** a **Spring Data MongoDB** afectó **exclusivamente** la capa de infraestructura:

| Capa          | ¿Cambió? | Qué cambió                                                   |
|---------------|:--------:|--------------------------------------------------------------|
| `api/`        | Mínimo   | Solo `Long` → `String` en `@PathVariable` (tipo de ID)       |
| `application/`| Mínimo   | Solo `Long` → `String` en firmas de interfaces y DTOs         |
| `domain/`     | Sí       | Anotaciones JPA → MongoDB; `Category category` → `String categoryId` |
| `infrastructure/`| Sí    | `JpaRepository` → `MongoRepository`; `@Transactional` removido; implementaciones adaptadas |

Las **reglas de negocio** (`SkuAlreadyExistsException`, `CategoryHasProductsException`, etc.) **no cambiaron**. Los **contratos de la API REST** (endpoints, DTOs, validaciones) **no cambiaron** (excepto el tipo de ID). Esto es exactamente lo que promete la arquitectura hexagonal: cambiar la tecnología de persistencia sin tocar la lógica de negocio ni la interfaz pública.
