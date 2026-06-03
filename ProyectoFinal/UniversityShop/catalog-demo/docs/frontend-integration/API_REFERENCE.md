# API Reference — Catalog Demo

**Base URL:** `http://localhost:8080/api/v1`  
**Content-Type:** `application/json`  
**Autenticación:** Bearer JWT en header `Authorization: Bearer <token>`

---

## Autenticación

### POST /auth/login
- **Auth:** No
- **Body:** `AuthRequest`
- **Response 200:** `AuthResponse` (incluye el JWT)
- **Errores:**
  - `401` — credenciales incorrectas

**Body de ejemplo:**
```json
{ "username": "admin", "password": "admin123" }
```

**Response de ejemplo:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "message": "Autenticación exitosa. Incluye este token en el header Authorization."
}
```

---

## Health

### GET /health
- **Auth:** No (público)
- **Response 200:** `HealthResponse`

**Response de ejemplo:**
```json
{
  "status": "UP",
  "service": "catalog-demo",
  "timestamp": "2026-05-30T12:00:00Z"
}
```

---

## Categorías

### GET /categories
- **Auth:** No (lectura pública)
- **Query params:** ninguno
- **Response 200:** `CategoryResponse[]`

**Response de ejemplo:**
```json
[
  { "id": "683a1c...", "name": "Papelería", "description": "Artículos de escritura" },
  { "id": "683a1d...", "name": "Tecnología", "description": null }
]
```

### GET /categories/{id}
- **Auth:** No
- **Path params:** `id` (String — ObjectId de MongoDB)
- **Response 200:** `CategoryResponse`
- **Errores:**
  - `404` — categoría no encontrada

### POST /categories
- **Auth:** Sí (Bearer JWT)
- **Body:** `CategoryRequest`
- **Response 201:** `CategoryResponse`
- **Errores:**
  - `400` — datos inválidos (nombre vacío, excede longitud)
  - `401` — token inválido o ausente
  - `409` — ya existe una categoría con ese nombre

**Body de ejemplo:**
```json
{ "name": "Libros de Texto", "description": "Textos universitarios" }
```

### PUT /categories/{id}
- **Auth:** Sí (Bearer JWT)
- **Path params:** `id` (String)
- **Body:** `CategoryRequest`
- **Response 200:** `CategoryResponse`
- **Errores:**
  - `400` — datos inválidos
  - `401` — no autenticado
  - `404` — categoría no encontrada
  - `409` — nombre ya en uso por otra categoría

### DELETE /categories/{id}
- **Auth:** Sí (Bearer JWT)
- **Path params:** `id` (String)
- **Response 204:** (sin cuerpo)
- **Errores:**
  - `401` — no autenticado
  - `404` — categoría no encontrada
  - `409` — la categoría tiene productos asociados y no puede eliminarse

---

## Productos

### GET /products
- **Auth:** No (lectura pública)
- **Query params:**
  - `name` (String, opcional) — filtro por nombre parcial (case-insensitive)
  - `categoryId` (String, opcional) — filtro por ID de categoría
  - `page` (int, default `0`) — número de página (0-indexed)
  - `size` (int, default `10`) — elementos por página
- **Response 200:** `PagedResponse<ProductResponse>`

**Request de ejemplo:**
```
GET /api/v1/products?name=laptop&categoryId=683a1d...&page=0&size=20
```

**Response de ejemplo:**
```json
{
  "content": [
    {
      "id": "683b2e...",
      "sku": "TEC-001",
      "name": "Laptop Dell XPS",
      "price": 999.99,
      "dateCreated": "2026-05-30T12:00:00.000Z",
      "category": {
        "id": "683a1d...",
        "name": "Tecnología",
        "description": null
      }
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### GET /products/all
- **Auth:** No
- **Response 200:** `ProductResponse[]` (lista completa, sin paginación)
- **Uso recomendado:** dropdowns o selects en el frontend

### GET /products/{id}
- **Auth:** No
- **Path params:** `id` (String — ObjectId de MongoDB)
- **Response 200:** `ProductResponse`
- **Errores:**
  - `404` — producto no encontrado

### POST /products
- **Auth:** Sí (Bearer JWT)
- **Body:** `ProductRequest`
- **Response 201:** `ProductResponse`
- **Errores:**
  - `400` — datos inválidos (SKU vacío, precio ≤ 0, categoryId ausente)
  - `401` — no autenticado
  - `404` — la categoría indicada no existe
  - `409` — el SKU ya está registrado en el catálogo

**Body de ejemplo:**
```json
{
  "sku": "TEC-002",
  "name": "Mouse Logitech MX Master",
  "price": 89.99,
  "categoryId": "683a1d..."
}
```

### PUT /products/{id}
- **Auth:** Sí (Bearer JWT)
- **Path params:** `id` (String)
- **Body:** `ProductUpdate`
- **Response 200:** `ProductResponse`
- **Errores:**
  - `400` — datos inválidos
  - `401` — no autenticado
  - `404` — producto o categoría no encontrado

**Nota:** el campo `sku` NO puede modificarse (es el identificador comercial inmutable).

**Body de ejemplo:**
```json
{
  "name": "Mouse Logitech MX Master 3",
  "price": 94.99,
  "categoryId": "683a1d..."
}
```

### DELETE /products/{id}
- **Auth:** Sí (Bearer JWT)
- **Path params:** `id` (String)
- **Response 204:** (sin cuerpo)
- **Errores:**
  - `401` — no autenticado
  - `404` — producto no encontrado

---

## Formato de errores

Todos los errores siguen la misma estructura:

```json
{
  "timestamp": "2026-05-30T12:00:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "El producto con ID abc no fue encontrado.",
  "path": "/api/v1/products/abc"
}
```

En errores de validación (400), se agrega `fieldErrors`:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Validación de datos fallida",
  "path": "/api/v1/products",
  "fieldErrors": [
    { "field": "sku", "message": "El SKU no puede estar vacío" },
    { "field": "price", "message": "El precio debe ser mayor a 0.00" }
  ]
}
```

| Status | Significado                                      |
|--------|--------------------------------------------------|
| 200    | Operación exitosa                                |
| 201    | Recurso creado                                   |
| 204    | Eliminación exitosa (sin cuerpo)                 |
| 400    | Datos de entrada inválidos                       |
| 401    | Token ausente, inválido o expirado               |
| 403    | Sin permisos para la operación                   |
| 404    | Recurso no encontrado                            |
| 409    | Conflicto (duplicado, dependencias)              |
| 500    | Error interno del servidor                       |
