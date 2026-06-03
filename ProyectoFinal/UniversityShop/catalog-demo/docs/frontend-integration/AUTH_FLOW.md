# Flujo de Autenticación — catalog-demo

## Diagrama

```
Frontend                          Backend
   │                                 │
   │  POST /api/v1/auth/login        │
   │  { username, password }  ──────►│
   │                                 │  Valida credenciales
   │                                 │  Genera JWT (24h HS256)
   │◄────── 200 { token, type } ─────│
   │                                 │
   │  localStorage.setItem('jwt', token)
   │                                 │
   │  GET /api/v1/products           │
   │  Authorization: Bearer <token> ►│
   │                                 │  JwtAuthenticationFilter valida token
   │◄────── 200 [ productos... ] ────│
   │                                 │
   │  (token expira a las 24h)       │
   │                                 │
   │  GET /api/v1/products           │
   │  Authorization: Bearer <expired>►│
   │◄────── 401 Unauthorized ────────│
   │                                 │
   │  Interceptor detecta 401        │
   │  localStorage.removeItem('jwt') │
   │  window.location.href = '/login'│
```

## Pasos detallados

### 1. Login

```typescript
import { authService } from './services/api';

const handleLogin = async (username: string, password: string) => {
  try {
    const response = await authService.login({ username, password });
    // authService.login ya guarda el token en localStorage
    console.log('Bienvenido, token guardado:', response.token);
    navigate('/dashboard');
  } catch (error) {
    // error.response.status === 401 → credenciales incorrectas
    console.error('Login fallido');
  }
};
```

### 2. Peticiones autenticadas (automáticas)

El interceptor de request en `example-axios-service.ts` inyecta el token en cada petición:

```typescript
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```

No necesitas hacer nada extra: usa los servicios normalmente y el token se adjunta solo.

### 3. Logout

```typescript
import { authService } from './services/api';

const handleLogout = () => {
  authService.logout();   // elimina el token de localStorage
  navigate('/login');
};
```

### 4. Manejo de token expirado

Si el token expira (24 horas por defecto), el backend devuelve `401`. El interceptor de response lo detecta automáticamente:

```typescript
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('jwt');
      window.location.href = '/login';   // redirige al login
    }
    return Promise.reject(error);
  },
);
```

## Endpoints públicos vs. protegidos

| Grupo           | Endpoints                              | Auth requerida |
|-----------------|----------------------------------------|:--------------:|
| Autenticación   | `POST /auth/login`                     | No             |
| Health          | `GET /health`                          | No             |
| Catálogo (lect) | `GET /products/**`, `GET /categories/**`| No            |
| Documentación   | `/swagger-ui/**`, `/v3/api-docs/**`    | No             |
| Catálogo (escr) | `POST/PUT/DELETE /products/**`         | Sí             |
| Catálogo (escr) | `POST/PUT/DELETE /categories/**`       | Sí             |

## Verificar autenticación en el frontend

```typescript
import { authService } from './services/api';

// En un componente protegido
useEffect(() => {
  if (!authService.isAuthenticated()) {
    navigate('/login');
  }
}, []);
```

## Credenciales de demo

| Campo    | Valor      |
|----------|-----------|
| username | `admin`   |
| password | `admin123`|

> En producción, estas credenciales deben cambiarse y los usuarios deben persistirse en base de datos.
