/**
 * example-axios-service.ts
 * Servicio Axios listo para usar — copia a src/services/api.ts en el frontend.
 *
 * Instala axios primero:
 *   npm install axios
 *
 * Crea .env.local en la raíz del frontend con:
 *   VITE_API_BASE_URL=http://localhost:8080/api/v1
 */

import axios, { AxiosError, AxiosInstance } from 'axios';
import type {
  AuthRequest,
  AuthResponse,
  CategoryRequest,
  CategoryResponse,
  ProductRequest,
  ProductUpdate,
  ProductResponse,
  PagedResponse,
  ApiError,
  HealthResponse,
} from '../types/api';

// ── Instancia base ─────────────────────────────────────────────────

const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1',
  timeout: 10_000,
  headers: { 'Content-Type': 'application/json' },
});

// ── Interceptor de request: adjunta JWT si existe ─────────────────

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ── Interceptor de response: maneja 401 globalmente ───────────────

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('jwt');
      // Redirige al login; ajusta la ruta según tu router
      window.location.href = '/login';
    }
    // Re-lanza el error para que el caller pueda manejarlo localmente
    return Promise.reject(error);
  },
);

// ── Servicio de autenticación ──────────────────────────────────────

export const authService = {
  /**
   * Autentica al usuario y devuelve el token JWT.
   * Guarda el token en localStorage automáticamente.
   *
   * @example
   *   const { token } = await authService.login({ username: 'admin', password: 'admin123' });
   *   localStorage.setItem('jwt', token);
   */
  login: async (data: AuthRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    localStorage.setItem('jwt', response.data.token);
    return response.data;
  },

  logout: (): void => {
    localStorage.removeItem('jwt');
  },

  isAuthenticated: (): boolean => !!localStorage.getItem('jwt'),
};

// ── Servicio de categorías ─────────────────────────────────────────

export const categoryService = {
  /** Lista todas las categorías (público). */
  list: (): Promise<CategoryResponse[]> =>
    api.get<CategoryResponse[]>('/categories').then((r) => r.data),

  /** Obtiene una categoría por ID (público). */
  getById: (id: string): Promise<CategoryResponse> =>
    api.get<CategoryResponse>(`/categories/${id}`).then((r) => r.data),

  /** Crea una categoría (requiere JWT). */
  create: (data: CategoryRequest): Promise<CategoryResponse> =>
    api.post<CategoryResponse>('/categories', data).then((r) => r.data),

  /** Actualiza nombre/descripción de una categoría (requiere JWT). */
  update: (id: string, data: CategoryRequest): Promise<CategoryResponse> =>
    api.put<CategoryResponse>(`/categories/${id}`, data).then((r) => r.data),

  /**
   * Elimina una categoría (requiere JWT).
   * Lanza 409 si la categoría tiene productos asociados.
   */
  delete: (id: string): Promise<void> =>
    api.delete(`/categories/${id}`).then(() => undefined),
};

// ── Servicio de productos ──────────────────────────────────────────

export const productService = {
  /**
   * Lista productos con paginación y filtros opcionales (público).
   *
   * @example
   *   // Primera página de 10
   *   const result = await productService.list();
   *
   *   // Buscar "laptop" en la categoría TEC
   *   const result = await productService.list({ name: 'laptop', categoryId: '683a1d...' });
   */
  list: (params?: {
    name?: string;
    categoryId?: string;
    page?: number;
    size?: number;
  }): Promise<PagedResponse<ProductResponse>> =>
    api
      .get<PagedResponse<ProductResponse>>('/products', {
        params: { page: 0, size: 10, ...params },
      })
      .then((r) => r.data),

  /** Lista todos los productos sin paginación (útil para selects/dropdowns). */
  listAll: (): Promise<ProductResponse[]> =>
    api.get<ProductResponse[]>('/products/all').then((r) => r.data),

  /** Obtiene un producto por ID (público). */
  getById: (id: string): Promise<ProductResponse> =>
    api.get<ProductResponse>(`/products/${id}`).then((r) => r.data),

  /** Crea un producto (requiere JWT). */
  create: (data: ProductRequest): Promise<ProductResponse> =>
    api.post<ProductResponse>('/products', data).then((r) => r.data),

  /**
   * Actualiza nombre, precio y/o categoría de un producto (requiere JWT).
   * El SKU NO es modificable.
   */
  update: (id: string, data: ProductUpdate): Promise<ProductResponse> =>
    api.put<ProductResponse>(`/products/${id}`, data).then((r) => r.data),

  /** Elimina un producto (requiere JWT). */
  delete: (id: string): Promise<void> =>
    api.delete(`/products/${id}`).then(() => undefined),
};

// ── Servicio de health ─────────────────────────────────────────────

export const healthService = {
  /** Verifica que el backend está disponible. */
  check: (): Promise<HealthResponse> =>
    api.get<HealthResponse>('/health').then((r) => r.data),
};

// ── Utilidad: extraer mensaje de error ────────────────────────────

/**
 * Extrae el mensaje legible de un error de Axios.
 * Útil para mostrar toasts o mensajes en el UI.
 *
 * @example
 *   try {
 *     await productService.create(data);
 *   } catch (err) {
 *     toast.error(getErrorMessage(err));
 *   }
 */
export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const apiError = error.response?.data as ApiError | undefined;
    return apiError?.message ?? error.message;
  }
  if (error instanceof Error) return error.message;
  return 'Error desconocido';
}

export default api;
