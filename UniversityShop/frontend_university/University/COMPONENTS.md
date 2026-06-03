# Componentes del Frontend — University Shop

Referencia para el diagrama de componentes. El frontend está construido con React + TypeScript y se organiza en cinco capas: rutas, layouts, páginas, componentes compartidos y componentes de UI base.

---

## 1. Rutas (`src/routes/`)

| Componente | Responsabilidad |
|---|---|
| `AppRoutes` | Define el árbol de rutas de la aplicación con React Router. |
| `ProtectedRoute` | Bloquea el acceso a rutas de administración si el usuario no está autenticado. |
| `PublicRoute` | Redirige al catálogo si el usuario ya inició sesión (evita que vuelva al login). |

---

## 2. Layouts (`src/components/layout/`)

| Componente | Responsabilidad |
|---|---|
| `PublicLayout` | Envuelve las páginas públicas (catálogo, detalle). Incluye `Header` y zona de contenido. |
| `AdminLayout` | Envuelve las páginas de administración. Incluye `AdminSidebar` y zona de contenido. |
| `Header` | Barra superior fija con logo (`Wordmark`) y menú de usuario (login / logout / ir al admin). |
| `AdminSidebar` | Barra lateral del panel admin con navegación a Productos y Categorías. |

---

## 3. Páginas (`src/pages/`)

| Componente | Ruta | Responsabilidad |
|---|---|---|
| `CatalogPage` | `/catalog` | Muestra el catálogo de productos con búsqueda por nombre, filtro por categoría y paginación. |
| `ProductDetailPage` | `/catalog/:id` | Muestra el detalle de un producto individual. |
| `LoginPage` | `/login` | Formulario de autenticación del administrador. |
| `AdminProductsPage` | `/admin/products` | CRUD completo de productos (listar, crear, editar, eliminar). |
| `AdminCategoriesPage` | `/admin/categories` | CRUD completo de categorías. |
| `ConnectionDiagnosticsPage` | `/diagnostics` | Herramienta de diagnóstico de conexión con el backend. |
| `NotFoundPage` | `*` | Página 404 para rutas no encontradas. |

---

## 4. Componentes compartidos (`src/components/shared/`)

Componentes reutilizables que usan varias páginas.

| Componente | Responsabilidad |
|---|---|
| `ProductCard` | Tarjeta de producto para el catálogo: imagen, categoría, nombre, SKU y precio. |
| `DataTable` | Tabla genérica con columnas configurables, paginación integrada, estado de carga y estado vacío. |
| `CategoryPills` | Fila de píldoras de filtro por categoría (barra de filtros del catálogo). |
| `Pagination` | Controles de paginación (anterior / siguiente / número de página). |
| `ConfirmDialog` | Modal de confirmación reutilizable para acciones destructivas (ej. eliminar). |
| `FormField` | Envuelve un campo de formulario con su `<label>`, hint opcional y manejo de error. |
| `BackendStatus` | Banner rojo fijo en la parte superior cuando el backend no responde. |
| `EmptyState` | Estado vacío genérico con ícono, título, descripción y acción opcional. |
| `LoadingSpinner` | Spinner de carga en distintos tamaños. |
| `Skeleton` | Placeholder de carga con efecto shimmer para listas y tarjetas. |
| `Breadcrumb` | Migas de pan para navegación jerárquica. |
| `Wordmark` | Logo/marca de la tienda. Soporta modo icono y modo completo. |
| `ProductImagePlaceholder` | Placeholder visual para productos sin imagen. |

---

## 5. Componentes de UI base (`src/components/ui/`)

Componentes primitivos de diseño basados en **shadcn/ui** + Radix UI. No contienen lógica de negocio.

| Componente | Descripción |
|---|---|
| `Button` | Botón con variantes (default, outline, ghost, destructive) y tamaños. |
| `Input` | Campo de texto estilizado. |
| `Textarea` | Área de texto estilizada. |
| `Label` | Etiqueta accesible para campos de formulario. |
| `Select` | Selector desplegable accesible (Radix). |
| `Form` | Componentes de formulario integrados con react-hook-form. |
| `Dialog` | Modal/diálogo accesible (Radix). |
| `DropdownMenu` | Menú desplegable accesible (Radix). |
| `Sheet` | Panel lateral deslizante (Radix). |
| `Badge` | Etiqueta de estado o categoría con variantes de color. |
| `Card` | Contenedor con borde y sombra. |
| `Table` | Tabla HTML estilizada. |
| `Separator` | Línea divisora horizontal o vertical. |
| `Skeleton` | Versión UI base del placeholder shimmer. |
| `Tooltip` | Tooltip accesible (Radix). |

---

## 6. Contexto y Hooks (`src/context/`, `src/hooks/`)

| Archivo | Tipo | Responsabilidad |
|---|---|---|
| `AuthContext` | Context | Provee el estado de autenticación (token, usuario) a toda la app. |
| `useAuth` | Hook | Accede al `AuthContext`; expone `isAuthenticated`, `login`, `logout`. |
| `useProducts` | Hook | Hooks de React Query para listar, crear, editar y eliminar productos. |
| `useCategories` | Hook | Hooks de React Query para listar y gestionar categorías. |

---

## 7. Servicios y utilidades (`src/services/`, `src/lib/`)

| Archivo | Responsabilidad |
|---|---|
| `services/api.ts` | Cliente HTTP (axios/fetch) con interceptores; centraliza las llamadas al backend. |
| `lib/format.ts` | Funciones de formato: `formatPrice`, `formatDate`. |
| `lib/utils.ts` | Utilidad `cn()` para combinar clases Tailwind. |
| `lib/design-tokens.ts` | Tokens de diseño (colores, espaciados) compartidos entre componentes. |
| `types/api.ts` | Tipos TypeScript que modelan las respuestas del backend (`ProductResponse`, etc.). |
