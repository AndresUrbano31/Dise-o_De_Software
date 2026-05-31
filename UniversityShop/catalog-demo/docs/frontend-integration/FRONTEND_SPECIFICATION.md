# Especificación del Frontend — Catalog Demo

**Versión:** 1.0.0  
**Fecha:** 2026-05-30  
**Backend de referencia:** `catalog-demo` (Spring Boot 3.2.5 · MongoDB Atlas)  
**Audiencia:** Desarrolladores frontend que construirán la interfaz de usuario

> **Nota crítica antes de comenzar:** el backend actual NO expone un endpoint de registro de usuarios (`/auth/register`). Solo existe `POST /auth/login`. El único usuario disponible es `admin / admin123`. Cualquier pantalla de "Registro" queda fuera del alcance hasta que el backend implemente la funcionalidad correspondiente. Este documento NO inventará endpoints que no existen.

---

## Tabla de Contenido

1. [Contexto del Producto](#1-contexto-del-producto)
2. [Stack y Decisiones Técnicas](#2-stack-y-decisiones-técnicas)
3. [Estructura de Carpetas Recomendada](#3-estructura-de-carpetas-recomendada)
4. [Mapa de Rutas](#4-mapa-de-rutas)
5. [Especificación Detallada de Cada Página](#5-especificación-detallada-de-cada-página)
6. [Componentes Compartidos](#6-componentes-compartidos)
7. [Autenticación y Autorización](#7-autenticación-y-autorización)
8. [Estado del Servidor con TanStack Query](#8-estado-del-servidor-con-tanstack-query)
9. [Manejo de Errores y Feedback Visual](#9-manejo-de-errores-y-feedback-visual)
10. [Orden de Implementación Recomendado](#10-orden-de-implementación-recomendado)
11. [Checklist de Aceptación](#11-checklist-de-aceptación)

---

## 1. Contexto del Producto

### Problema que resuelve el sistema

La tienda universitaria (University Shop) necesita gestionar su catálogo de productos de forma centralizada y exponerlo digitalmente a sus dos tipos de usuarios: el personal administrativo que crea y mantiene el inventario, y los compradores (estudiantes, docentes, comunidad universitaria) que consultan qué artículos están disponibles y a qué precio. Sin este sistema, el catálogo vive en hojas de cálculo dispersas, los precios se desactualizan y no existe una fuente única de verdad que cualquier dispositivo pueda consultar.

El backend resuelve la lógica del catálogo: persistencia de productos y categorías, validaciones de integridad (SKU único, categoría única por nombre, no eliminar categorías con productos asociados) y seguridad de las operaciones de escritura. Sin embargo, el backend es una API REST que devuelve JSON: carece de interfaz visual, navegación, estados de carga, mensajes de confirmación, filtros interactivos y cualquier otro elemento que haga la experiencia de uso humana. El frontend es la capa que convierte esa API en un producto usable.

### Usuarios del frontend

**Administrador del catálogo** — El empleado de la tienda universitaria encargado de mantener el inventario. Necesita iniciar sesión de forma segura, organizar el catálogo en categorías temáticas (por ejemplo, "Papelería", "Tecnología", "Libros de Texto") y registrar, actualizar o retirar productos con su precio y código de referencia (SKU). Sus operaciones son críticas: un error en el precio o un SKU incorrecto afecta directamente a los compradores y a los sistemas externos que puedan consumir la API. Este usuario necesita formularios con validación clara, confirmaciones antes de acciones destructivas y mensajes de error precisos cuando el backend rechaza una operación.

**Cliente / Comprador** — El estudiante, docente o miembro de la comunidad universitaria que consulta el catálogo para tomar decisiones de compra. No necesita cuenta ni autenticación. Necesita ver los productos disponibles con precio y categoría, buscar por nombre o filtrar por categoría, y consultar los detalles de un artículo específico. Su experiencia debe ser rápida, sin fricciones y accesible desde cualquier dispositivo (incluyendo móvil).

### Por qué existe el frontend

La API expone datos en JSON, pero un JSON no es navegable por un humano sin herramientas técnicas. El frontend existe para:

1. **Presentar el catálogo visualmente**: listas con imágenes (o placeholders), precios formateados con moneda, categorías como etiquetas de navegación.
2. **Proteger las operaciones administrativas**: aunque el backend valida el JWT, el frontend es quien decide qué pantallas y botones mostrar según el estado de autenticación, evitando que el comprador vea controles irrelevantes.
3. **Dar feedback inmediato**: antes de enviar un formulario al backend, el frontend valida los campos y muestra errores junto al input que los causó. Esto reduce la carga de peticiones erróneas y mejora la percepción de velocidad.
4. **Gestionar estados de carga, vacío y error**: la API nunca le dice al usuario "estamos buscando..." o "no hay productos que coincidan". Esa responsabilidad es del frontend.
5. **Hacer accesible el health check**: el frontend puede verificar al cargarse que el backend está disponible y mostrar un banner si no lo está, en lugar de un error técnico incomprensible.

Toda pantalla y componente definido en este documento se puede rastrear directamente a una necesidad de alguno de estos dos perfiles de usuario.

---

## 2. Stack y Decisiones Técnicas

| Tecnología | Decisión | Justificación |
|---|---|---|
| **React 18 + TypeScript** | Obligatorio | Base ya establecida en el proyecto; TypeScript garantiza que los DTOs de `src/types/api.ts` se usen correctamente sin errores de tipo en tiempo de ejecución. |
| **Vite** | Obligatorio | Ya configurado; hot reload instantáneo hace que el desarrollo contra la API local sea fluido. |
| **React Router v6** | Recomendado | Estándar de facto para navegación en React; las rutas anidadas permiten compartir el layout de admin sin duplicar código; el estado de filtros y paginación puede vivir en la URL (facilita compartir links y el botón "atrás"). |
| **Axios** | Obligatorio | `src/services/api.ts` ya está escrito con interceptores JWT; no se debe reemplazar por `fetch` en este proyecto. |
| **React Hook Form + Zod** | Recomendado | RHF minimiza re-renders en formularios largos; Zod permite definir esquemas que replican exactamente las anotaciones `@NotBlank`, `@Size`, `@DecimalMin` del backend, cerrando el ciclo de validación antes de que la petición salga. |
| **Context API (AuthContext)** | Recomendado | El estado de autenticación es simple (token sí/no + usuario); no justifica Redux ni Zustand para esta app. Context es suficiente y no agrega dependencias. |
| **TanStack Query (React Query v5)** | Recomendado | Elimina el boilerplate de `useEffect + useState` para cada fetch; maneja caché, estados de loading/error, revalidación y la invalidación de queries tras mutaciones (por ejemplo, crear un producto actualiza automáticamente la lista). Sin esto, cada pantalla repetiría el mismo patrón manual de tres estados. |
| **Tailwind CSS + shadcn/ui** | Recomendado | shadcn/ui provee componentes accesibles (Dialog, Table, Form, Toast) que se copian al proyecto y se adaptan; Tailwind evita crear CSS custom para layouts y espaciado. Alternativa aceptable: MUI o CSS Modules si el equipo prefiere. |
| **sonner** | Recomendado | Librería de toasts ligera y con buen diseño por defecto; compatible con Tailwind; reemplaza el uso de `alert()` para notificaciones de éxito/error. |
| **lucide-react** | Recomendado | Iconos consistentes con el diseño de shadcn/ui; tree-shakeable (solo se incluyen los íconos usados). |

---

## 3. Estructura de Carpetas Recomendada

```
src/
├── components/
│   ├── ui/                   ← Componentes shadcn/ui (Button, Input, Dialog, Table…)
│   ├── layout/
│   │   ├── Header.tsx        ← Barra superior: logo, nav, botón login/logout
│   │   ├── AdminSidebar.tsx  ← Navegación lateral solo en rutas /admin
│   │   ├── PublicLayout.tsx  ← Layout para páginas públicas (catálogo)
│   │   └── AdminLayout.tsx   ← Layout para páginas de administración
│   └── shared/
│       ├── DataTable.tsx     ← Tabla genérica con paginación y estado vacío
│       ├── ConfirmDialog.tsx ← Modal de confirmación para acciones destructivas
│       ├── FormField.tsx     ← Label + Input + mensaje de error Zod
│       ├── EmptyState.tsx    ← Estado vacío con ícono y CTA opcional
│       ├── LoadingSpinner.tsx
│       ├── Skeleton.tsx      ← Esqueletos de carga para listas y detalles
│       ├── ErrorBoundary.tsx ← Captura errores no manejados en React
│       ├── BackendStatus.tsx ← Banner cuando el backend no responde
│       └── ProductCard.tsx   ← Tarjeta de producto para el catálogo público
├── pages/
│   ├── auth/
│   │   └── LoginPage.tsx
│   ├── catalog/
│   │   ├── ProductsListPage.tsx    ← Catálogo público con búsqueda y filtros
│   │   └── ProductDetailPage.tsx  ← Detalle de un producto
│   ├── admin/
│   │   ├── products/
│   │   │   ├── AdminProductsPage.tsx   ← Lista admin con acciones CRUD
│   │   │   └── ProductFormPage.tsx     ← Crear / editar producto
│   │   └── categories/
│   │       ├── AdminCategoriesPage.tsx ← Lista admin con acciones CRUD
│   │       └── CategoryFormPage.tsx    ← Crear / editar categoría
│   └── NotFoundPage.tsx
├── services/
│   └── api.ts               ← Instancia Axios + servicios (ya generado en docs/)
├── types/
│   └── api.ts               ← Interfaces TypeScript (ya generado en docs/)
├── hooks/
│   ├── useAuth.ts           ← Leer/escribir estado de autenticación desde AuthContext
│   ├── useProducts.ts       ← Queries y mutaciones de productos (TanStack Query)
│   └── useCategories.ts     ← Queries y mutaciones de categorías
├── context/
│   └── AuthContext.tsx      ← Provider JWT: token, isAuthenticated, login, logout
├── routes/
│   ├── AppRoutes.tsx        ← Árbol completo de rutas de la app
│   ├── ProtectedRoute.tsx   ← Redirige a /login si no hay JWT
│   └── PublicRoute.tsx      ← Redirige a /admin si ya hay JWT (para /login)
├── lib/
│   ├── validation.ts        ← Esquemas Zod que replican las validaciones del backend
│   └── format.ts            ← Funciones de formato: precio con moneda, fecha legible
├── App.tsx                  ← QueryClientProvider + RouterProvider + Toaster
└── main.tsx                 ← Punto de entrada; monta AuthContext
```

Esta organización separa la aplicación **por responsabilidad, no por tipo de archivo**. Las carpetas `pages/` contienen pantallas completas que corresponden a rutas del router. Los `hooks/` encapsulan la lógica de acceso a datos (TanStack Query) para que los componentes no mezclen lógica de servidor con lógica de presentación. Los `components/shared/` son los bloques reutilizables que aparecen en múltiples páginas. `lib/` contiene utilidades puras sin efectos secundarios. Esta separación permite que un desarrollador abra `pages/admin/products/ProductFormPage.tsx` y entienda qué hace sin necesitar leer código de otras capas.

---

## 4. Mapa de Rutas

> **Importante:** NO existe endpoint de registro en el backend. La ruta `/register` no se implementa.

| Ruta | Acceso | Página | Endpoints backend consumidos |
|------|--------|--------|------------------------------|
| `/` | Público | Redirige a `/catalog` | — |
| `/login` | Público (redirige a /admin si hay JWT) | `LoginPage` | `POST /auth/login` |
| `/catalog` | Público | `ProductsListPage` | `GET /products`, `GET /categories` |
| `/catalog/:productId` | Público | `ProductDetailPage` | `GET /products/{id}` |
| `/admin` | Protegido | Redirige a `/admin/products` | — |
| `/admin/products` | Protegido | `AdminProductsPage` | `GET /products`, `GET /categories` |
| `/admin/products/new` | Protegido | `ProductFormPage` (modo crear) | `POST /products`, `GET /categories` |
| `/admin/products/:id/edit` | Protegido | `ProductFormPage` (modo editar) | `GET /products/{id}`, `PUT /products/{id}`, `GET /categories` |
| `/admin/categories` | Protegido | `AdminCategoriesPage` | `GET /categories` |
| `/admin/categories/new` | Protegido | `CategoryFormPage` (modo crear) | `POST /categories` |
| `/admin/categories/:id/edit` | Protegido | `CategoryFormPage` (modo editar) | `GET /categories/{id}`, `PUT /categories/{id}` |
| `*` | Cualquiera | `NotFoundPage` | — |

---

## 5. Especificación Detallada de Cada Página

---

### Página: LoginPage

**Ruta:** `/login`  
**Acceso:** Pública. Si ya hay JWT válido en `localStorage`, redirige automáticamente a `/admin/products`.  
**Propósito de negocio:** Permite que el administrador de la tienda se identifique para obtener acceso a las operaciones de gestión del catálogo. Sin este paso, el backend rechaza cualquier intento de crear, editar o eliminar productos o categorías.

**Componentes UI:**
- Formulario centrado verticalmente con logo/nombre de la tienda
- Campo de texto para `username` (label "Usuario")
- Campo de contraseña para `password` (label "Contraseña") con botón de mostrar/ocultar
- Botón de submit "Iniciar sesión" con estado disabled mientras se procesa
- Mensaje de error inline bajo el formulario (no en toast, para no romper el flujo de login)

**Datos que muestra:**
- Ninguno del backend. Es un formulario de entrada pura.
- El campo username puede pre-rellenarse si se detecta un `username` en `localStorage` (opcional).

**Acciones del usuario:**
- Escribir usuario y contraseña y enviar el formulario
- Presionar Enter para enviar (comportamiento estándar de formulario)
- Ver/ocultar la contraseña

**Llamadas a la API:**
- `POST /api/v1/auth/login` — se dispara al enviar el formulario con datos válidos
- Si responde 200: guarda el token en `localStorage.setItem('jwt', response.token)`, actualiza `AuthContext`, redirige a `/admin/products`
- Si responde 401: muestra "Usuario o contraseña incorrectos" bajo el formulario (no en toast)

**Validaciones del formulario:**
- `username`: requerido (`z.string().min(1, 'El usuario es obligatorio')`) — refleja `@NotBlank` del `AuthRequestDTO`
- `password`: requerido (`z.string().min(1, 'La contraseña es obligatoria')`) — refleja `@NotBlank` del `AuthRequestDTO`

**Estados a manejar:**
- **Idle:** formulario editable, botón activo
- **Loading:** botón desactivado con spinner, campos read-only
- **Error 401:** mensaje "Usuario o contraseña incorrectos" bajo el formulario, campos vuelven a ser editables
- **Error 500 / red:** toast "Error del servidor, intenta de nuevo"
- **Éxito:** redirección inmediata a `/admin/products`

**Errores específicos del backend a manejar:**
- `401` → mostrar error inline en el formulario (no redirigir, el usuario está intentando autenticarse)
- `500` → toast rojo genérico

**Notas de UX:**
- Al cargar la página, dar foco automático al campo `username`
- El estado de error del formulario debe limpiarse cuando el usuario empiece a escribir de nuevo
- No mostrar texto de ayuda sobre las credenciales de demo en producción (esto es solo para desarrollo)

---

### Página: ProductsListPage (Catálogo Público)

**Ruta:** `/catalog`  
**Acceso:** Pública. No requiere autenticación.  
**Propósito de negocio:** Es la pantalla principal del catálogo de la tienda universitaria. Permite a cualquier visitante explorar todos los productos disponibles, filtrar por categoría, buscar por nombre y navegar entre páginas. Es la respuesta al problema central del proyecto: centralizar la consulta del inventario.

**Componentes UI:**
- `<Header />` con logo y link a `/login` si no hay sesión (o a `/admin` si hay sesión)
- Barra de búsqueda con campo de texto y botón de búsqueda (ícono lupa)
- Selector de categoría: dropdown o chips/pills con las categorías disponibles + opción "Todas"
- Grid o lista de `<ProductCard />` para cada producto
- Controles de paginación: botones "Anterior" / "Siguiente" + indicador "Página X de Y (Z productos)"
- `<EmptyState />` cuando no hay productos que coincidan con los filtros
- `<Skeleton />` mientras carga la primera petición

**Datos que muestra:**
- Por producto: nombre, precio formateado con moneda (ej: `$89.99`), categoría, SKU
- Metadatos de paginación: página actual, total de páginas, total de productos
- Lista de categorías para el filtro (cargada por separado de `GET /categories`)

**Acciones del usuario:**
- Escribir en el campo de búsqueda para filtrar por nombre (con debounce de 400ms)
- Seleccionar una categoría para filtrar
- Navegar entre páginas con los controles de paginación
- Hacer clic en un producto para ver su detalle (navega a `/catalog/:productId`)
- Combinar filtro de nombre + filtro de categoría simultáneamente

**Llamadas a la API:**
- `GET /categories` — al montar la página, para poblar el selector de categoría. Query key: `['categories']`. Se hace una sola vez.
- `GET /products?name=&categoryId=&page=0&size=12` — al montar y cada vez que cambian los filtros o la página. Query key: `['products', { page, size, name, categoryId }]`. Se dispara con debounce cuando el usuario escribe en la búsqueda.

**Validaciones del formulario:** No aplica (es solo búsqueda, no un formulario de creación).

**Estados a manejar:**
- **Loading inicial:** grid de `<Skeleton />` con 12 tarjetas placeholder
- **Loading por filtro:** indicador sutil (spinner en el borde del campo de búsqueda) sin ocultar el contenido existente
- **Vacío (sin productos):** `<EmptyState />` con mensaje "No encontramos productos con esos criterios" y botón "Limpiar filtros"
- **Error de red:** `<EmptyState />` con mensaje "Error al cargar el catálogo" y botón "Reintentar"
- **Con datos:** grid de productos + paginación

**Errores específicos del backend a manejar:**
- `500` → mensaje en el área de contenido (no toast), con botón de reintento

**Notas de UX:**
- Los parámetros de búsqueda, categoría y página deben vivir en la URL como query params: `/catalog?name=laptop&categoryId=abc&page=2`. Esto permite que el usuario comparta un link directo al resultado de búsqueda y que el botón "atrás" funcione correctamente.
- Al cambiar el filtro de nombre o categoría, reiniciar `page` a 0 automáticamente.
- El debounce de 400ms en la búsqueda evita peticiones por cada tecla pulsada.
- En móvil: los filtros colapsan en un botón "Filtrar" que abre un panel lateral o modal.

---

### Página: ProductDetailPage

**Ruta:** `/catalog/:productId`  
**Acceso:** Pública.  
**Propósito de negocio:** Permite al comprador ver toda la información de un artículo específico antes de tomar una decisión de compra: nombre completo, SKU (código de referencia para mencionar al personal), precio exacto, categoría y fecha de incorporación al catálogo.

**Componentes UI:**
- `<Header />` con breadcrumb: "Catálogo > [Nombre del producto]"
- Sección principal: nombre del producto (h1), precio formateado, categoría como badge/chip
- Tabla o lista de detalles: SKU, fecha de incorporación al catálogo (formateada como "30 may. 2026")
- Botón "Volver al catálogo" (navega a `/catalog` con los filtros previos si están en URL)
- `<Skeleton />` mientras carga el producto
- `<EmptyState />` con opción de volver si el producto no existe

**Datos que muestra:**
- `id` — no se muestra al usuario
- `sku` — código de referencia (útil para mencionar al personal de la tienda)
- `name` — nombre completo del artículo
- `price` — formateado con símbolo de moneda y 2 decimales
- `dateCreated` — formateado de forma legible: `"Disponible desde el 30 de mayo de 2026"`
- `category.name` — nombre de la categoría como etiqueta visual

**Acciones del usuario:**
- Leer la información del producto
- Hacer clic en "Volver al catálogo"
- (Opcional futuro: compartir la URL del producto)

**Llamadas a la API:**
- `GET /products/{id}` — al montar la página con el `productId` del parámetro de URL. Query key: `['products', productId]`.

**Validaciones del formulario:** No aplica (es una página de solo lectura).

**Estados a manejar:**
- **Loading:** `<Skeleton />` con la forma aproximada del layout
- **Error 404:** `<EmptyState />` con "Este producto ya no está disponible" y botón "Ver catálogo"
- **Error de red:** mensaje con botón "Reintentar"
- **Con datos:** contenido completo del producto

**Errores específicos del backend a manejar:**
- `404` → mostrar estado vacío con mensaje amigable, NO página 404 genérica
- `500` → `<EmptyState />` con "Error al cargar el producto" y reintento

**Notas de UX:**
- El `productId` de MongoDB es un ObjectId (string de 24 caracteres hexadecimales). Si el parámetro de URL no tiene esa forma, se puede mostrar el 404 sin siquiera hacer la petición.
- La URL de esta página es compartible: cualquier persona puede recibir el link y ver el mismo producto.

---

### Página: AdminProductsPage

**Ruta:** `/admin/products`  
**Acceso:** Protegida (requiere JWT válido).  
**Propósito de negocio:** Es el panel de control del administrador para gestionar el inventario de productos. Permite ver todos los productos del catálogo en formato de tabla, con acciones de edición y eliminación por fila, y un botón para registrar un nuevo artículo.

**Componentes UI:**
- `<AdminLayout />` que incluye `<AdminSidebar />` con links a productos y categorías
- Encabezado de sección: "Gestión de Productos" + botón "Nuevo producto" (link a `/admin/products/new`)
- Filtros: campo de búsqueda por nombre, selector de categoría (igual que en el catálogo público)
- `<DataTable />` con columnas: SKU, Nombre, Precio, Categoría, Fecha de alta, Acciones
- Acciones por fila: botón "Editar" (link a `/admin/products/:id/edit`) + botón "Eliminar" (abre `<ConfirmDialog />`)
- Paginación integrada en `<DataTable />`
- `<Skeleton />` mientras carga la lista
- `<EmptyState />` si no hay productos

**Datos que muestra:**
- Todos los campos de `ProductResponse`: SKU, nombre, precio (formateado), `category.name`, `dateCreated` (formateada)
- Metadatos de paginación
- Indicador del total de productos: "Mostrando X-Y de Z productos"

**Acciones del usuario:**
- Buscar productos por nombre (con debounce de 400ms)
- Filtrar por categoría
- Navegar entre páginas
- Hacer clic en "Editar" → navega a `/admin/products/:id/edit`
- Hacer clic en "Eliminar" → abre `<ConfirmDialog />` con el mensaje "¿Eliminar el producto [nombre]? Esta acción no se puede deshacer."
- Confirmar la eliminación en el dialog
- Hacer clic en "Nuevo producto" → navega a `/admin/products/new`

**Llamadas a la API:**
- `GET /categories` — al montar, para el selector de filtro. Query key: `['categories']`.
- `GET /products?name=&categoryId=&page=0&size=20` — al montar y con filtros. Query key: `['products', { page, size, name, categoryId }]`.
- `DELETE /products/{id}` — al confirmar en `<ConfirmDialog />`. Tras éxito: invalida `['products']` y muestra toast "Producto eliminado correctamente".

**Validaciones del formulario:** No aplica (búsqueda y filtros, no creación).

**Estados a manejar:**
- **Loading:** `<Skeleton />` en la tabla (filas placeholder)
- **Vacío:** `<EmptyState />` con "No hay productos registrados" y CTA "Crear primer producto"
- **Error de red:** mensaje en el área de la tabla con botón reintento
- **Eliminando:** botón "Eliminar" de la fila en estado loading, tabla no interactuable
- **Éxito de eliminación:** toast verde "Producto eliminado", lista actualizada automáticamente por TanStack Query

**Errores específicos del backend a manejar:**
- `401` → interceptor redirige a `/login`
- `403` → toast "No tienes permiso para eliminar productos"
- `404` → toast "El producto ya no existe" + recargar lista
- `500` → toast rojo genérico

**Notas de UX:**
- El dialog de confirmación de eliminación debe mostrar el nombre del producto (no el ID), para que el administrador confirme conscientemente qué está borrando.
- Los parámetros de filtro y página deben estar en la URL para que el botón "atrás" (después de editar un producto) devuelva al administrador a la misma página y filtros.
- Tamaño de página recomendado para admin: 20 (más denso que el catálogo público de 12).

---

### Página: ProductFormPage (Crear)

**Ruta:** `/admin/products/new`  
**Acceso:** Protegida.  
**Propósito de negocio:** Permite al administrador incorporar un nuevo artículo al inventario del catálogo. Requiere proporcionar el código de referencia (SKU) que es único e inmutable, el nombre, el precio y la categoría a la que pertenece.

**Componentes UI:**
- `<AdminLayout />`
- Breadcrumb: "Gestión de Productos > Nuevo Producto"
- Formulario con campos:
  - `sku`: campo de texto, label "SKU (código de referencia)"
  - `name`: campo de texto, label "Nombre del producto"
  - `price`: campo numérico con símbolo `$`, label "Precio"
  - `categoryId`: select/dropdown con las categorías existentes, label "Categoría"
- Aviso informativo bajo el campo SKU: "El SKU es permanente. No podrá modificarse después de crear el producto."
- Botón "Crear producto" (submit, con spinner en loading)
- Botón "Cancelar" (link a `/admin/products`)

**Datos que muestra:**
- Lista de categorías disponibles para el selector, cargadas de `GET /categories`
- Si no hay categorías: `<EmptyState />` con "Debes crear al menos una categoría antes de agregar productos" y link a `/admin/categories/new`

**Acciones del usuario:**
- Completar el formulario con los datos del producto
- Seleccionar una categoría del listado
- Enviar el formulario
- Cancelar y volver a la lista

**Llamadas a la API:**
- `GET /categories/all` o `GET /categories` — al montar, para poblar el selector. Query key: `['categories']`.
- `POST /products` — al enviar el formulario válido. Tras éxito: invalida `['products']`, muestra toast "Producto creado correctamente", navega a `/admin/products`.

**Validaciones del formulario** (esquema Zod que replica el `ProductRequestDTO` del backend):

```
sku:        z.string()
              .min(1, 'El SKU es obligatorio')
              .max(100, 'El SKU no puede exceder 100 caracteres')
              .regex(/^[A-Z0-9\-_]+$/i, 'El SKU solo puede contener letras, números, guiones y guiones bajos')

name:       z.string()
              .min(1, 'El nombre es obligatorio')
              .max(255, 'El nombre no puede exceder 255 caracteres')

price:      z.number({
              required_error: 'El precio es obligatorio',
              invalid_type_error: 'El precio debe ser un número'
            })
              .min(0.01, 'El precio debe ser mayor a 0.00')
              .multipleOf(0.01, 'Máximo 2 decimales')

categoryId: z.string()
              .min(1, 'La categoría es obligatoria')
```

Nota: `@NotBlank` → `z.string().min(1, ...)`. `@Size(max=100)` → `.max(100, ...)`. `@DecimalMin("0.01")` → `.min(0.01, ...)`. `@Digits(integer=10, fraction=2)` → `.multipleOf(0.01)`. El campo `sku` no tiene una restricción de formato en el backend, se agrega aquí como buena práctica de UX pero NO es autoritativa.

**Estados a manejar:**
- **Loading categorías:** spinner/skeleton en el selector de categoría
- **Sin categorías:** `<EmptyState />` descriptivo con CTA
- **Idle:** formulario editable, botón activo
- **Submitting:** botón con spinner y texto "Creando...", formulario bloqueado
- **Error 400 con fieldErrors:** mostrar cada mensaje de error junto al campo correspondiente (usando `<FormField />`)
- **Error 409 (SKU duplicado):** mostrar "Este SKU ya está registrado en el catálogo" junto al campo `sku`
- **Error 404 (categoría no existe):** raro en flujo normal, toast "La categoría seleccionada ya no existe, recarga la página"
- **Éxito:** toast verde + redirección

**Errores específicos del backend a manejar:**
- `400` con `fieldErrors` → mapear cada `{ field, message }` al campo del formulario
- `409` → mensaje en campo `sku`
- `401` → interceptor redirige a `/login`
- `500` → toast rojo

**Notas de UX:**
- El aviso sobre la inmutabilidad del SKU debe ser visible antes de que el usuario envíe el formulario, no solo como error posterior.
- El campo `price` debe usar un input de tipo numérico con paso de 0.01 para evitar valores inválidos desde el teclado.
- Si el usuario llega a esta página con categorías ya en `queryCache`, el selector debe mostrarlas inmediatamente sin spinner.

---

### Página: ProductFormPage (Editar)

**Ruta:** `/admin/products/:id/edit`  
**Acceso:** Protegida.  
**Propósito de negocio:** Permite al administrador actualizar el nombre, precio o categoría de un artículo existente. El SKU no es modificable (es el identificador comercial que puede estar referenciado en facturas o sistemas externos).

**Componentes UI:**
- `<AdminLayout />`
- Breadcrumb: "Gestión de Productos > [Nombre del producto]"
- Aviso destacado: "El SKU no puede modificarse" (con ícono de información, en gris)
- Campo SKU: visible pero deshabilitado (`disabled`), con tooltip "El SKU es un identificador permanente"
- Formulario con campos editables: `name`, `price`, `categoryId` (mismos componentes que en creación)
- Botón "Guardar cambios" (submit)
- Botón "Cancelar" (link a `/admin/products`)

**Datos que muestra:**
- Datos actuales del producto cargados de `GET /products/{id}` (pre-rellena el formulario)
- Lista de categorías para el selector
- SKU actual (solo lectura)
- Fecha de alta del producto (informativa, no editable)

**Acciones del usuario:**
- Modificar nombre, precio y/o categoría
- Guardar los cambios
- Cancelar y volver

**Llamadas a la API:**
- `GET /products/{id}` — al montar, para pre-rellenar el formulario. Query key: `['products', id]`.
- `GET /categories` — al montar, para el selector. Query key: `['categories']`.
- `PUT /products/{id}` — al enviar el formulario (cuerpo sin SKU). Tras éxito: invalida `['products']` y `['products', id]`, toast "Cambios guardados", navega a `/admin/products`.

**Validaciones del formulario** (esquema Zod que replica `ProductUpdateDTO`):

```
name:       z.string()
              .min(1, 'El nombre es obligatorio')
              .max(255, 'El nombre no puede exceder 255 caracteres')

price:      z.number({
              required_error: 'El precio es obligatorio',
              invalid_type_error: 'El precio debe ser un número'
            })
              .min(0.01, 'El precio debe ser mayor a 0.00')
              .multipleOf(0.01, 'Máximo 2 decimales')

categoryId: z.string()
              .min(1, 'La categoría es obligatoria')
```

No incluye `sku` (el backend no lo acepta en PUT y el campo está deshabilitado en el formulario).

**Estados a manejar:**
- **Loading inicial (producto + categorías):** `<Skeleton />` con la forma del formulario
- **Error 404 al cargar producto:** `<EmptyState />` "Este producto no existe" + link a lista
- **Idle (datos cargados):** formulario pre-rellenado y editable
- **Submitting:** botón con spinner, formulario bloqueado
- **Error 400 con fieldErrors:** mensajes junto a campos
- **Error 404 (categoría no existe):** toast descriptivo
- **Éxito:** toast + redirección

**Errores específicos del backend a manejar:**
- `400` con `fieldErrors` → mapear a campos
- `404` al cargar el producto → estado vacío descriptivo
- `401` → interceptor a `/login`
- `500` → toast rojo

**Notas de UX:**
- Detectar si el usuario no modificó ningún campo y deshabilitar el botón "Guardar cambios" en ese caso.
- Si el usuario navegó desde la lista de admin y tenía filtros activos, el botón "Cancelar" debe devolverlo a la misma URL con filtros (usar `navigate(-1)` o guardar la URL previa).

---

### Página: AdminCategoriesPage

**Ruta:** `/admin/categories`  
**Acceso:** Protegida.  
**Propósito de negocio:** Permite al administrador gestionar las secciones del catálogo. Las categorías son obligatorias para crear productos: sin categorías, el catálogo no puede organizarse. Esta pantalla es la primera que debe usar el administrador al configurar el sistema.

**Componentes UI:**
- `<AdminLayout />`
- Encabezado: "Gestión de Categorías" + botón "Nueva categoría" (link a `/admin/categories/new`)
- `<DataTable />` con columnas: Nombre, Descripción, Acciones
- Acciones por fila: "Editar" + "Eliminar"
- `<ConfirmDialog />` para eliminación con advertencia especial
- `<EmptyState />` si no hay categorías

**Datos que muestra:**
- Lista completa de `CategoryResponse[]` (sin paginación — el backend devuelve todas en un solo request)
- Nombre y descripción de cada categoría

**Acciones del usuario:**
- Crear nueva categoría (navega a `/admin/categories/new`)
- Editar categoría (navega a `/admin/categories/:id/edit`)
- Eliminar categoría (abre `<ConfirmDialog />`)

**Llamadas a la API:**
- `GET /categories` — al montar. Query key: `['categories']`.
- `DELETE /categories/{id}` — al confirmar eliminación. Tras éxito: invalida `['categories']`, toast "Categoría eliminada". Si responde 409: mostrar error especial.

**Validaciones del formulario:** No aplica.

**Estados a manejar:**
- **Loading:** `<Skeleton />` en la tabla
- **Vacío:** `<EmptyState />` con "Aún no hay categorías" y CTA "Crear primera categoría"
- **Eliminando:** botón en loading
- **Error 409 al eliminar:** el `<ConfirmDialog />` debe ser capaz de mostrar el error 409 recibido: "Esta categoría tiene productos asociados y no puede eliminarse. Primero mueve o elimina sus productos." El dialog permanece abierto con este mensaje.
- **Éxito:** toast + tabla actualizada

**Errores específicos del backend a manejar:**
- `409` al eliminar (`CategoryHasProductsException`) → mostrar dentro del dialog, NO toast. El administrador necesita ver la acción que falló y el motivo en el mismo contexto.
- `404` → toast "La categoría ya no existe" + recarga lista
- `401` → interceptor a `/login`

**Notas de UX:**
- El `<ConfirmDialog />` de eliminación de categoría debe incluir una advertencia adicional: "Si esta categoría tiene productos asociados, el backend rechazará la operación." Esto educa al usuario antes de que cometa el error, reduciendo la frustración.
- A diferencia de la eliminación de productos, aquí el error 409 es más probable y más informativo: el usuario tiene que actuar (mover los productos) antes de poder eliminar.

---

### Página: CategoryFormPage (Crear)

**Ruta:** `/admin/categories/new`  
**Acceso:** Protegida.  
**Propósito de negocio:** Permite al administrador crear una nueva sección del catálogo. Las categorías son la estructura organizativa que permite clasificar los productos: sin categorías, no se pueden registrar artículos.

**Componentes UI:**
- `<AdminLayout />`
- Breadcrumb: "Gestión de Categorías > Nueva Categoría"
- Formulario:
  - `name`: campo de texto, label "Nombre de la categoría" (requerido)
  - `description`: textarea, label "Descripción" (opcional)
- Botón "Crear categoría" (submit)
- Botón "Cancelar"

**Datos que muestra:** Ninguno del backend al montar.

**Acciones del usuario:**
- Completar el nombre (y opcionalmente la descripción) y enviar
- Cancelar y volver

**Llamadas a la API:**
- `POST /categories` — al enviar el formulario. Tras éxito: invalida `['categories']` e `['products']` (porque el selector de categoría en los formularios de producto usa esta query), toast "Categoría creada", navega a `/admin/categories`.

**Validaciones del formulario** (replica `CategoryRequestDTO`):

```
name:        z.string()
               .min(1, 'El nombre es obligatorio')
               .max(100, 'El nombre no puede exceder 100 caracteres')

description: z.string()
               .max(500, 'La descripción no puede exceder 500 caracteres')
               .optional()
               .or(z.literal(''))
```

**Estados a manejar:**
- **Idle:** formulario editable
- **Submitting:** botón con spinner, formulario bloqueado
- **Error 400 con fieldErrors:** mensajes junto a campos
- **Error 409 (nombre duplicado):** mostrar "Ya existe una categoría con este nombre" junto al campo `name`
- **Éxito:** toast + redirección

**Errores específicos del backend a manejar:**
- `409` (`CategoryNameAlreadyExistsException`) → mensaje en campo `name`
- `400` con `fieldErrors` → mensajes en campos correspondientes

**Notas de UX:**
- El campo `description` no debe mostrar error cuando está vacío; solo cuando excede 500 caracteres.

---

### Página: CategoryFormPage (Editar)

**Ruta:** `/admin/categories/:id/edit`  
**Acceso:** Protegida.  
**Propósito de negocio:** Permite al administrador actualizar el nombre o la descripción de una categoría existente, por ejemplo al expandir la oferta de la tienda y necesitar renombrar una sección.

**Componentes UI:**
- Igual que el formulario de creación, pero con datos pre-rellenados
- Breadcrumb: "Gestión de Categorías > [Nombre actual]"
- Mismos campos: `name` (editable) y `description` (editable)

**Datos que muestra:**
- Datos actuales de la categoría de `GET /categories/{id}` (pre-rellena el formulario)

**Acciones del usuario:**
- Modificar nombre y/o descripción y guardar
- Cancelar y volver

**Llamadas a la API:**
- `GET /categories/{id}` — al montar, para pre-rellenar. Query key: `['categories', id]`.
- `PUT /categories/{id}` — al enviar. Tras éxito: invalida `['categories']` y `['categories', id]`, toast "Cambios guardados", navega a `/admin/categories`.

**Validaciones del formulario:** Idénticas a CategoryFormPage (Crear).

**Estados a manejar:**
- **Loading inicial:** `<Skeleton />` con la forma del formulario
- **Error 404:** `<EmptyState />` "Esta categoría no existe"
- **Idle (datos cargados):** formulario pre-rellenado
- **Submitting:** botón con spinner
- **Error 409 (nombre duplicado):** mensaje en campo `name`
- **Éxito:** toast + redirección

**Errores específicos del backend a manejar:**
- `409` → mensaje en campo `name`
- `404` al cargar → estado vacío descriptivo
- `401` → interceptor a `/login`

---

### Página: NotFoundPage

**Ruta:** `*` (cualquier ruta no definida)  
**Acceso:** Cualquiera.  
**Propósito de negocio:** Evitar que el usuario vea una pantalla en blanco o un error técnico cuando navega a una URL que no existe.

**Componentes UI:**
- Mensaje amigable: "Página no encontrada" con código 404
- Botón "Volver al catálogo" (navega a `/catalog`)
- Botón "Volver atrás" (usa `navigate(-1)`)
- (Opcional) ilustración o ícono decorativo

**Datos que muestra:** Ninguno del backend.

**Acciones del usuario:**
- Volver al catálogo o volver a la página anterior

---

## 6. Componentes Compartidos

---

**`<Header />`**  
Barra de navegación superior presente en todas las páginas. Muestra el logo/nombre de la tienda y adapta su contenido según el estado de autenticación: si no hay sesión muestra un link "Acceder" que lleva a `/login`; si hay sesión muestra el nombre de usuario y un botón "Cerrar sesión". En rutas de admin, puede mostrar también el link "Ver catálogo".  
*Encuadre en el negocio:* el Header es el punto de entrada principal del sistema para ambos perfiles de usuario. Un comprador lo usa para navegar; un administrador para saber que está autenticado y poder salir.

**`<AdminSidebar />`**  
Panel lateral de navegación presente únicamente en las rutas `/admin/**`. Contiene links a "Productos" y "Categorías". Puede indicar visualmente en qué sección está el usuario actualmente (link activo resaltado).  
*Encuadre en el negocio:* el administrador necesita moverse frecuentemente entre la gestión de categorías y la de productos. El sidebar evita que tenga que volver al menú principal en cada cambio.

**`<DataTable<T> />`**  
Tabla genérica con paginación integrada. Acepta columnas configurables, datos de tipo genérico, metadatos de paginación (`page`, `totalPages`, `first`, `last`) y callbacks para cambio de página. Incluye `<EmptyState />` cuando `content.length === 0`. Se usa en `AdminProductsPage` y `AdminCategoriesPage`.  
*Encuadre en el negocio:* el administrador verá muchos productos y categorías. Una tabla con paginación hace manejable ese volumen sin sobrecargar la pantalla.

**`<ConfirmDialog />`**  
Modal de confirmación para acciones destructivas. Props: `title`, `description`, `onConfirm`, `onCancel`, `isLoading`, `errorMessage`. Soporta mostrar el mensaje de error del backend (especialmente el 409 de `CategoryHasProductsException`) dentro del propio modal, en lugar de cerrarlo y mostrar un toast.  
*Encuadre en el negocio:* protege al administrador de borrar accidentalmente una categoría con productos, o un producto del que puede haber registros. La visualización del error 409 dentro del modal mantiene el contexto y permite al usuario entender qué debe hacer antes de reintentar.

**`<FormField />`**  
Wrapper de campo de formulario que combina `<label>`, el input (cualquier tipo), y el mensaje de error de validación de Zod o del backend (`fieldErrors`). Gestiona los estados visual del campo (default, focus, error, disabled).  
*Encuadre en el negocio:* la retroalimentación de validación debe estar inmediatamente junto al campo que causó el error. Un mensaje de error genérico al final del formulario no es suficiente cuando hay varios campos.

**`<EmptyState />`**  
Componente que se muestra cuando una lista o sección no tiene datos. Props: `icon?`, `title`, `description`, `action?` (botón con CTA opcional). Se usa en listas vacías de productos/categorías y en páginas de error 404.  
*Encuadre en el negocio:* cuando el catálogo no tiene productos que coincidan con la búsqueda, el comprador necesita entender que no es un error del sistema sino un resultado vacío, y tener una forma de limpiar los filtros o volver al catálogo completo.

**`<LoadingSpinner />`** y **`<Skeleton />`**  
`<LoadingSpinner />` para acciones puntuales (submit de formulario, eliminación). `<Skeleton />` para estados de carga inicial de páginas completas o listas: renderiza una versión "fantasma" del layout real, evitando el efecto de "flash" entre carga y contenido.  
*Encuadre en el negocio:* la percepción de velocidad importa. Una pantalla en blanco durante la carga hace que el usuario dude de si el sistema funciona. Los skeletons comunican que hay contenido por llegar.

**`<ErrorBoundary />`**  
Componente de clase React que captura errores no manejados en el árbol de componentes hijos y muestra una pantalla de error genérica con opción de recargar, en lugar de una pantalla en blanco.  
*Encuadre en el negocio:* previene que un bug de rendering en una pantalla secundaria rompa toda la aplicación.

**`<BackendStatus />`**  
Banner pegajoso en la parte superior de la pantalla que aparece cuando el health check (`GET /health`) falla al arrancar la app. Desaparece cuando el backend vuelve a responder. Props: `isDown: boolean`.  
*Encuadre en el negocio:* si el backend está caído, todos los endpoints fallarán. Informar al usuario de esta situación global una sola vez (en lugar de mostrar errores en cada componente) es más claro y profesional.

**`<ProductCard />`**  
Tarjeta de presentación de un producto para el catálogo público. Muestra nombre, precio formateado, badge de categoría y SKU. Es un link que navega a `/catalog/:productId`.  
*Encuadre en el negocio:* el catálogo público es el escaparate de la tienda. Una tarjeta bien diseñada, consistente y responsiva hace el catálogo explorable desde cualquier dispositivo.

**`<ProtectedRoute />`**  
Wrapper de ruta que verifica la existencia del JWT en `localStorage`. Si no hay token, redirige a `/login` y guarda la URL intentada para redirigir de vuelta tras el login.  
*Encuadre en el negocio:* el backend protege las operaciones de escritura con JWT, pero el frontend también debe proteger las pantallas de administración para no mostrar formularios a usuarios que no pueden completar las acciones.

---

## 7. Autenticación y Autorización

### Flujo de login

El usuario abre `/login`, completa el formulario y hace clic en "Iniciar sesión". El frontend llama a `POST /api/v1/auth/login` con `{ username, password }`. Si el backend responde 200, guarda `response.token` en `localStorage.setItem('jwt', token)`, actualiza `AuthContext` para que `isAuthenticated` sea `true`, y redirige a `/admin/products`.

### Dónde guardar el JWT

El token se guarda en `localStorage` con la clave `jwt`. Esta decisión implica un trade-off conocido: `localStorage` es accesible por JavaScript de la página, lo que lo hace vulnerable a ataques XSS. Una alternativa más segura es usar una `httpOnly cookie`, que JavaScript no puede leer. Sin embargo, esta alternativa requiere que el backend envíe la cookie (no lo hace actualmente) y maneja el CORS de forma más compleja. Para esta etapa académica, `localStorage` es aceptable porque: (1) el backend no implementa cookies, (2) el alcance del proyecto no incluye usuarios con datos sensibles y (3) la mitigación principal de XSS (no ejecutar scripts de terceros no confiables) se gestiona con buenas prácticas de desarrollo.

### Inyección del token en cada request

El interceptor de request ya está definido en `src/services/api.ts`:
```typescript
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```
No es necesario pasar el token manualmente en ninguna llamada: todos los métodos de `productService`, `categoryService`, etc. lo incluyen automáticamente.

### Detección de token expirado

El interceptor de response detecta el código 401:
```typescript
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('jwt');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  },
);
```
El JWT tiene una validez de 24 horas (configurado en `application.yml`). No hay mecanismo de refresh token: cuando expira, el usuario debe iniciar sesión de nuevo.

### Cierre de sesión

```typescript
const logout = () => {
  localStorage.removeItem('jwt');
  // Actualizar AuthContext para isAuthenticated = false
  navigate('/login');
};
```
No se hace ninguna llamada al backend al cerrar sesión (el backend es stateless; basta con dejar de enviar el token).

### Rutas públicas vs. protegidas

- **Públicas:** `/login`, `/catalog`, `/catalog/:productId`. Cualquier persona puede acceder.
- **Protegidas:** todas las rutas `/admin/**`. Si no hay JWT en `localStorage`, `<ProtectedRoute />` redirige a `/login`.
- **Ruta de login con redirección inversa:** si el usuario ya tiene sesión y navega a `/login`, `<PublicRoute />` lo redirige a `/admin/products`.

### Roles en el JWT

El backend actual asigna siempre el rol `ROLE_ADMIN` al token y no tiene múltiples roles. El frontend no necesita inspeccionar claims del JWT para mostrar/ocultar UI: la lógica es binaria (autenticado → admin; no autenticado → solo catálogo). Si en el futuro se agregan roles (por ejemplo, `ROLE_VIEWER`), habría que decodificar el JWT en el cliente con `jwtDecode` y ajustar la lógica de visibilidad.

---

## 8. Estado del Servidor con TanStack Query

### Configuración del QueryClient

```typescript
// En App.tsx
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5,   // 5 minutos antes de revalidar
      retry: 1,                    // 1 reintento en caso de error de red
    },
  },
});
```

### Query Keys definidos

| Recurso | Query Key | Endpoint | Notas |
|---------|-----------|----------|-------|
| Lista paginada de productos | `['products', { page, size, name, categoryId }]` | `GET /products` | Cada combinación de filtros es una query distinta en caché |
| Todos los productos (sin paginar) | `['products', 'all']` | `GET /products/all` | Para dropdowns/selects |
| Producto individual | `['products', id]` | `GET /products/{id}` | `id` es el ObjectId como string |
| Lista de categorías | `['categories']` | `GET /categories` | Se usa en filtros, selectores y la tabla de admin |
| Categoría individual | `['categories', id]` | `GET /categories/{id}` | Para pre-rellenar formularios de edición |
| Health del backend | `['health']` | `GET /health` | Verificado al cargar la app |

### Invalidaciones de queries tras mutaciones

| Mutación | Invalida | Razonamiento |
|----------|----------|--------------|
| `POST /products` | `['products']` (todas las queries de lista) | El nuevo producto debe aparecer en la lista |
| `PUT /products/{id}` | `['products']` y `['products', id]` | La lista y el detalle deben reflejar el cambio |
| `DELETE /products/{id}` | `['products']` | El producto eliminado debe desaparecer de la lista |
| `POST /categories` | `['categories']` y `['products']` | Las queries de productos usan categorías en el selector |
| `PUT /categories/{id}` | `['categories']` y `['categories', id]` | La lista y el formulario de edición deben actualizarse |
| `DELETE /categories/{id}` | `['categories']` y `['products']` | Los productos que mostraban esa categoría deben refrescarse |

### Hooks personalizados

Se crean en `src/hooks/` para encapsular TanStack Query y ocultar los detalles al componente:

**`useProducts.ts`:**
- `useProductList(params)` → `useQuery(['products', params], ...)`
- `useProductDetail(id)` → `useQuery(['products', id], ...)`
- `useCreateProduct()` → `useMutation(...)` con invalidación
- `useUpdateProduct(id)` → `useMutation(...)` con invalidación
- `useDeleteProduct()` → `useMutation(...)` con invalidación

**`useCategories.ts`:**
- `useCategoryList()` → `useQuery(['categories'], ...)`
- `useCategoryDetail(id)` → `useQuery(['categories', id], ...)`
- `useCreateCategory()` → `useMutation(...)` con invalidación
- `useUpdateCategory(id)` → `useMutation(...)` con invalidación
- `useDeleteCategory()` → `useMutation(...)` con invalidación

---

## 9. Manejo de Errores y Feedback Visual

### Tabla de respuestas UI por código de error

| Código HTTP | Causa en el backend | Comportamiento UI |
|-------------|---------------------|-------------------|
| `400` con `fieldErrors` | Validación falló (`@NotBlank`, `@Size`, etc.) | Mostrar cada `fieldErrors[n].message` junto al campo `fieldErrors[n].field` correspondiente en el formulario. El formulario permanece abierto. |
| `400` sin `fieldErrors` | Error de parsing o validación genérica | Toast rojo con el `message` del backend |
| `401` | Token ausente, inválido o expirado | El interceptor borra el JWT, redirige a `/login`, muestra toast "Tu sesión ha expirado" al cargar la página de login |
| `403` | Sin permisos (ROLE_ADMIN requerido) | Toast rojo "No tienes permiso para realizar esta acción" |
| `404` | Recurso no encontrado | En páginas de detalle/edición: `<EmptyState />` con mensaje descriptivo. En acciones de la lista: toast rojo + recarga de la lista. |
| `409` | Conflicto (SKU duplicado, nombre de categoría duplicado, categoría con productos) | En formularios: mostrar el `message` del backend junto al campo que causó el conflicto. En eliminaciones: mostrar dentro del `<ConfirmDialog />`. |
| `500` | Error interno del servidor | Toast rojo genérico "Error del servidor. Por favor, inténtalo de nuevo." |
| Error de red | Backend caído o sin internet | `<BackendStatus />` banner persistente "Sin conexión con el servidor". Toast rojo en operaciones individuales. |

### Reglas de diseño para feedback

**Toasts de éxito** (color verde, `sonner`):
- Texto conciso: "Producto creado", "Cambios guardados", "Categoría eliminada"
- Duración: 3 segundos con auto-dismiss
- Posición: esquina superior derecha (o inferior derecha en móvil)

**Toasts de error** (color rojo):
- Texto del campo `message` de `ApiError` siempre que esté disponible; de lo contrario, mensaje genérico
- Duración: persistente hasta que el usuario los cierre (no auto-dismiss)
- El usuario no debe tener que recordar el error; que permanezca visible hasta que actúe

**Skeletons** (estado de carga inicial):
- Siempre presentes cuando la página carga datos por primera vez
- Deben imitar el layout real del contenido: número de filas de tabla esperado, tamaño aproximado de tarjetas de producto
- Nunca mostrar una pantalla completamente vacía durante la carga

**Spinners** (acciones puntuales):
- Dentro del botón de submit: reemplaza el texto del botón, no agrega un spinner encima
- El botón se deshabilita mientras el spinner está activo para evitar doble envío

**Estados vacíos**:
- `<EmptyState />` con ícono, título descriptivo y, cuando corresponde, un botón de acción (por ejemplo, "Crear primera categoría")
- Nunca una página en blanco o solo el texto "(vacío)"

---

## 10. Orden de Implementación Recomendado

> Los pasos 1-5 son **MVP** (mínimo viable para usar el sistema). Los pasos 6-8 son mejoras de calidad.

### MVP

**Paso 1 — Setup base** *(~2 horas)*
- Instalar dependencias: `npm install axios react-router-dom @tanstack/react-query react-hook-form zod tailwindcss sonner lucide-react`
- Instalar shadcn/ui y componentes base: `npx shadcn-ui@latest init`, agregar `Button`, `Input`, `Dialog`, `Select`, `Table`, `Toast`
- Crear `.env.local` con `VITE_API_BASE_URL=http://localhost:8080/api/v1`
- Copiar `docs/frontend-integration/types.ts` → `src/types/api.ts`
- Copiar `docs/frontend-integration/example-axios-service.ts` → `src/services/api.ts`
- Configurar `QueryClientProvider` en `App.tsx` y `Toaster` de sonner

**Paso 2 — Routing + layout base** *(~3 horas)*
- Crear `src/routes/AppRoutes.tsx` con todas las rutas del mapa (Sección 4)
- Crear `<PublicLayout />` con `<Header />`
- Crear `<AdminLayout />` con `<Header />` y `<AdminSidebar />`
- Crear `NotFoundPage.tsx`
- Verificar que la navegación entre rutas funciona (aunque las páginas estén vacías)

**Paso 3 — Autenticación** *(~4 horas)*
- Crear `src/context/AuthContext.tsx` con `isAuthenticated`, `login`, `logout`, `token`
- Crear `src/hooks/useAuth.ts` para consumir el context
- Crear `<ProtectedRoute />` y `<PublicRoute />`
- Crear `LoginPage.tsx` con formulario validado con Zod + llamada a `authService.login`
- Probar: login con `admin/admin123` → redirigir a `/admin/products`; acceder a `/admin` sin JWT → redirigir a `/login`

**Paso 4 — Catálogo público** *(~5 horas)*
- Crear `src/hooks/useProducts.ts` con `useProductList` y `useProductDetail`
- Crear `src/hooks/useCategories.ts` con `useCategoryList`
- Crear `<ProductCard />`
- Crear `ProductsListPage.tsx`: grid de productos, filtro de nombre con debounce, selector de categoría, paginación
- Crear `ProductDetailPage.tsx`: detalle del producto con SKU, precio, categoría y fecha
- Probar: catálogo accesible sin login, filtros en URL, paginación funciona

**Paso 5 — Admin de categorías** *(~4 horas)*
- Crear `<DataTable />` genérica
- Crear `<ConfirmDialog />`
- Crear `<FormField />`
- Crear `AdminCategoriesPage.tsx`: lista de categorías + eliminar con manejo de error 409
- Crear `CategoryFormPage.tsx` (crear y editar): validación Zod, manejo de error 409 en campo nombre
- Probar: crear, renombrar, intentar eliminar categoría con productos → ver error 409

### Mejoras de calidad *(nice-to-have)*

**Paso 6 — Admin de productos** *(~5 horas)*
- Crear `AdminProductsPage.tsx`: lista con filtros + eliminar
- Crear `ProductFormPage.tsx` (crear y editar): campos con validación Zod, aviso SKU inmutable, selector de categoría
- Probar flujo completo: crear categoría → crear producto → editar precio → eliminar

**Paso 7 — Pulido de UX** *(~3 horas)*
- Agregar `<Skeleton />` en todos los estados de carga
- Agregar `<EmptyState />` en todas las listas vacías
- Agregar `<BackendStatus />` con verificación de health al cargar la app
- Revisar responsive en móvil (especialmente el catálogo y los formularios)

**Paso 8 — Extras opcionales** *(variable)*
- Debounce en búsqueda del catálogo (si no se implementó en paso 4)
- Dark mode con Tailwind
- Búsqueda con highlight de término en los resultados
- Exportar catálogo a CSV (solo frontend, formateando los datos de `GET /products/all`)

---

## 11. Checklist de Aceptación

Marca cada ítem cuando esté funcionando en el navegador (no solo en los tests):

```
□ El usuario puede iniciar sesión con las credenciales de demo (admin / admin123).
□ El JWT se guarda en localStorage y se envía automáticamente en cada request protegida.
□ Las rutas /admin redirigen a /login si no hay JWT en localStorage.
□ Si el token expira, el interceptor lo detecta, borra el JWT y redirige a /login con toast "Tu sesión ha expirado".
□ El catálogo de productos es visible sin autenticación desde /catalog.
□ El filtro de nombre en el catálogo funciona con debounce y actualiza la URL.
□ El filtro de categoría en el catálogo actualiza la URL y puede combinarse con el filtro de nombre.
□ La paginación del catálogo muestra el número de página en la URL.
□ El botón "atrás" del navegador restaura los filtros y página anterior.
□ El detalle de un producto (/catalog/:id) muestra nombre, precio, SKU, categoría y fecha de alta.
□ El administrador puede ver la lista de categorías en /admin/categories.
□ El administrador puede crear una categoría con nombre único.
□ Si el nombre de categoría ya existe, el error 409 aparece junto al campo "nombre", no en toast.
□ El administrador puede editar el nombre y descripción de una categoría.
□ Al intentar eliminar una categoría con productos, el error 409 aparece dentro del ConfirmDialog.
□ El administrador puede eliminar una categoría vacía con éxito.
□ El administrador puede ver la lista de productos en /admin/products.
□ El administrador puede crear un producto con SKU, nombre, precio y categoría.
□ Si el SKU ya existe, el error 409 aparece junto al campo "SKU".
□ El campo SKU en el formulario de edición está deshabilitado y no se envía al backend.
□ El administrador puede actualizar nombre, precio y categoría de un producto.
□ El administrador puede eliminar un producto con confirmación previa.
□ Los errores 400 con fieldErrors muestran mensajes junto a cada campo inválido.
□ Los errores 401 (desde cualquier endpoint) redirigen a /login.
□ Los errores 403 muestran un toast informativo.
□ Los errores 404 muestran un EmptyState descriptivo (no la página 404 genérica).
□ Los errores 500 muestran un toast rojo genérico.
□ Todos los estados de carga inicial muestran skeletons (no pantalla en blanco).
□ Todas las listas vacías muestran EmptyState con mensaje descriptivo y CTA cuando corresponde.
□ El health check del backend se verifica al cargar la app; si falla, aparece el banner BackendStatus.
□ La aplicación es usable en mobile (formularios, tabla, catálogo adaptados a pantalla pequeña).
□ Los toasts de éxito desaparecen en 3 segundos; los de error persisten hasta que el usuario los cierre.
```

---

*Especificación generada a partir del análisis exhaustivo del backend `catalog-demo`. Toda pantalla, validación y manejo de error descritos aquí están respaldados por endpoints y DTOs reales del backend. No se inventó ningún endpoint ni regla de negocio.*
