// Generated from backend DTOs (catalog-demo) — copy to src/types/api.ts
// Last updated: 2026-05-30
//
// Correspondencia de tipos:
//   Java String (ObjectId MongoDB) → string
//   Java BigDecimal                → number
//   Java LocalDateTime / Instant  → string (ISO 8601, e.g. "2026-05-30T12:00:00.000Z")
//   Java boolean                  → boolean
//   Java int / long               → number

// ── Autenticación ──────────────────────────────────────────────────

/** Body de POST /api/v1/auth/login */
export interface AuthRequest {
  username: string;
  password: string;
}

/** Response de POST /api/v1/auth/login */
export interface AuthResponse {
  token: string;
  type: string;       // siempre "Bearer"
  message: string;
}

// ── Categorías ─────────────────────────────────────────────────────

/** Body de POST/PUT /api/v1/categories */
export interface CategoryRequest {
  name: string;
  description?: string | null;
}

/** Response de GET /api/v1/categories y embebido en ProductResponse */
export interface CategoryResponse {
  id: string;         // ObjectId de MongoDB serializado como string
  name: string;
  description?: string | null;
}

// ── Productos ──────────────────────────────────────────────────────

/** Body de POST /api/v1/products */
export interface ProductRequest {
  sku: string;
  name: string;
  price: number;       // BigDecimal → number. Min: 0.01. Max: 10 enteros, 2 decimales.
  categoryId: string;  // ObjectId de la categoría existente
}

/**
 * Body de PUT /api/v1/products/{id}.
 * El campo `sku` NO está incluido (el SKU es inmutable en el backend).
 */
export interface ProductUpdate {
  name: string;
  price: number;
  categoryId: string;
}

/** Response de GET/POST/PUT /api/v1/products/{id} */
export interface ProductResponse {
  id: string;                   // ObjectId de MongoDB
  sku: string;
  name: string;
  price: number;
  dateCreated: string;          // ISO 8601 UTC, e.g. "2026-05-30T12:00:00.000Z"
  category: CategoryResponse;   // categoría embebida (no es necesaria segunda llamada)
}

// ── Paginación ─────────────────────────────────────────────────────

/**
 * Respuesta paginada genérica.
 * Usada por GET /api/v1/products (ProductResponse[]).
 */
export interface PagedResponse<T> {
  content: T[];
  page: number;           // página actual (0-indexed)
  size: number;           // elementos solicitados por página
  totalElements: number;  // total de registros en la BD
  totalPages: number;     // Math.ceil(totalElements / size)
  first: boolean;         // true si es la primera página
  last: boolean;          // true si es la última página
}

// ── Errores ────────────────────────────────────────────────────────

/**
 * Estructura uniforme de error devuelta por el backend para todos los 4xx/5xx.
 */
export interface ApiError {
  timestamp: string;   // ISO 8601
  status: number;      // código HTTP numérico
  error: string;       // texto del status (e.g. "Not Found", "Conflict")
  message: string;     // mensaje legible por el usuario
  path: string;        // ruta que causó el error
  fieldErrors?: FieldErrorDetail[];  // solo presente en errores 400
}

/** Detalle de un campo inválido (presente en errores de validación 400). */
export interface FieldErrorDetail {
  field: string;    // nombre del campo Java (e.g. "sku", "price")
  message: string;  // mensaje de validación
}

// ── Health ──────────────────────────────────────────────────────────

/** Response de GET /api/v1/health */
export interface HealthResponse {
  status: 'UP' | 'DOWN';
  service: string;
  timestamp: string;   // ISO 8601
}
