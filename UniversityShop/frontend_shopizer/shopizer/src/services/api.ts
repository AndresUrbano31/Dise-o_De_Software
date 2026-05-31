import axios, { type AxiosError, type AxiosInstance } from 'axios';
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
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  },
);

// ── Servicio de autenticación ──────────────────────────────────────

export const authService = {
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
  list: (): Promise<CategoryResponse[]> =>
    api.get<CategoryResponse[]>('/categories').then((r) => r.data),

  getById: (id: string): Promise<CategoryResponse> =>
    api.get<CategoryResponse>(`/categories/${id}`).then((r) => r.data),

  create: (data: CategoryRequest): Promise<CategoryResponse> =>
    api.post<CategoryResponse>('/categories', data).then((r) => r.data),

  update: (id: string, data: CategoryRequest): Promise<CategoryResponse> =>
    api.put<CategoryResponse>(`/categories/${id}`, data).then((r) => r.data),

  delete: (id: string): Promise<void> =>
    api.delete(`/categories/${id}`).then(() => undefined),
};

// ── Servicio de productos ──────────────────────────────────────────

export const productService = {
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

  listAll: (): Promise<ProductResponse[]> =>
    api.get<ProductResponse[]>('/products/all').then((r) => r.data),

  getById: (id: string): Promise<ProductResponse> =>
    api.get<ProductResponse>(`/products/${id}`).then((r) => r.data),

  create: (data: ProductRequest): Promise<ProductResponse> =>
    api.post<ProductResponse>('/products', data).then((r) => r.data),

  update: (id: string, data: ProductUpdate): Promise<ProductResponse> =>
    api.put<ProductResponse>(`/products/${id}`, data).then((r) => r.data),

  delete: (id: string): Promise<void> =>
    api.delete(`/products/${id}`).then(() => undefined),
};

// ── Servicio de health ─────────────────────────────────────────────

export const healthService = {
  check: (): Promise<HealthResponse> =>
    api.get<HealthResponse>('/health').then((r) => r.data),
};

// ── Utilidad: extraer mensaje de error ────────────────────────────

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const apiError = error.response?.data as ApiError | undefined;
    return apiError?.message ?? error.message;
  }
  if (error instanceof Error) return error.message;
  return 'Error desconocido';
}

export default api;
