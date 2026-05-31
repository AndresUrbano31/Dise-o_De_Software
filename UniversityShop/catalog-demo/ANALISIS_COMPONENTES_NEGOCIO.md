# ANÁLISIS DE COMPONENTES CON CONTEXTO DE NEGOCIO
## Proyecto `catalog-demo` — University Shop

**Stack:** Spring Boot 3.2.5 · Java 17 · Spring Data JPA · PostgreSQL / H2 · JWT · OpenAPI 3  
**Fecha:** 2026-05-29  
**Enfoque:** Justificación del tipo Java + Valor de negocio de cada componente

---

## TABLA DE CONTENIDO

1. [Comprensión del Problema](#1-comprensión-del-problema)
2. [Capas Explicadas desde el Negocio](#2-capas-explicadas-desde-el-negocio)
3. [Análisis de Componentes — Capa API](#3-análisis-de-componentes--capa-api)
4. [Análisis de Componentes — Capa Application (DTOs)](#4-análisis-de-componentes--capa-application-dtos)
5. [Análisis de Componentes — Capa Application (Puertos)](#5-análisis-de-componentes--capa-application-puertos)
6. [Análisis de Componentes — Capa Domain (Entidades)](#6-análisis-de-componentes--capa-domain-entidades)
7. [Análisis de Componentes — Capa Domain (Excepciones)](#7-análisis-de-componentes--capa-domain-excepciones)
8. [Análisis de Componentes — Capa Infrastructure](#8-análisis-de-componentes--capa-infrastructure)
9. [Clase Principal](#9-clase-principal)
10. [Archivos de Configuración](#10-archivos-de-configuración)
11. [Flujo Completo del Negocio](#11-flujo-completo-del-negocio)
12. [Conclusión Orientada al Problema](#12-conclusión-orientada-al-problema)

---

## 1. COMPRENSIÓN DEL PROBLEMA

### Problema Principal

La tienda universitaria (`university.shop`) necesita gestionar digitalmente su catálogo de productos de forma centralizada, organizada y segura. Sin este sistema, el inventario vive en hojas de cálculo o documentos dispersos, los precios se desactualizan, no existe un canal unificado para que los compradores consulten qué artículos hay disponibles, y cualquier persona podría modificar la información del catálogo sin control.

El proyecto `catalog-demo` resuelve exactamente ese problema: provee una **API REST** que centraliza la información de **productos** y **categorías**, protege las operaciones de modificación con **autenticación JWT** y expone la consulta del catálogo de forma **pública y paginada** para cualquier cliente (navegador, app móvil, sistema externo).

---

### Usuarios Involucrados

| Tipo de usuario       | ¿Quién es?                                             | ¿Qué necesita?                                                    |
|-----------------------|--------------------------------------------------------|-------------------------------------------------------------------|
| **Administrador**     | Personal de la tienda; responsable del inventario      | Crear, editar y eliminar productos y categorías de forma segura   |
| **Cliente/Estudiante**| Miembro de la comunidad universitaria                  | Consultar el catálogo, buscar productos, ver precios y categorías |
| **Integrador/Dev**    | Desarrollador que construye el frontend o app móvil    | API documentada con Swagger, contratos de datos claros (DTOs)     |

---

### Necesidad de Negocio

El proyecto fue creado para satisfacer una doble necesidad:

- **Operacional:** digitalizar y centralizar el catálogo de la tienda universitaria para que sea consultable en tiempo real desde cualquier dispositivo.
- **Académica:** demostrar principios de diseño de software (arquitectura hexagonal, SOLID, DDD, patrones de diseño) en un caso de uso concreto y comprensible.

---

### Beneficios

| Beneficio                                   | Mecanismo técnico que lo logra                                  |
|---------------------------------------------|------------------------------------------------------------------|
| Catálogo siempre actualizado                | CRUD en tiempo real via endpoints REST                           |
| Productos sin duplicados (SKU único)        | `SkuAlreadyExistsException` + `existsBySku()` antes de guardar  |
| Categorías organizadas sin repetición       | `CategoryNameAlreadyExistsException` + `existsByName()`          |
| Integridad referencial del catálogo         | `CategoryHasProductsException` protege borrado inconsistente     |
| Solo el admin modifica el catálogo          | JWT + `SecurityConfig` protege POST/PUT/DELETE                   |
| Navegación eficiente en catálogos grandes   | `PagedResponseDTO<T>` con `page`, `size`, `totalElements`        |
| Búsqueda integrada en nombre y categoría    | `findByNameContainingIgnoreCaseAndCategoryId()` en repositorio   |
| API autodocumentada                         | `OpenApiConfig` + Swagger UI en `/swagger-ui.html`               |
| Registro automático de fecha de alta        | `@PrePersist onPrePersist()` en entidad `Product`                |

---

### Flujo de Negocio

```
ADMINISTRADOR DE LA TIENDA
        │
        ▼
Se autentica con usuario y contraseña
(POST /api/v1/auth/login → AuthController → JwtService)
        │
        ▼
Recibe token JWT válido por 24 horas
        │
        ▼
Organiza el catálogo creando categorías
(POST /api/v1/categories → CategoryController → CategoryServiceImpl)
El sistema valida nombre único → guarda → devuelve CategoryResponseDTO
        │
        ▼
Registra productos asignándolos a categorías existentes
(POST /api/v1/products → ProductController → ProductServiceImpl)
El sistema valida SKU único + categoría existente → guarda con fecha automática
→ devuelve ProductResponseDTO con categoría embebida
        │
        ▼
EL CATÁLOGO ESTÁ DISPONIBLE PÚBLICAMENTE

CLIENTE / ESTUDIANTE
        │
        ▼
Consulta el catálogo sin autenticarse
(GET /api/v1/products?name=laptop&page=0&size=10)
        │
        ▼
Recibe lista paginada con productos, precios y categorías
(PagedResponseDTO<ProductResponseDTO>)
        │
        ▼
Consulta detalles de un producto específico
(GET /api/v1/products/{id} → ProductResponseDTO con CategoryResponseDTO embebido)
        │
        ▼
Toma una decisión de compra informada
```

---

## 2. CAPAS EXPLICADAS DESDE EL NEGOCIO

### API — `com.university.shop.api`

La capa API es **la ventana de la tienda hacia el mundo exterior**. Es el único punto de contacto entre los usuarios (clientes, administradores, desarrolladores) y el catálogo digital.

**Funcionalidad de negocio que expone:**

- **`AuthController`** — permite que el administrador se identifique ante el sistema. Sin autenticación, nadie podría gestionar el catálogo de forma segura.
- **`CategoryController`** — expone la gestión de secciones del catálogo (crear, leer, actualizar, eliminar categorías). Las categorías deben existir antes que los productos; son la estructura organizativa de la tienda.
- **`ProductController`** — es el canal principal del catálogo: permite a los clientes consultar y buscar productos, y al administrador autenticado crear, actualizar y retirar artículos.
- **`SecurityConfig`** — establece la política de acceso: la lectura del catálogo es libre para todos, pero la gestión solo es posible con token JWT válido. Representa la regla de negocio "cualquiera puede ver la vitrina, solo el personal puede tocar el inventario".
- **`OpenApiConfig`** — genera la documentación interactiva del catálogo de servicios. Permite que los integradores (desarrolladores de frontend o apps) entiendan la API sin leer el código fuente.
- **`GlobalExceptionHandler`** — traduce los errores de negocio en respuestas comprensibles. Cuando el administrador intenta registrar un SKU duplicado, el sistema no responde con un error técnico incomprensible: responde "El SKU ya está registrado en el catálogo" con código HTTP 409.

**Necesidad del usuario que atiende:** hacer accesible el catálogo desde cualquier tecnología cliente.

**Proceso de negocio que representa:** el mostrador de la tienda y la política de acceso del personal.

---

### APPLICATION — `com.university.shop.application`

La capa Application define **qué puede hacer el sistema** (puertos) y **cómo se intercambia la información** (DTOs). Es el manual de operaciones del catálogo.

**Casos de uso implementados a través de los puertos:**

- `ProductService` — 6 casos de uso: crear producto, listar todos, buscar con filtros y paginación, obtener por ID, actualizar y eliminar.
- `CategoryService` — 5 casos de uso: crear categoría, listar todas, obtener por ID, actualizar y eliminar.

**Los DTOs protegen el dominio del exterior:** definen exactamente qué datos entran y salen en cada operación de negocio, evitando que las entidades JPA con sus relaciones internas (referencias circulares, campos de Hibernate) lleguen directamente al cliente.

**Contribución al objetivo:** sin esta capa, los controllers dependerían directamente de implementaciones concretas (imposible testear, imposible cambiar la BD sin tocar la API) y el dominio quedaría expuesto al mundo exterior.

---

### DOMAIN — `com.university.shop.domain`

El dominio es **la representación digital del negocio real de la tienda**. Aquí viven los objetos que existen en el mundo real y las reglas que los gobiernan.

**Entidades del mundo real:**

- **`Product`** — un artículo físico en el inventario de la tienda. Tiene un código de barras digital (SKU), nombre, precio, fecha de ingreso al inventario y pertenece a una sección del catálogo (Category).
- **`Category`** — una sección del catálogo (ej. "Papelería", "Tecnología"). Tiene nombre único y agrupa múltiples productos.

**Reglas de negocio implementadas por las excepciones:**

- No puede haber dos artículos con el mismo código en el inventario → `SkuAlreadyExistsException`
- No pueden existir dos secciones con el mismo nombre → `CategoryNameAlreadyExistsException`
- No se puede retirar una sección que aún tiene artículos activos → `CategoryHasProductsException`
- Si un artículo no existe, el sistema lo comunica con claridad → `ProductNotFoundException`
- Si una sección no existe, el sistema lo comunica → `CategoryNotFoundException`

---

### INFRASTRUCTURE — `com.university.shop.infrastructure`

La infraestructura son **los cimientos técnicos que hacen que el negocio funcione en la práctica**: la base de datos donde se persiste el catálogo, la seguridad que protege las operaciones y la lógica que conecta el dominio con la tecnología.

- **`CategoryServiceImpl` / `ProductServiceImpl`** — materializan los casos de uso del negocio: coordinan validaciones, repositorios y mapeos para que las operaciones del catálogo funcionen correctamente.
- **`CategoryRepository` / `ProductRepository`** — garantizan que el catálogo persiste entre reinicios del servidor. Sin ellos, los datos vivirían solo en memoria.
- **`JwtService` / `JwtAuthenticationFilter`** — garantizan que solo el administrador autenticado puede modificar el catálogo, validando el "pase de acceso" en cada petición.

---

## 3. ANÁLISIS DE COMPONENTES — CAPA API

---

### OpenApiConfig

**Ruta:** `com.university.shop.api.config.OpenApiConfig`

#### A) Justificación del Tipo

**Tipo Java:** `class` con anotaciones `@Configuration`, `@OpenAPIDefinition` e `@SecurityScheme`.

**¿Por qué `class` y no `interface`?**
Porque necesita ser detectada por el contenedor de Spring como un bean de configuración. Una interfaz no puede instanciarse ni registrarse directamente como bean Spring. Al ser `@Configuration`, Spring la instancia una sola vez (patrón Singleton) y aplica los metadatos de las anotaciones de clase al contexto de la aplicación.

**¿Por qué `@Configuration` y no `@Component`?**
`@Configuration` comunica explícitamente la intención: esta clase no contiene lógica de negocio, solo definiciones de infraestructura transversal. Además, `@Configuration` garantiza que los `@Bean` declarados dentro sean proxied correctamente por Spring (aunque aquí no hay métodos `@Bean` — toda la configuración es vía anotaciones de clase de Springdoc).

**¿Por qué no tiene métodos?**
Porque toda la configuración se declara mediante anotaciones de nivel de clase (`@OpenAPIDefinition`, `@SecurityScheme`). Springdoc OpenAPI 2.x lee estas anotaciones automáticamente sin necesitar métodos `@Bean` adicionales. Esto es una ventaja de usar anotaciones de metadatos: el código de configuración queda en cero líneas ejecutables.

**Patrón de diseño:** Configuración declarativa (Annotation-Based Configuration). La clase actúa como Facade de configuración de Swagger UI.

**¿Qué pasaría si se cambiara su tipo?**
Si fuera una interfaz, Spring no podría instanciarla ni leer sus anotaciones de clase como bean. Si se eliminara la anotación `@Configuration`, Spring podría no detectarla en el component scan. El resultado sería que Swagger UI no tendría título, descripción ni el botón de autorización JWT.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque el catálogo digital necesita ser consumido por frontends, apps móviles o integradores externos, y esos consumidores necesitan entender la API sin leer el código fuente. `OpenApiConfig` genera automáticamente el portal de documentación interactiva en `/swagger-ui.html`.

**¿Qué necesidad del usuario satisface?**
- *Desarrollador de frontend:* puede explorar todos los endpoints, probarlos en el navegador, ver qué campos acepta cada uno y qué responde — sin Postman, sin leer código.
- *Administrador técnico:* puede usar Swagger UI para probar el login y las operaciones del catálogo directamente desde el navegador.
- *Integrador:* tiene el esquema OpenAPI disponible en `/v3/api-docs` para generar clientes automáticamente.

**¿Qué ocurriría si no existiera?**
La API seguiría funcionando, pero nadie sabría exactamente cómo usarla sin leer el código fuente. El botón `[Authorize]` en Swagger UI (que permite probar endpoints protegidos con JWT) desaparecería.

**¿Cómo contribuye al objetivo?**
Un catálogo digital sin documentación es difícil de integrar. `OpenApiConfig` reduce el tiempo de integración de días a minutos.

---

### SecurityConfig

**Ruta:** `com.university.shop.api.config.SecurityConfig`

#### A) Justificación del Tipo

**Tipo Java:** `class` con anotaciones `@Configuration` y `@EnableWebSecurity`.

**¿Por qué `class` y no `interface`?**
Porque necesita contener métodos `@Bean` con lógica concreta: la definición de la cadena de filtros de seguridad (`SecurityFilterChain`), el gestor de autenticación, el usuario de demo y el codificador de contraseñas. Una interfaz no puede tener métodos con cuerpo que Spring interprete como beans (antes de Java 8; incluso con default methods, Spring no las procesaría como `@Bean`).

**¿Por qué `@Configuration` + `@EnableWebSecurity`?**
- `@Configuration` → registra los `@Bean` declarados en esta clase en el contenedor Spring.
- `@EnableWebSecurity` → activa toda la infraestructura de Spring Security: interceptores, contexto de seguridad, manejo de sesiones. Sin esta anotación, las reglas de acceso definidas serían ignoradas.

**¿Por qué no `@Service` o `@Component`?**
`@Service` y `@Component` son para beans de lógica de negocio o componentes genéricos. No habilitan la semántica de configuración de `@Bean` ni activan la infraestructura de seguridad. `@Configuration` es el estereotipo específico para clases que registran beans del contexto de aplicación.

**Patrón de diseño:** Chain of Responsibility (cadena de filtros de seguridad) + Factory Method (los métodos `@Bean` actúan como factories registradas en el contenedor).

**¿Qué pasaría si se eliminara?**
Spring Security aplicaría su configuración por defecto: todos los endpoints estarían protegidos con HTTP Basic Auth generado automáticamente. El catálogo no sería públicamente accesible y el login JWT no funcionaría.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque la tienda necesita una política de acceso clara: el catálogo es público para consulta, pero la gestión del inventario es exclusiva del personal autorizado. `SecurityConfig` implementa exactamente esa política en código.

**¿Qué necesidad del usuario satisface?**
- *Cliente:* accede al catálogo sin barreras — `GET /api/v1/products/**` y `GET /api/v1/categories/**` son `permitAll()`.
- *Administrador:* sus operaciones de escritura (POST/PUT/DELETE) están protegidas y requieren un JWT válido.
- *Tienda:* garantiza que ningún actor no autorizado puede modificar precios, crear productos falsos o eliminar categorías.

**¿Qué ocurriría si no existiera?**
Cualquiera podría crear, editar o eliminar productos y categorías. El catálogo estaría completamente expuesto a modificaciones maliciosas o accidentales.

**¿Cómo contribuye al objetivo?**
Protege la integridad del catálogo. Es la diferencia entre una tienda con inventario controlado y una donde cualquiera puede cambiar los precios.

---

### AuthController

**Ruta:** `com.university.shop.api.controller.AuthController`

#### A) Justificación del Tipo

**Tipo Java:** `class` con anotaciones `@RestController` y `@RequestMapping("/api/v1/auth")`.

**¿Por qué `class` y no `interface`?**
Porque necesita implementar lógica concreta en el método `login()`: recibir el `@RequestBody`, invocar `AuthenticationManager.authenticate()`, capturar excepciones y construir el `ResponseEntity`. Una interfaz no puede ejecutar lógica.

**¿Por qué `@RestController` y no `@Controller`?**
`@RestController` es un estereotipo compuesto que combina `@Controller` (registra el bean como manejador de peticiones MVC) + `@ResponseBody` (serializa automáticamente el valor de retorno a JSON sin necesidad de `@ResponseBody` en cada método). En una API REST que siempre retorna JSON, `@RestController` elimina repetición.

**¿Por qué no `@Service`?**
`@Service` es para lógica de negocio, no para adaptadores HTTP. Un controller es un adaptador de entrada (HTTP → Application) — su responsabilidad es la traducción de protocolo, no la lógica del dominio.

**Patrón de diseño:** Adapter (convierte el protocolo HTTP en llamadas al dominio de aplicación). La clase `AuthController` es el adaptador de entrada para el caso de uso de autenticación.

**¿Qué pasaría si fuera una interfaz con implementación?**
Si se creara una interfaz `AuthController` con una implementación, se agregaría complejidad innecesaria. Los controllers rara vez tienen múltiples implementaciones intercambiables; el patrón de interfaz tiene más sentido en los puertos de la capa de aplicación.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque la tienda necesita un mecanismo para que el administrador obtenga su "pase de acceso" (token JWT) antes de poder modificar el catálogo. Sin autenticación, no habría forma de distinguir al administrador de cualquier usuario anónimo.

**¿Qué necesidad del usuario satisface?**
- *Administrador:* envía sus credenciales una vez, recibe un JWT válido 24 horas, y lo usa en todas sus operaciones de gestión sin volver a autenticarse.

**¿Qué ocurriría si no existiera?**
No habría forma de obtener el token JWT. Los endpoints protegidos (crear/editar/eliminar productos y categorías) serían inaccessibles para todos, incluido el administrador.

**¿Cómo contribuye al objetivo?**
Es la puerta de entrada al área administrativa. Sin él, la seguridad del catálogo es inoperable.

---

### CategoryController

**Ruta:** `com.university.shop.api.controller.CategoryController`

#### A) Justificación del Tipo

**Tipo Java:** `class` con `@RestController` y `@RequestMapping("/api/v1/categories")`.

**¿Por qué `class`?**
Requiere lógica concreta en cada método handler: recibir parámetros HTTP, invocar el servicio, construir `ResponseEntity` con el código de estado correcto (201 para creación, 204 para eliminación, 200 para lecturas).

**¿Por qué `@RestController` y no `@Controller` + `@ResponseBody`?**
`@RestController` es la combinación directa de ambos en un solo estereotipo. Para APIs REST que siempre devuelven JSON, es la forma idiomática en Spring MVC.

**Inyección por constructor:** la dependencia `CategoryService` (interfaz, no implementación) se inyecta por constructor — aplica **Inversión de Dependencias (DIP)**. El controller no sabe ni le importa si está hablando con `CategoryServiceImpl` u otra implementación.

**Patrón:** Adapter de entrada (HTTP → puerto `CategoryService`).

**¿Qué pasaría si se inyectara `CategoryServiceImpl` directamente?**
Se violaría DIP. El controller quedaría acoplado a la implementación concreta: si se crea una segunda implementación (ej. `CachedCategoryServiceImpl`), el controller tendría que modificarse.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque la tienda necesita gestionar sus secciones (categorías) de forma centralizada. Las categorías son la estructura organizativa del catálogo: un producto no puede existir sin pertenecer a una categoría.

**¿Qué necesidad del usuario satisface?**
- *Administrador:* puede crear secciones como "Papelería", "Tecnología" o "Libros de Texto", editarlas y eliminar las que ya no use (solo si están vacías).
- *Cliente:* puede consultar qué secciones tiene la tienda y navegar el catálogo organizado.

**¿Qué ocurriría si no existiera?**
Los productos no podrían clasificarse. El catálogo sería una lista plana sin organización, y la regla de negocio "todo producto debe tener una categoría" sería imposible de implementar.

**Endpoints y su significado de negocio:**

| Método | Ruta                        | Acceso  | Operación de negocio                              |
|--------|-----------------------------|---------|---------------------------------------------------|
| POST   | `/api/v1/categories`        | Admin   | Crear una nueva sección en el catálogo            |
| GET    | `/api/v1/categories`        | Público | Listar todas las secciones disponibles            |
| GET    | `/api/v1/categories/{id}`   | Público | Ver detalles de una sección específica            |
| PUT    | `/api/v1/categories/{id}`   | Admin   | Renombrar o actualizar descripción de una sección |
| DELETE | `/api/v1/categories/{id}`   | Admin   | Retirar una sección vacía del catálogo            |

---

### ProductController

**Ruta:** `com.university.shop.api.controller.ProductController`

#### A) Justificación del Tipo

**Tipo Java:** `class` con `@RestController` y `@RequestMapping("/api/v1/products")`.

**¿Por qué `class`?**
Porque cada endpoint requiere lógica de adaptación concreta: extraer `@RequestParam`, `@PathVariable`, `@RequestBody`, construir `PagedResponseDTO` o `ResponseEntity` con el status HTTP apropiado.

**¿Por qué `@RestController`?**
Mismo razonamiento que en `CategoryController`: API REST que siempre retorna JSON. `@RestController` = `@Controller` + `@ResponseBody` aplicado globalmente a la clase.

**Inyección de `ProductService` (interfaz):** aplica DIP. El controller no conoce `ProductServiceImpl`; solo conoce el contrato definido por la interfaz-puerto.

**Patrón:** Adapter de entrada (HTTP → puerto `ProductService`). Cada método del controller mapea un verbo HTTP a un caso de uso del negocio.

**Anotaciones OpenAPI (`@Operation`, `@ApiResponse`, `@Parameter`):** documentan el contrato de negocio directamente en el código. Son decoradores que enriquecen la Swagger UI sin afectar la lógica de ejecución.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque los productos son el núcleo del catálogo. Sin `ProductController`, ni los clientes podrían ver el inventario ni el administrador podría gestionarlo.

**¿Qué necesidad del usuario satisface?**
- *Cliente:* puede navegar el catálogo paginado, buscar por nombre o categoría, y ver los detalles de un artículo específico incluyendo su precio y sección.
- *Administrador:* puede registrar nuevos artículos, actualizar precios y retirar productos descontinuados.

**¿Qué ocurriría si no existiera?**
El catálogo existiría en la base de datos pero nadie podría acceder a él desde el exterior. Es la ventana principal de la tienda.

**Endpoints y su significado de negocio:**

| Método | Ruta                        | Acceso  | Operación de negocio                                        |
|--------|-----------------------------|---------|-------------------------------------------------------------|
| GET    | `/api/v1/products`          | Público | Explorar el catálogo con filtros de nombre/categoría y paginación |
| GET    | `/api/v1/products/all`      | Público | Obtener todos los productos (para dropdowns en frontend)    |
| GET    | `/api/v1/products/{id}`     | Público | Ver todos los detalles de un artículo específico            |
| POST   | `/api/v1/products`          | Admin   | Registrar un nuevo artículo en el inventario                |
| PUT    | `/api/v1/products/{id}`     | Admin   | Actualizar nombre, precio o categoría de un artículo        |
| DELETE | `/api/v1/products/{id}`     | Admin   | Retirar un artículo del catálogo                            |

---

### GlobalExceptionHandler

**Ruta:** `com.university.shop.api.exception.GlobalExceptionHandler`

#### A) Justificación del Tipo

**Tipo Java:** `class` con `@RestControllerAdvice`.

**¿Por qué `class` y no `interface`?**
Porque contiene métodos `@ExceptionHandler` con lógica concreta: construir mapas de error con timestamp, status y mensaje, y retornar `ResponseEntity` con el código HTTP apropiado.

**¿Por qué `@RestControllerAdvice` y no `@ControllerAdvice`?**
`@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody` aplicado globalmente. Como todos los manejadores retornan JSON (no vistas HTML), es la anotación correcta para APIs REST.

**¿Por qué no manejar las excepciones en cada controller individualmente?**
Hacerlo en cada controller violaría DRY (Don't Repeat Yourself) y SRP: los controllers se volverían responsables tanto del flujo normal como del manejo de todos los errores posibles. `@RestControllerAdvice` centraliza el manejo de errores en una sola clase con una sola responsabilidad.

**Patrón de diseño:** Interceptor / Anti-Corruption Layer. Intercepta excepciones de dominio (`SkuAlreadyExistsException`, `CategoryNotFoundException`, etc.) antes de que lleguen al cliente, y las traduce a un formato HTTP estandarizado y comprensible. Aplica el principio de **separación de preocupaciones**: el dominio lanza sus propias excepciones semánticas; la API las traduce a HTTP.

**¿Qué pasaría si se cambiara por `@ControllerAdvice` sin `@ResponseBody`?**
Los manejadores de excepciones tratarían de buscar una vista (template HTML) para renderizar el error en lugar de retornar JSON. Todos los errores de la API devuelverían `406 Not Acceptable` o un error de resolución de vista.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque los errores en un sistema son inevitables: el administrador puede intentar crear un producto con SKU duplicado, buscar un producto inexistente o enviar datos incompletos. Sin `GlobalExceptionHandler`, el sistema respondería con stack traces de Java — información técnica inútil para el cliente y peligrosa desde el punto de vista de seguridad.

**¿Qué necesidad del usuario satisface?**
- *Administrador:* recibe mensajes claros de negocio: "El SKU 'PROD-001' ya está registrado en el catálogo", no `HibernateConstraintViolationException: ...`.
- *Cliente:* recibe "El producto con ID 99 no fue encontrado" (404), no un error de servidor genérico.
- *Desarrollador de frontend:* recibe JSON consistente con `timestamp`, `status`, `error` y `message` — puede mostrar errores al usuario sin parseado especial.

**¿Qué ocurriría si no existiera?**
Las excepciones de dominio llegarían sin capturar hasta el framework, que respondería con `500 Internal Server Error` y posiblemente el stack trace completo. Esto representa una fuga de información interna y una experiencia de usuario terrible.

**Mapeo excepción → respuesta de negocio:**

| Excepción de dominio lanzada              | Respuesta HTTP     | Mensaje de negocio                                        |
|-------------------------------------------|--------------------|-----------------------------------------------------------|
| `SkuAlreadyExistsException`               | 409 Conflict       | "El SKU 'X' ya está registrado en el catálogo"            |
| `CategoryNameAlreadyExistsException`      | 409 Conflict       | "Ya existe una categoría con el nombre 'X'"               |
| `CategoryHasProductsException`            | 409 Conflict       | "La categoría tiene productos asociados y no puede eliminarse" |
| `DataIntegrityViolationException`         | 409 Conflict       | "Violación de integridad de datos"                        |
| `CategoryNotFoundException`               | 404 Not Found      | "La categoría con ID X no fue encontrada"                 |
| `ProductNotFoundException`                | 404 Not Found      | "El producto con ID X no fue encontrado"                  |
| `BadCredentialsException`                 | 401 Unauthorized   | "Credenciales incorrectas"                                |
| `MethodArgumentNotValidException`         | 400 Bad Request    | "Validación fallida" + lista de campos erróneos           |
| `Exception` (genérica)                    | 500 Internal Error | "Error interno del servidor" (sin detalles)               |

---

## 4. ANÁLISIS DE COMPONENTES — CAPA APPLICATION (DTOs)

---

### AuthRequestDTO

**Ruta:** `com.university.shop.application.dto.AuthRequestDTO`

#### A) Justificación del Tipo

**Tipo Java:** `class` estándar (no `record`) con anotaciones de validación Jakarta.

**¿Por qué `class` y no `record` (Java 16+)?**
Los `record` en Java son inmutables por diseño: sus campos son `final` y se inicializan solo por el constructor canónico. El problema con los records para DTOs de entrada en Spring MVC es que Jackson (el serializador JSON) los deserializa por defecto usando el constructor canónico, lo que requiere que todos los campos estén presentes en el JSON. Para flexibilidad de deserialización (ej. campos opcionales, `null` por defecto), la clase con constructor vacío + setters es más compatible con Jackson sin configuración adicional.

Adicionalmente, las anotaciones de validación Bean Validation (`@NotBlank`) funcionan en ambos tipos, pero los frameworks de validación y serialización tienen más madurez y compatibilidad con clases regulares que con records en el ecosistema Spring Boot.

**¿Por qué no una entidad de dominio?**
Si el controller recibiera directamente una entidad `User` del dominio (hipotética), se expondrían campos internos (id de base de datos, roles, fecha de creación) que no deben ser enviados por el cliente. El DTO filtra solo los campos relevantes para la operación de login.

**¿Por qué `@NotBlank` y no `@NotNull`?**
`@NotNull` solo verifica que el campo no sea `null`. `@NotBlank` verifica que no sea null, no sea vacío (`""`) y no sea solo espacios (`"   "`). Para credenciales de usuario, `@NotBlank` es la validación correcta.

**Patrón de diseño:** DTO (Data Transfer Object) — transporta datos del cliente a la capa de aplicación sin exponer el modelo interno.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque el sistema necesita recibir las credenciales del administrador de forma estructurada, validada y segura.

**¿Qué necesidad satisface?**
Define exactamente qué datos debe enviar el administrador para autenticarse: `username` y `password`, ambos obligatorios.

**¿Qué ocurriría si no existiera?**
El controller recibiría los datos como un `Map<String, String>` sin tipo ni validación, o peor: la entidad `User` con campos innecesarios. Cualquier dato podría llegar sin validación previa.

---

### AuthResponseDTO

**Ruta:** `com.university.shop.application.dto.AuthResponseDTO`

#### A) Justificación del Tipo

**Tipo Java:** `class` con campos `final` (efectivamente inmutable, aunque no es un `record`).

**¿Por qué `class` con `final` y no `record`?**
Los campos `type = "Bearer"` y `message` son constantes de negocio que siempre tienen el mismo valor, independientemente del token. Podrían ser `static final` en la clase. La elección de una clase con constructor que solo recibe `token` permite inyectar el token generado mientras los otros campos se fijan en tiempo de compilación.

**¿Por qué no devolver solo el `String token` directamente?**
Devolver el objeto `AuthResponseDTO` permite agregar en el futuro campos adicionales (ej. `expiresIn`, `refreshToken`, `roles`) sin cambiar el contrato del endpoint. La respuesta JSON es más autodescriptiva para el cliente al incluir `type: "Bearer"` y el mensaje de instrucciones.

**Patrón de diseño:** DTO de salida con datos de respuesta inmutables.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque cuando el administrador se autentica exitosamente, necesita recibir no solo el token sino también instrucciones de cómo usarlo. El `message` embebido en `AuthResponseDTO` explica directamente al consumidor que debe incluir el token en el header `Authorization: Bearer`.

**¿Qué ocurriría si no existiera?**
El endpoint devolvería directamente el string del token sin contexto. Los integradores que nunca han visto la API tendrían que consultar la documentación por separado para entender cómo autenticarse en las siguientes peticiones.

---

### CategoryRequestDTO

**Ruta:** `com.university.shop.application.dto.CategoryRequestDTO`

#### A) Justificación del Tipo

**Tipo Java:** `class` con getters/setters y anotaciones Bean Validation.

**¿Por qué `class` con setters y no `record`?**
La deserialización de JSON por Jackson requiere, por defecto, un constructor sin argumentos + setters públicos para clases regulares. Usar `record` con Jackson requiere configuración adicional (`@JsonDeserialize` o la dependencia `jackson-module-parameter-names`). Para compatibilidad directa con Spring MVC + Jackson sin configuración extra, la clase con setters es la elección pragmática en este proyecto.

**¿Por qué `@NotBlank` en `name` pero no en `description`?**
Porque `name` es el identificador único de negocio de la categoría (obligatorio para distinguirla de otras), mientras que `description` es una información opcional de enriquecimiento. Esta distinción refleja la regla de negocio: una categoría puede no tener descripción, pero siempre debe tener nombre.

**¿Por qué `@Size(max=100)` en nombre y `@Size(max=500)` en descripción?**
Refleja los límites de columna definidos en la entidad `Category` (`length = 100` y `length = 500`). Validar en el DTO antes de llegar a la BD evita excepciones de truncado de Hibernate menos descriptivas.

**Patrón de diseño:** DTO de entrada con validaciones de contrato de API.

#### B) Valor para el Negocio

**¿Por qué existe?**
Define el formulario que debe completar el administrador cuando crea o actualiza una categoría. Garantiza que el nombre de la categoría sea válido antes de siquiera intentar guardarla.

**¿Qué ocurriría si no existiera?**
El controller recibiría un objeto genérico o la entidad `Category` directamente, exponiendo el `id` (asignado por la BD, no por el cliente) y sin validaciones automáticas. El administrador podría enviar categorías con nombre vacío o excediendo los límites de la BD.

---

### CategoryResponseDTO

**Ruta:** `com.university.shop.application.dto.CategoryResponseDTO`

#### A) Justificación del Tipo

**Tipo Java:** `class` con constructor completo y solo getters (sin setters públicos para los campos de negocio).

**¿Por qué sin setters para `id`, `name`, `description`?**
El comentario en el código documenta que es un "DTO de salida de solo lectura". Al no exponer setters, se evita que el código cliente interno (por error) mute el DTO después de construirlo. No es inmutabilidad completa (no hay `final`), pero es suficientemente seguro para el alcance del proyecto.

**¿Por qué no devolver directamente la entidad `Category`?**
Si se retornara la entidad JPA, Jackson intentaría serializar campos de Hibernate como el `@OneToMany` (si existiera) causando bucles infinitos en la serialización, además de exponer detalles de persistencia al cliente.

**¿Por qué se embebe dentro de `ProductResponseDTO`?**
Para que una sola petición `GET /api/v1/products/{id}` devuelva la información completa del producto incluyendo su categoría, sin requerir una segunda petición al cliente. Esto es el patrón **Embedded DTO**, que mejora el rendimiento desde la perspectiva del cliente (reduce round-trips HTTP).

**Patrón de diseño:** DTO de salida / Embedded DTO.

#### B) Valor para el Negocio

**¿Por qué existe?**
Para exponer la información de una categoría al cliente en el formato correcto, sin detalles técnicos de JPA. Es lo que el cliente ve cuando consulta la sección de un producto.

**¿Qué ocurriría si no existiera?**
La entidad `Category` con sus anotaciones JPA se serializaría directamente, exponiendo detalles de persistencia y posiblemente causando errores de serialización con relaciones lazy no inicializadas.

---

### PagedResponseDTO\<T\>

**Ruta:** `com.university.shop.application.dto.PagedResponseDTO`

#### A) Justificación del Tipo

**Tipo Java:** `class` genérica `PagedResponseDTO<T>` con constructor completo y solo getters.

**¿Por qué genérica (`<T>`) y no específica para productos?**
La paginación es un mecanismo transversal: cualquier colección puede necesitar paginar. Al ser genérica, la misma clase puede envolver `List<ProductResponseDTO>`, `List<CategoryResponseDTO>` o cualquier otro tipo sin duplicar código. Es el principio **DRY** (Don't Repeat Yourself) aplicado al diseño de DTOs.

**¿Por qué `class` y no `record`?**
Los records genéricos (`record PagedResponseDTO<T>(List<T> content, ...)`) son válidos en Java 16+, pero la clase regular tiene mayor compatibilidad con herramientas de documentación (Springdoc/OpenAPI) que necesitan inferir el tipo genérico para generar el schema correcto en Swagger UI.

**¿Por qué incluir `totalElements`, `totalPages` y `last`?**
Porque el cliente (frontend) necesita esta información para renderizar los controles de paginación: saber cuántas páginas hay, si está en la última, cuántos elementos totales existen. Sin estos metadatos, la paginación sería ciega.

**Patrón de diseño:** DTO genérico de paginación. Adapta el objeto `Page<T>` de Spring Data (que incluye metadatos de infraestructura) a un DTO limpio de aplicación.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque un catálogo real puede tener cientos o miles de productos. Devolver todos en una sola respuesta sobrecargaría el servidor, la red y el cliente. La paginación divide el catálogo en "páginas" manejables.

**¿Qué necesidad satisface?**
- *Cliente:* recibe una página del catálogo (ej. 10 productos por vez) con información de navegación para ir a la página siguiente o anterior.
- *Frontend:* puede renderizar controles de paginación usando `totalPages` y `last`.

**¿Qué ocurriría si no existiera?**
`GET /api/v1/products` devolvería todos los productos de una vez. Con un catálogo grande, esto causaría timeouts, alto consumo de memoria y mala experiencia de usuario.

---

### ProductRequestDTO

**Ruta:** `com.university.shop.application.dto.ProductRequestDTO`

#### A) Justificación del Tipo

**Tipo Java:** `class` con getters/setters y validaciones Bean Validation exhaustivas.

**¿Por qué no incluye `dateCreated` ni `id`?**
Son campos que el sistema asigna automáticamente, no el cliente. `id` lo genera la base de datos (AUTO_INCREMENT). `dateCreated` se asigna en el callback `@PrePersist` de la entidad `Product`. Incluirlos en el DTO de entrada permitiría al cliente manipular datos que son responsabilidad del sistema.

**¿Por qué `@DecimalMin("0.01")` y `@Digits(integer=10, fraction=2)`?**
`@DecimalMin("0.01")` garantiza que el precio sea positivo (no puede registrarse un artículo gratis o con precio negativo — regla de negocio). `@Digits` limita la precisión a 10 enteros y 2 decimales, coincidiendo con el campo `price DECIMAL(10,2)` en la base de datos. Validar antes de llegar a Hibernate evita excepciones de conversión de datos menos descriptivas.

**¿Por qué `@NotNull` en `categoryId` y no `@NotBlank`?**
`categoryId` es `Long`, no `String`. `@NotBlank` es exclusivo de cadenas. `@NotNull` verifica que el ID de categoría fue proporcionado (no puede enviarse `"categoryId": null`).

**Patrón de diseño:** DTO de entrada con contrato de validación estricto.

#### B) Valor para el Negocio

**¿Por qué existe?**
Define el formulario oficial para registrar un nuevo artículo en el catálogo. Garantiza que toda la información necesaria (SKU, nombre, precio, categoría) esté presente y sea válida antes de intentar guardar.

**¿Qué ocurriría si no existiera?**
El administrador podría registrar productos sin precio, con precios negativos, sin categoría asignada o con nombres vacíos. El catálogo se poblaría de datos inconsistentes.

---

### ProductResponseDTO

**Ruta:** `com.university.shop.application.dto.ProductResponseDTO`

#### A) Justificación del Tipo

**Tipo Java:** `class` con constructor completo, solo getters y `CategoryResponseDTO` embebido.

**¿Por qué embeber `CategoryResponseDTO` en lugar de solo el `categoryId`?**
Si se devolviera solo el `categoryId`, el cliente necesitaría hacer una segunda petición `GET /api/v1/categories/{categoryId}` para obtener el nombre de la categoría — dos round-trips HTTP para mostrar un producto. Al embeber el `CategoryResponseDTO` completo, la respuesta es auto-suficiente: en una sola petición el cliente tiene toda la información para renderizar el producto con su categoría.

**¿Por qué incluir `dateCreated`?**
Es información de negocio relevante para el cliente: indica cuándo el artículo fue incorporado al catálogo, útil para mostrar "Nuevos productos" o auditoría básica.

**Patrón de diseño:** DTO de salida con Embedded DTO (patrón de composición de respuestas).

#### B) Valor para el Negocio

**¿Por qué existe?**
Define exactamente qué información recibe el cliente cuando consulta un producto. Contiene todo lo necesario para presentar un artículo del catálogo: código de referencia, nombre, precio, fecha de ingreso y su sección.

**¿Qué ocurriría si no existiera?**
La entidad `Product` (con sus anotaciones JPA y la relación `@ManyToOne` EAGER) se serializaría directamente, exponiendo detalles de persistencia e incluyendo potencialmente más información de la que el cliente necesita.

---

### ProductUpdateDTO

**Ruta:** `com.university.shop.application.dto.ProductUpdateDTO`

#### A) Justificación del Tipo

**Tipo Java:** `class` con getters/setters. Contiene `name`, `price` y `categoryId`, pero **no `sku`**.

**¿Por qué no incluir `sku` si `ProductRequestDTO` sí lo tiene?**
Esta es la decisión de negocio más importante de este DTO: **el SKU es el identificador comercial inmutable del producto**. El comentario en el código lo explica: cambiar el SKU rompería integraciones con sistemas externos (ERP, facturas, sistemas de logística) que usan el SKU como identificador. Al tener un DTO de actualización separado que excluye el SKU, el sistema hace imposible (por diseño) actualizar este campo crítico.

**¿Por qué un DTO separado de `ProductRequestDTO`?**
Porque las operaciones de creación y actualización tienen diferentes contratos. En creación se requiere el SKU; en actualización no. Reutilizar el mismo DTO obligaría a hacer el SKU opcional en creación (violando la regla de negocio) o incluirlo en actualización (permitiendo su modificación, lo cual es incorrecto).

**Patrón de diseño:** DTO de actualización parcial (Command Object) separado del DTO de creación — aplica el principio Interface Segregation (ISP).

#### B) Valor para el Negocio

**¿Por qué existe?**
Para permitir que el administrador actualice nombre, precio y categoría de un artículo sin riesgo de modificar accidentalmente su código de referencia (SKU), que es el identificador usado por otros sistemas.

**¿Qué ocurriría si no existiera y se reutilizara `ProductRequestDTO` para actualizaciones?**
El SKU podría ser modificado desde el frontend (con o sin intención). Cualquier sistema externo que referencie el artículo por SKU (un ERP, facturas, etc.) perdería la referencia, generando inconsistencias de negocio difíciles de detectar.

---

## 5. ANÁLISIS DE COMPONENTES — CAPA APPLICATION (PUERTOS)

---

### CategoryService

**Ruta:** `com.university.shop.application.port.CategoryService`

#### A) Justificación del Tipo

**Tipo Java:** `interface` (puerto de entrada en arquitectura hexagonal).

**¿Por qué `interface` y no `abstract class` o clase concreta?**
Porque en arquitectura hexagonal, los **puertos de entrada** son contratos, no implementaciones. Al ser una interfaz:
1. **Inversión de Dependencias (DIP):** `CategoryController` depende de `CategoryService` (abstracción), no de `CategoryServiceImpl` (implementación concreta). Si mañana se crea `CachedCategoryServiceImpl`, el controller no necesita cambiar.
2. **Testabilidad:** En pruebas unitarias del controller, se puede crear un mock o stub de `CategoryService` sin necesidad de la implementación real (que requiere base de datos).
3. **Múltiples implementaciones:** La misma interfaz podría tener una implementación real (`CategoryServiceImpl`) y una de prueba (`InMemoryCategoryService`) sin modificar los consumers.

**¿Por qué no `abstract class`?**
Una clase abstracta puede tener estado y métodos concretos, lo que introduciría lógica en el contrato. Un puerto hexagonal debe ser solo un contrato puro sin implementación.

**¿Qué declara la interfaz?**
Los **5 casos de uso del negocio** de categorías, con Javadoc que documenta las excepciones de dominio que pueden lanzarse:
- `createCategory(CategoryRequestDTO)` → crea la sección
- `getAllCategories()` → lista todas las secciones
- `getCategoryById(Long)` → obtiene una sección por ID
- `updateCategory(Long, CategoryRequestDTO)` → actualiza una sección
- `deleteCategory(Long)` → elimina una sección vacía

**Patrón de diseño:** Port (puerto de entrada, Hexagonal Architecture) + Strategy (cualquier implementación puede sustituirse). Aplica DIP de SOLID.

**¿Qué pasaría si `CategoryService` fuera una clase concreta?**
`CategoryController` dependería de la clase concreta. Si se quisiera cambiar la implementación (ej. agregar caché), se tendría que modificar el controller. Probar el controller requeriría instanciar toda la cadena de dependencias hasta la base de datos. Se perdería la flexibilidad de sustitución.

#### B) Valor para el Negocio

**¿Por qué existe?**
Define qué puede hacer el sistema con las categorías del catálogo. Es el "manual de operaciones" de la sección de categorías, escrito como contrato formal.

**¿Qué necesidad satisface?**
Permite que el equipo de desarrollo trabaje en paralelo: quien desarrolla la API (usando la interfaz) y quien implementa la lógica (implementando la interfaz) pueden trabajar simultáneamente sobre el mismo contrato.

**¿Qué ocurriría si no existiera?**
`CategoryController` dependería directamente de `CategoryServiceImpl`. Si la implementación cambia (nueva base de datos, nueva estrategia de caché), el controller también tiene que cambiar, rompiendo el principio de capas.

---

### ProductService

**Ruta:** `com.university.shop.application.port.ProductService`

#### A) Justificación del Tipo

**Tipo Java:** `interface` (puerto de entrada, análogo a `CategoryService`).

**¿Por qué `interface` y los mismos argumentos que `CategoryService`?**
Aplican exactamente los mismos principios: DIP, testabilidad, múltiples implementaciones, separación de contrato e implementación.

**¿Qué declara la interfaz?**
Los **6 casos de uso del negocio** de productos:
- `createProduct(ProductRequestDTO)` → registrar artículo
- `getAllProducts()` → lista completa (para dropdowns)
- `searchProducts(name, categoryId, page, size)` → búsqueda paginada con filtros
- `getProductById(Long)` → detalle de un artículo
- `updateProduct(Long, ProductUpdateDTO)` → actualizar artículo (sin SKU)
- `deleteProduct(Long)` → retirar artículo

**Patrón de diseño:** Port (entrada hexagonal) + Strategy.

**Diferencia clave vs `CategoryService`:** `ProductService` tiene el método `searchProducts` con parámetros de búsqueda y paginación, reflejando que los productos son el recurso primario del catálogo (con más operaciones de consulta complejas que las categorías).

#### B) Valor para el Negocio

**¿Por qué existe?**
Define el contrato completo de gestión del catálogo de productos. Es el corazón del sistema: todos los casos de uso relacionados con los artículos de la tienda están aquí declarados.

**¿Qué ocurriría si no existiera?**
`ProductController` dependería de `ProductServiceImpl` directamente. Pruebas unitarias del controller necesitarían una base de datos. Cambiar la implementación del servicio requeriría modificar el controller.

---

## 6. ANÁLISIS DE COMPONENTES — CAPA DOMAIN (ENTIDADES)

---

### Category

**Ruta:** `com.university.shop.domain.Category`

#### A) Justificación del Tipo

**Tipo Java:** `class` con anotaciones JPA (`@Entity`, `@Table`, `@Column`, `@Id`, `@GeneratedValue`).

**¿Por qué `class` y no `record`?**
Las entidades JPA **requieren** constructor sin argumentos (Hibernate lo necesita para instanciar objetos al leer de la base de datos) y campos mutables con setters (Hibernate necesita setear el `id` después de un INSERT). Los `record` de Java son inmutables por diseño (campos `final`, sin setters), lo que los hace incompatibles con la semántica de las entidades JPA sin configuración especial compleja.

**¿Por qué `@Entity` + `@Table(name = "category")`?**
- `@Entity` → registra la clase como entidad persistible en el contexto de persistencia de JPA. Sin esto, Hibernate no sabe que esta clase corresponde a una tabla.
- `@Table(name = "category")` → especifica el nombre exacto de la tabla en la base de datos. Sin esta anotación, JPA usaría el nombre de la clase en minúsculas por convención, lo que puede ser ambiguo en algunas BD.

**¿Por qué no incluye `@OneToMany(products)` aunque existe la relación?**
El código y sus comentarios explican la decisión: incluir `@OneToMany` bidireccional causa **bucles infinitos de serialización JSON** (Category → Products → Category → ...). Para este proyecto, la navegación es unidireccional: `Product → Category`. Si se necesita "los productos de una categoría", se consulta por `productRepository.findByCategoryId(id)`.

**¿Por qué `@Column(unique=true)` en `name`?**
Porque la regla de negocio "no puede haber dos categorías con el mismo nombre" debe garantizarse a dos niveles: a nivel de aplicación (verificación en `CategoryServiceImpl`) y a nivel de base de datos (restricción UNIQUE en la columna). La restricción de BD es el último bastión de integridad.

**Patrón de diseño:** Entity (DDD Entity Pattern) — objeto con identidad propia (id) y ciclo de vida gestionado por la BD.

**¿Qué pasaría si `name` no tuviera `unique=true`?**
Podría existir "Papelería" dos veces en la BD si dos transacciones concurrentes pasan la validación de `existsByName()` al mismo tiempo (condición de carrera). La restricción UNIQUE en la BD es la protección definitiva contra este escenario.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque en la tienda real existen secciones que agrupan productos ("Papelería", "Tecnología"). `Category` es la representación digital de esa sección. Sin categorías, los productos no tendrían clasificación y el catálogo sería una lista plana inorganizada.

**¿Qué necesidad satisface?**
Permite organizar el inventario en grupos lógicos, facilitar la búsqueda por sección y garantizar que cada producto pertenezca a un área del catálogo.

**¿Qué ocurriría si no existiera?**
No habría forma de clasificar los productos. La regla de negocio "todo producto debe tener categoría" sería imposible de implementar. Los clientes no podrían filtrar el catálogo por tipo de artículo.

---

### Product

**Ruta:** `com.university.shop.domain.Product`

#### A) Justificación del Tipo

**Tipo Java:** `class` con anotaciones JPA + `@PrePersist` lifecycle callback.

**¿Por qué `class` y no `record`?**
Mismo argumento que `Category`: Hibernate requiere constructor sin argumentos y setters mutables. Adicionalmente, el callback `@PrePersist` debe ser un método de instancia en la clase.

**¿Por qué `@ManyToOne(fetch = FetchType.EAGER)` y no `LAZY`?**
`EAGER` carga la categoría automáticamente junto con el producto en el mismo SELECT. Esto es correcto para este demo porque `ProductResponseDTO` siempre incluye la categoría embebida — si fuera `LAZY`, se necesitaría inicializar la sesión JPA explícitamente al mapear. La nota en el código reconoce que en producción con catálogos grandes se debería evaluar `LAZY` con `JOIN FETCH` explícito en queries específicas.

**¿Por qué `@JoinColumn(name = "category_id", nullable = false)`?**
Define la columna de FK en la tabla `product` (`category_id`) y establece que `nullable = false`: **todo producto debe pertenecer a una categoría**, sin excepciones. Esto refuerza a nivel de BD la regla de negocio validada por `ProductRequestDTO` (`@NotNull` en `categoryId`).

**¿Por qué `@Column(updatable = false)` en `dateCreated`?**
La fecha de creación es un dato de auditoría inmutable: representa cuándo el artículo fue incorporado al catálogo por primera vez. Al marcarla con `updatable = false`, Hibernate nunca incluye este campo en un UPDATE SQL, garantizando que la fecha original no pueda sobreescribirse accidentalmente.

**¿Por qué `@PrePersist onPrePersist()`?**
Es un callback del ciclo de vida JPA que se ejecuta automáticamente **antes** de que Hibernate ejecute el INSERT en la base de datos. Al asignar `dateCreated = LocalDateTime.now()` aquí, la fecha de registro es siempre la del momento real de persistencia, independientemente de qué valor tenga el campo en el objeto Java antes de guardarse. El cliente no puede manipular este campo.

**Patrón de diseño:** Entity (DDD Aggregate Root) — `Product` es el Aggregate Root del bounded context del catálogo. Lifecycle Hook (callback `@PrePersist`).

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque un artículo de la tienda tiene propiedades reales que el sistema necesita recordar: su código de referencia (SKU), nombre, precio, cuándo fue incorporado al inventario y a qué sección pertenece.

**¿Qué necesidad satisface?**
Es el objeto central de todo el sistema. Sin `Product`, no hay catálogo. Es la razón de existir del proyecto.

**¿Qué ocurriría si no existiera?**
No habría ningún objeto que representar, gestionar ni consultar. El sistema entero no tendría propósito.

---

## 7. ANÁLISIS DE COMPONENTES — CAPA DOMAIN (EXCEPCIONES)

---

### CategoryHasProductsException

**Ruta:** `com.university.shop.domain.exception.CategoryHasProductsException`

#### A) Justificación del Tipo

**Tipo Java:** `class` que extiende `RuntimeException` (excepción no chequeada / unchecked).

**¿Por qué hereda de `RuntimeException` y no de `Exception`?**
Las excepciones de dominio representan violaciones a las reglas de negocio — condiciones que el llamador raramente puede "recuperar" programáticamente. Al ser `RuntimeException`:
1. El código que la lanza (`CategoryServiceImpl.deleteCategory()`) no necesita declarar `throws CategoryHasProductsException` en su firma, manteniendo el código limpio.
2. La excepción "flota" hasta el `GlobalExceptionHandler` sin ser capturada en capas intermedias, que no tienen contexto para manejarla.
3. Es coherente con el enfoque de Spring: la mayoría de sus propias excepciones de negocio (DataAccessException, etc.) son `RuntimeException`.

**¿Por qué no `IllegalStateException` o `IllegalArgumentException` genéricas?**
Porque las excepciones genéricas de la JDK no comunican la intención del dominio. `CategoryHasProductsException` es semántica: su nombre solo puede significar una cosa. Además, permite al `GlobalExceptionHandler` tener un `@ExceptionHandler(CategoryHasProductsException.class)` específico que retorna exactamente 409 Conflict con el mensaje correcto.

**¿Por qué almacenar `categoryId` como campo `final`?**
Para que el mensaje de error sea preciso: "La categoría con id **X** tiene productos asociados". Sin este campo, el mensaje sería genérico e inutilizable para debugging o para que el frontend sugiera qué categoría tiene el problema.

**Patrón de diseño:** Domain Exception (DDD Pattern) — excepción que representa la violación de una invariante del dominio.

**¿Qué pasaría si se usara `RuntimeException("mensaje")` directamente?**
El `GlobalExceptionHandler` no podría distinguir esta excepción de otras `RuntimeException`. Devolvería 500 en lugar de 409, y el cliente no sabría qué problema ocurrió.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque la tienda tiene una regla operativa crítica: **no se puede eliminar una sección del catálogo si aún tiene artículos activos**. Hacerlo dejaría esos artículos sin categoría válida, corrompiendo el catálogo. Esta excepción es el mecanismo que hace esa regla imposible de violar.

**¿Qué ocurriría si no existiera?**
Si el administrador intentara eliminar "Papelería" (que tiene 50 productos), la BD lanzaría una `DataIntegrityViolationException` genérica de FK violation. El cliente recibiría un mensaje técnico incomprensible. Con esta excepción, recibe: "La categoría con id 3 tiene productos asociados y no puede eliminarse."

---

### CategoryNameAlreadyExistsException

**Ruta:** `com.university.shop.domain.exception.CategoryNameAlreadyExistsException`

#### A) Justificación del Tipo

**Tipo Java:** `class` que extiende `RuntimeException`. Campo `final String name`.

**¿Por qué `RuntimeException`?**
Mismo argumento: violación de regla de negocio, no error técnico recuperable. No tiene sentido capturarla en capas intermedias.

**¿Por qué almacenar el `name` afectado?**
Para que el mensaje de error sea accionable: "Ya existe una categoría con el nombre **'Papelería'**". El administrador sabe exactamente qué nombre causó el conflicto sin tener que buscar en el catálogo.

**Patrón:** Domain Exception. El `GlobalExceptionHandler` la mapea a HTTP 409 Conflict.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque la tienda necesita que cada sección del catálogo tenga un nombre único para evitar confusiones. Si existieran dos secciones "Papelería", los reportes y la navegación del catálogo serían ambiguos.

**¿Qué ocurriría si no existiera?**
Sin esta excepción, la validación de unicidad podría lanzar una excepción genérica de BD (constraint violation). El cliente no sabría qué campo o valor causó el conflicto.

---

### CategoryNotFoundException

**Ruta:** `com.university.shop.domain.exception.CategoryNotFoundException`

#### A) Justificación del Tipo

**Tipo Java:** `class` que extiende `RuntimeException`. Campo `final Long categoryId`.

**¿Por qué `RuntimeException` para una búsqueda fallida?**
Una búsqueda fallida en el contexto del negocio es una condición excepcional que rompe el flujo normal (el cliente pidió algo que no existe). No es un error técnico recuperable por el llamador: la respuesta correcta es notificar al cliente con 404.

**Patrón:** Domain Exception. El `GlobalExceptionHandler` la mapea a HTTP 404 Not Found.

#### B) Valor para el Negocio

**¿Por qué existe?**
Cuando un administrador intenta asociar un producto a una categoría que no existe, o cuando un cliente consulta una categoría por ID inexistente, el sistema debe informarlo con claridad. Sin esta excepción, el código tendría que manejar `Optional.empty()` en cada punto de uso.

---

### ProductNotFoundException

**Ruta:** `com.university.shop.domain.exception.ProductNotFoundException`

#### A) Justificación del Tipo

**Tipo Java:** `class` que extiende `RuntimeException`. Campo `final Long productId`.

**Idéntico razonamiento a `CategoryNotFoundException`** pero para el recurso `Product`. El `GlobalExceptionHandler` la mapea a HTTP 404 Not Found con el mensaje "El producto con ID X no fue encontrado."

**Patrón:** Domain Exception.

#### B) Valor para el Negocio

**¿Por qué existe?**
Cuando un cliente consulta un producto por ID que no existe en el catálogo, o cuando el administrador intenta actualizar o eliminar un artículo inexistente, el sistema debe responder con claridad en lugar de un NullPointerException o un error genérico 500.

---

### SkuAlreadyExistsException

**Ruta:** `com.university.shop.domain.exception.SkuAlreadyExistsException`

#### A) Justificación del Tipo

**Tipo Java:** `class` que extiende `RuntimeException`. Campo `final String sku`. Es la excepción más documentada del proyecto: su Javadoc explica exactamente por qué hereda de `RuntimeException` y por qué no es una excepción genérica.

**¿Por qué `RuntimeException` (unchecked)?**
El comentario en el código lo explica: el servicio no necesita declarar `throws` propagando la excepción por toda la cadena, manteniendo el código limpio. El `GlobalExceptionHandler` la captura centralizadamente.

**¿Por qué no `IllegalArgumentException`?**
Porque `IllegalArgumentException` es una excepción de propósito general de la JDK que no comunica la intención del dominio. `SkuAlreadyExistsException` es específica: solo puede significar que el SKU ya existe en el catálogo, y permite el `@ExceptionHandler` específico → HTTP 409 Conflict.

**Patrón:** Domain Exception — violación de la invariante de unicidad de SKU del catálogo.

#### B) Valor para el Negocio

**¿Por qué existe?**
El SKU es el **identificador comercial único de un artículo**. Si dos productos tuvieran el mismo SKU, los sistemas externos (ERP, logística, facturación) no podrían distinguirlos, generando errores operativos graves. Esta excepción garantiza que la regla de unicidad de SKU sea imposible de violar.

**¿Qué ocurriría si no existiera?**
Sin la verificación previa de `existsBySku()` + esta excepción, el primer intento de guardar llegaría a la BD, que rechazaría el INSERT con una `DataIntegrityViolationException` genérica. El cliente recibiría "Violación de integridad de datos" sin saber qué campo o valor causó el problema.

---

## 8. ANÁLISIS DE COMPONENTES — CAPA INFRASTRUCTURE

---

### JwtAuthenticationFilter

**Ruta:** `com.university.shop.infrastructure.security.JwtAuthenticationFilter`

#### A) Justificación del Tipo

**Tipo Java:** `class` que extiende `OncePerRequestFilter`, anotada con `@Component`.

**¿Por qué extender `OncePerRequestFilter` en lugar de implementar `Filter` directamente?**
`OncePerRequestFilter` garantiza que el filtro se ejecute **exactamente una vez por petición HTTP**, incluso si la petición pasa por múltiples dispatchers de Spring (ej. forward interno, include). Si se implementara `Filter` directamente, podría ejecutarse múltiples veces en ciertos escenarios de reenvío, causando intentos duplicados de autenticación.

**¿Por qué `@Component` y no `@Service`?**
`@Component` es el estereotipo genérico para beans de Spring que no encajan en roles más específicos. Este filtro no es un servicio de negocio, no es un repositorio, no es un controller — es un componente de infraestructura. `@Component` es semánticamente correcto.

**¿Por qué no `@Bean` en `SecurityConfig`?**
El filtro podría registrarse como `@Bean` en `SecurityConfig`, pero al anotarse con `@Component` Spring lo detecta automáticamente por component scanning. `SecurityConfig.securityFilterChain()` lo inyecta explícitamente en la cadena de filtros con `addFilterBefore()`, dando control sobre el orden de ejecución sin necesidad de un `@Bean` adicional.

**¿Por qué asigna siempre `ROLE_ADMIN` al token válido?**
Porque en este demo académico solo existe un usuario administrador. En un sistema real, el rol vendría como claim en el payload del JWT y se extraería con `jwtService.extractClaims(token)`. El comentario en el código reconoce esta simplificación.

**Patrón de diseño:** Chain of Responsibility (eslabón en la cadena de filtros de Spring Security) + Adapter (adapta la validación JWT a la abstracción de autenticación de Spring Security, `SecurityContextHolder`).

**¿Qué pasaría si no heredara de `OncePerRequestFilter`?**
En dispatches internos de Spring (forward), el filtro podría ejecutarse dos veces: la primera validaría correctamente, la segunda intentaría validar de nuevo (ya en el SecurityContext) o fallaría por condiciones de carrera.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque en cada petición que llega al sistema, alguien tiene que verificar si el solicitante tiene derecho a realizar la operación solicitada. Este filtro es el guardia de seguridad de la tienda: revisa el "pase de acceso" (JWT) antes de permitir acceso a las operaciones de gestión.

**¿Qué necesidad satisface?**
- Permite que las peticiones sin token pasen como anónimas (clientes consultando el catálogo).
- Autentica automáticamente a los administradores con token válido.
- Bloquea silenciosamente (sin autenticar) a quien presente un token inválido o expirado.

**¿Qué ocurriría si no existiera?**
Spring Security no sabría quién hace cada petición. Todos los endpoints protegidos (POST/PUT/DELETE) rechazarían todas las peticiones, incluyendo las del administrador con token válido.

---

### JwtService

**Ruta:** `com.university.shop.infrastructure.security.JwtService`

#### A) Justificación del Tipo

**Tipo Java:** `class` con `@Service`. Campos `final` (`signingKey`, `expirationMs`) inicializados en el constructor.

**¿Por qué `class` y no `interface`?**
Porque encapsula lógica concreta de criptografía: generación de tokens firmados con HMAC-SHA256 usando la librería JJWT. Una interfaz no puede contener esta implementación. Si hubiera necesidad de múltiples estrategias de token (ej. RS256 asimétrico), se crearía una interfaz `TokenService` con dos implementaciones. En el estado actual, el diseño es YAGNI (You Ain't Gonna Need It) — la clase concreta es suficiente.

**¿Por qué `@Service` y no `@Component`?**
`@Service` indica que la clase pertenece a la capa de servicio y encapsula lógica de negocio (en este caso, lógica de seguridad). Es semánticamente más preciso que `@Component` para una clase que realiza una función de negocio específica (emisión y validación de tokens).

**¿Por qué la validación de la clave en el constructor?**
```java
if (keyBytes.length < 32) {
    throw new IllegalArgumentException("jwt.secret must be at least 32 bytes for HS256");
}
```
HS256 requiere una clave de al menos 256 bits = 32 bytes. La validación en el constructor hace que la aplicación **falle rápido** (`fail fast`) durante el arranque si la clave es demasiado corta, en lugar de emitir tokens que los validadores rechazarían después. Esto es una práctica de seguridad defensiva.

**¿Por qué `@Value("${jwt.secret}")` y `@Value("${jwt.expiration-ms}")`?**
Porque el secreto y la expiración son **configuración de entorno**, no constantes del código. En desarrollo se usa un secreto de prueba; en producción (`application-prod.yml`) se requiere `JWT_SECRET` como variable de entorno. Separar la configuración del código es un principio de los [12-Factor Apps](https://12factor.net/config).

**Patrón de diseño:** Service (lógica de negocio de seguridad encapsulada). Los métodos `generateToken`, `isValid` y `extractUsername` siguen el patrón Utility Method con estado interno (la clave de firma).

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque el sistema necesita emitir "pases de acceso" digitales (tokens JWT) que acrediten la identidad del administrador, y validar esos pases en cada petición subsiguiente sin consultar la base de datos.

**¿Qué necesidad satisface?**
- Generar tokens firmados que no pueden ser falsificados sin conocer el secreto.
- Validar tokens en tiempo real sin estado del lado del servidor (stateless).
- Extraer el nombre de usuario del token para identificar quién hace la petición.

**¿Qué ocurriría si no existiera?**
El sistema no podría emitir ni validar tokens JWT. La autenticación del administrador sería imposible, o tendría que usar sesiones de servidor (stateful), lo que contradice la arquitectura stateless necesaria para escalar horizontalmente.

---

### CategoryServiceImpl

**Ruta:** `com.university.shop.infrastructure.service.CategoryServiceImpl`

#### A) Justificación del Tipo

**Tipo Java:** `class` con `@Service` que implementa la interfaz `CategoryService`.

**¿Por qué `class` (y no interfaz)?**
Porque necesita contener la **lógica concreta** de cada caso de uso: consultar el repositorio, aplicar validaciones de negocio, lanzar excepciones de dominio y mapear entidades a DTOs. La interfaz define el "qué"; esta clase define el "cómo".

**¿Por qué implementar `CategoryService` (interfaz) en lugar de ser una clase independiente?**
Porque `CategoryController` depende de la interfaz `CategoryService`, no de esta clase. Spring IoC resuelve automáticamente: "cuando alguien necesite `CategoryService`, inyecta `CategoryServiceImpl`". Esto aplica DIP y el patrón Adapter: `CategoryServiceImpl` es el adaptador entre el puerto `CategoryService` y los repositorios JPA.

**¿Por qué `@Service` y no `@Component`?**
`@Service` comunica explícitamente que esta clase encapsula lógica de negocio (servicio de aplicación). Spring trata `@Service` y `@Component` casi igual funcionalmente, pero `@Service` hace el código más autoexplicativo.

**¿Por qué inyectar tanto `CategoryRepository` como `ProductRepository`?**
`CategoryServiceImpl` necesita `ProductRepository` en el caso de uso de eliminación: antes de eliminar una categoría, verifica `productRepository.countByCategory_Id(id) > 0`. Si hay productos, lanza `CategoryHasProductsException`. Esta es una regla de integridad referencial aplicada a nivel de aplicación (antes que la BD).

**¿Por qué `@Transactional` en operaciones de escritura y `@Transactional(readOnly = true)` en lecturas?**
- `@Transactional` en escrituras: garantiza que si falla algún paso (ej. la validación pasa pero el save falla), toda la operación se revierte (rollback automático).
- `@Transactional(readOnly = true)` en lecturas: permite que el motor de BD (y Hibernate) optimicen la consulta: no se toma lock en las filas, se puede usar una réplica de lectura, Hibernate no rastrea cambios (flush is disabled).

**Patrón de diseño:** Service Layer (capa de servicios de aplicación) + Adapter (implementa el puerto hexagonal `CategoryService`). Aplica el patrón Template Method implícito: cada caso de uso sigue la estructura "validar → ejecutar → mapear → retornar".

**¿Qué pasaría si fuera la clase que usan directamente los controllers (sin la interfaz)?**
Perdería el desacoplamiento y la testabilidad. El controller y el servicio estarían fuertemente acoplados.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque alguien tiene que coordinar las operaciones de gestión de categorías: verificar que los nombres sean únicos, proteger las categorías con productos, y persistir los cambios en la base de datos.

**Operaciones de negocio que materializa:**
1. **Crear categoría:** verifica nombre único → guarda → devuelve DTO
2. **Listar categorías:** obtiene todas → mapea a DTOs → devuelve lista
3. **Obtener por ID:** busca → si no existe lanza `CategoryNotFoundException` → devuelve DTO
4. **Actualizar:** verifica existe → verifica nombre único (solo si cambió) → guarda → devuelve DTO
5. **Eliminar:** verifica existe → verifica sin productos → elimina

**¿Qué ocurriría si no existiera?**
Los controllers tendrían que implementar directamente toda esta lógica, mezclando responsabilidades HTTP con lógica de negocio — exactamente lo que las capas buscan evitar.

---

### ProductServiceImpl

**Ruta:** `com.university.shop.infrastructure.service.ProductServiceImpl`

#### A) Justificación del Tipo

**Tipo Java:** `class` con `@Service` que implementa `ProductService`. Análogo a `CategoryServiceImpl`.

**¿Por qué tiene el método privado `findCategory(Long categoryId)`?**
Para evitar duplicación: tanto `createProduct` como `updateProduct` necesitan buscar y validar que la categoría existe. En lugar de repetir el `orElseThrow(CategoryNotFoundException...)` dos veces, se extrae a un método privado reutilizable. Aplica DRY.

**¿Por qué el método privado `toResponseDTO(Product)`?**
Por la misma razón: todos los casos de uso que retornan un producto necesitan convertir la entidad JPA a `ProductResponseDTO`. El mapeo se centraliza en un único lugar: si la estructura del `ProductResponseDTO` cambia, solo se modifica en un lugar.

**¿Qué lógica de negocio implementa `searchProducts`?**
Selecciona dinámicamente el método de repositorio correcto según los filtros presentes:
- `hasName && hasCategory` → busca por nombre parcial Y categoría
- `hasName` solo → busca por nombre parcial
- `hasCategory` solo → filtra por categoría
- Sin filtros → devuelve todos paginados

Esta es la única lógica de "ramificación" del servicio y refleja directamente los casos de uso de búsqueda del negocio.

**Patrón:** Service Layer + Adapter + Template Method (estructura: validar → buscar/crear → mapear → retornar).

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque las operaciones de gestión de productos son el núcleo del negocio. Esta clase coordina todas las validaciones, persistencia y mapeos necesarios para que el catálogo de artículos funcione correctamente.

**Casos de uso coordinados:**
1. **Crear artículo:** verifica SKU único + categoría válida → guarda con fecha automática → devuelve ProductResponseDTO
2. **Listar todos:** obtiene todos → mapea con categoría embebida → devuelve lista
3. **Buscar con filtros:** selecciona query dinámica → pagina → mapea → devuelve PagedResponseDTO
4. **Obtener por ID:** busca → si no existe lanza excepción → mapea
5. **Actualizar:** verifica producto existe + nueva categoría válida → actualiza (sin SKU) → guarda
6. **Eliminar:** verifica existe → borra

---

### CategoryRepository

**Ruta:** `com.university.shop.infrastructure.CategoryRepository`

#### A) Justificación del Tipo

**Tipo Java:** `interface` que extiende `JpaRepository<Category, Long>`, anotada con `@Repository`.

**¿Por qué `interface` y no `class`?**
Este es el punto técnico más interesante del proyecto: **Spring Data JPA genera la implementación en tiempo de ejecución** mediante proxies dinámicos (usando Java Reflection + CGLIB). Spring examina la interfaz, infiere las queries SQL a partir de los nombres de los métodos (Derived Query Methods), y construye una implementación completa sin que el desarrollador escriba una sola línea de SQL.

Ejemplo: `boolean existsByName(String name)` → Spring genera automáticamente:
```sql
SELECT COUNT(*) > 0 FROM category WHERE name = ?
```

**¿Por qué `@Repository` si es una interfaz?**
`@Repository` sirve dos propósitos: (1) indica a Spring que es un bean de acceso a datos para que lo detecte en el component scan, y (2) activa la traducción automática de excepciones JPA a `DataAccessException` de Spring (evita que excepciones específicas de Hibernate lleguen a capas superiores).

**¿Por qué `extends JpaRepository<Category, Long>` y no `CrudRepository`?**
`JpaRepository` extiende `CrudRepository` (operaciones CRUD básicas) y `PagingAndSortingRepository` (paginación y ordenamiento). Al usar `JpaRepository`, se hereda un repertorio más amplio de operaciones sin escribir código: `findAll(Pageable)`, `flush()`, `saveAndFlush()`, etc.

**Métodos adicionales declarados:**
- `existsByName(String)` → verifica unicidad de nombre (usado en `createCategory`)
- `existsByNameAndIdNot(String, Long)` → verifica unicidad excluyendo la misma categoría (usado en `updateCategory` — permite que una categoría conserve su propio nombre al actualizarse)

**Patrón de diseño:** Repository (DAO Pattern). Spring Data convierte la interfaz en implementación automáticamente — esto es el patrón **Proxy** generado en tiempo de ejecución.

**¿Qué pasaría si fuera una clase concreta?**
El desarrollador tendría que escribir manualmente el `EntityManager`, las queries JPQL/SQL y el código boilerplate de cada operación. Spring Data existe precisamente para eliminar ese código repetitivo.

#### B) Valor para el Negocio

**¿Por qué existe?**
Para que el catálogo de categorías **persista entre reinicios del servidor**. Sin el repositorio, las categorías existirían solo en memoria y se perderían al apagar la aplicación.

**¿Qué necesidad satisface?**
- Las categorías creadas por el administrador se guardan permanentemente en la BD.
- Se pueden consultar en cualquier momento, desde cualquier instancia del servidor.
- La verificación de unicidad de nombres se delega a la BD mediante queries específicas.

---

### ProductRepository

**Ruta:** `com.university.shop.infrastructure.ProductRepository`

#### A) Justificación del Tipo

**Tipo Java:** `interface` que extiende `JpaRepository<Product, Long>`, anotada con `@Repository`.

**Mismos principios que `CategoryRepository`** — Spring Data genera la implementación en tiempo de ejecución.

**Métodos adicionales de especial valor:**

- `existsBySku(String)` → verificación de unicidad de SKU antes de insertar. SQL: `SELECT COUNT(*) > 0 FROM product WHERE sku = ?`

- `findByNameContainingIgnoreCase(String, Pageable)` → búsqueda parcial insensible a mayúsculas. SQL: `WHERE LOWER(name) LIKE LOWER('%X%')`. Permite buscar "laptop" y encontrar "LAPTOP Dell XPS".

- `findByCategoryId(Long, Pageable)` → filtrado por categoría con paginación. Permite al cliente ver solo los productos de "Tecnología".

- `findByNameContainingIgnoreCaseAndCategoryId(String, Long, Pageable)` → búsqueda combinada: nombre parcial + categoría. Es la query más específica para el motor de búsqueda del catálogo.

- `countByCategory_Id(Long)` → cuenta productos en una categoría. Usado en `CategoryServiceImpl.deleteCategory` para implementar la regla "no eliminar categoría con productos". La sintaxis `Category_Id` usa navegación de asociación JPA: atraviesa la relación `@ManyToOne` hasta el campo `id` de `Category`.

**Patrón de diseño:** Repository (DAO Pattern) + Derived Query Methods (convención de nombres que Spring convierte en SQL).

#### B) Valor para el Negocio

**¿Por qué existe?**
Para persistir el catálogo de productos y proveer capacidades avanzadas de búsqueda que satisfagan las necesidades de los clientes: filtrar por nombre, por categoría, combinar filtros y navegar en páginas.

**¿Qué ocurriría si no existiera?**
Los productos solo existirían en memoria. Además, la búsqueda paginada con filtros tendría que implementarse manualmente con `EntityManager` y JPQL, duplicando código que Spring Data genera automáticamente.

---

## 9. CLASE PRINCIPAL

---

### CatalogApplication

**Ruta:** `com.university.shop.CatalogApplication`

#### A) Justificación del Tipo

**Tipo Java:** `class` con anotación `@SpringBootApplication` y método `main(String[] args)`.

**¿Por qué `class` y no `interface`?**
Porque necesita el método `main(String[] args)`, que en Java es el punto de entrada que la JVM invoca al arrancar. `main` solo puede ser un método estático en una clase — las interfaces no pueden tener el método estático `main` actuando como punto de arranque de una aplicación Spring (aunque Java 8+ permite métodos estáticos en interfaces, Spring no lo detecta como punto de entrada de la misma forma).

**¿Qué hace `@SpringBootApplication`?**
Es una anotación compuesta que activa tres comportamientos simultáneamente:
- `@SpringBootConfiguration` (especialización de `@Configuration`) → esta clase puede declarar beans.
- `@EnableAutoConfiguration` → Spring Boot configura automáticamente JPA, Web, Security, Jackson, etc. basándose en las dependencias en el classpath.
- `@ComponentScan` → escanea el paquete `com.university.shop` y todos sus subpaquetes, detectando automáticamente todos los `@RestController`, `@Service`, `@Repository`, `@Component` y `@Configuration`.

**¿Por qué el `System.out.println` en el método `main`?**
Es un banner de consola que imprime las URLs más importantes (endpoints de la API, consola H2) al arrancar. Es un patrón de conveniencia académica que facilita a los desarrolladores (y evaluadores) saber que el servidor está listo y dónde acceder.

**Patrón de diseño:** Application Entry Point (punto de entrada de la aplicación). En términos de patrones, es el Bootstrap del contexto de Spring IoC.

**¿Qué pasaría si `@SpringBootApplication` se removiera?**
Spring no escanearía automáticamente los componentes ni aplicaría la auto-configuración. El servidor no levantaría beans de JPA, Security ni Web MVC. La aplicación arrancaría pero fallaría al intentar inyectar dependencias.

#### B) Valor para el Negocio

**¿Por qué existe?**
Porque la JVM necesita un punto de entrada para ejecutar la aplicación. Sin esta clase, no existe la tienda digital — es la instrucción "abrir la tienda".

**¿Qué necesidad satisface?**
Inicializar todo el sistema: la base de datos, el servidor web, la seguridad, los repositorios, los servicios — todo el ecosistema que hace funcionar el catálogo.

**¿Qué ocurriría si no existiera?**
El proyecto no podría ejecutarse. No hay tienda sin encender el sistema.

---

## 10. ARCHIVOS DE CONFIGURACIÓN

---

### application.yml (Desarrollo local)

**Tipo:** Archivo YAML de configuración de Spring Boot (perfil por defecto).

**Configuración clave y su significado de negocio:**

| Propiedad                             | Valor                            | Significado de negocio                                                        |
|---------------------------------------|----------------------------------|-------------------------------------------------------------------------------|
| `spring.datasource.url`               | `jdbc:h2:mem:catalogdb`          | BD en memoria para desarrollo: el catálogo vive en RAM, sin instalar nada     |
| `spring.jpa.hibernate.ddl-auto`       | `create-drop`                    | Las tablas se crean al arrancar y se eliminan al parar — adecuado para demo   |
| `spring.h2.console.enabled`           | `true`                           | Consola web en `/h2-console` para inspeccionar la BD durante el desarrollo    |
| `jwt.secret`                          | `mi-clave-secreta-para-demo...`  | Clave de firma del JWT — en desarrollo usa valor por defecto                  |
| `jwt.expiration-ms`                   | `86400000` (24h)                 | El "pase de acceso" del administrador dura 24 horas                           |
| `springdoc.swagger-ui.enabled`        | `true`                           | Swagger UI disponible en `/swagger-ui.html`                                   |

---

### application-prod.yml (Producción)

**Tipo:** Archivo YAML de configuración para el perfil `prod` (activado con `SPRING_PROFILES_ACTIVE=prod`).

| Propiedad                             | Valor                                        | Significado de negocio                                                    |
|---------------------------------------|----------------------------------------------|---------------------------------------------------------------------------|
| `spring.datasource.url`               | `jdbc:postgresql://db:5432/catalogdb`        | BD persistente PostgreSQL — el catálogo no se pierde al reiniciar         |
| `spring.jpa.hibernate.ddl-auto`       | `update`                                     | Actualiza el schema sin destruir datos — safe para producción             |
| `spring.h2.console.enabled`           | `false`                                      | Consola de BD deshabilitada — no exponer acceso interno en producción     |
| `jwt.secret`                          | `${JWT_SECRET}`                              | Clave de firma JWT tomada de variable de entorno — nunca hardcodeada      |

---

## 11. FLUJO COMPLETO DEL NEGOCIO

### Flujo Funcional — Perspectiva del Administrador

**Escenario: El administrador prepara el catálogo para el inicio del semestre**

```
ADMINISTRADOR
        │
        ▼
1. Se autentica en el sistema
   Envía: usuario "admin" y contraseña "admin123"
   Recibe: token JWT válido por 24 horas
        │
        ▼
2. Crea las secciones del catálogo
   Crea "Papelería" → El sistema verifica que el nombre es único → Guarda → Responde con ID y datos
   Crea "Tecnología" → Mismo proceso
   Crea "Libros de Texto" → Mismo proceso
        │
        ▼
3. Registra los artículos del catálogo
   Registra "Cuaderno" (SKU: PAP-001, $45, Papelería)
   → El sistema verifica: ¿SKU duplicado? No → ¿Categoría existe? Sí → Guarda con fecha automática
   Registra "USB 64GB" (SKU: TEC-001, $180, Tecnología) → mismo proceso
   Registra "Cálculo Diferencial" (SKU: LIB-001, $320, Libros de Texto) → mismo proceso
        │
        ▼
EL CATÁLOGO ESTÁ LISTO Y DISPONIBLE PÚBLICAMENTE

CLIENTE / ESTUDIANTE
        │
        ▼
4. Explora el catálogo (sin necesidad de autenticarse)
   Busca "cuaderno" → Recibe: 1 resultado paginado con precio y categoría
   Explora todos los productos → Recibe: 3 productos, página 1 de 1
   Consulta detalle de producto ID 1 → Recibe: datos completos + "Papelería" embebida
        │
        ▼
5. Toma decisión de compra informada
```

---

### Flujo Funcional — Escenario de Error de Negocio

```
ADMINISTRADOR
        │
        ▼
Intenta registrar "USB 128GB" con SKU: TEC-001 (ya existe en el catálogo)
        │
        ▼
El sistema detecta que TEC-001 ya está registrado
(antes de llegar a la base de datos)
        │
        ▼
Responde con:
  HTTP 409 Conflict
  { "message": "El SKU 'TEC-001' ya está registrado en el catálogo", "status": 409 }
        │
        ▼
El administrador usa un SKU diferente (TEC-002) y la operación procede exitosamente
```

---

### Flujo Técnico Completo — POST /api/v1/products

```
HTTP Request: POST /api/v1/products
Headers: Authorization: Bearer eyJhbGci...
Body: { "sku": "TEC-002", "name": "Mouse Logitech", "price": 250.00, "categoryId": 2 }
        │
        ▼
[1] JwtAuthenticationFilter (@Component, extiende OncePerRequestFilter)
    → Extrae "Bearer eyJhbGci..." del header Authorization
    → token = "eyJhbGci..." (sin "Bearer ")
    → JwtService.isValid(token) → true (firma HMAC-SHA256 verificada, no expirado)
    → username = JwtService.extractUsername(token) → "admin"
    → SecurityContextHolder ← UsernamePasswordAuthenticationToken("admin", ROLE_ADMIN)
    → chain.doFilter() → continúa
        │
        ▼
[2] Spring Security Filter Chain (SecurityConfig)
    → POST /api/v1/products no está en permitAll() → requiere autenticación
    → SecurityContext tiene "admin" autenticado con ROLE_ADMIN → autorizado
    → Pasa al DispatcherServlet de Spring MVC
        │
        ▼
[3] ProductController (@RestController, @RequestMapping("/api/v1/products"))
    → @PostMapping recibe la petición
    → @Valid @RequestBody ProductRequestDTO request → Bean Validation ejecuta:
        @NotBlank(sku) → "TEC-002" ✓
        @NotBlank(name) → "Mouse Logitech" ✓
        @NotNull + @DecimalMin("0.01") price → 250.00 ✓
        @NotNull categoryId → 2L ✓
    → productService.createProduct(request)
    [ProductService es la interfaz → Spring inyecta ProductServiceImpl]
        │
        ▼
[4] ProductServiceImpl (@Service, implements ProductService)
    → productRepository.existsBySku("TEC-002")
       → SQL: SELECT COUNT(*) > 0 FROM product WHERE sku = 'TEC-002'
       → Resultado: false → continúa (SKU disponible)
    → findCategory(2L):
       → categoryRepository.findById(2L)
       → SQL: SELECT * FROM category WHERE id = 2
       → Resultado: Category{id=2, name="Tecnología"} → continúa
    → new Product()
       → product.setSku("TEC-002")
       → product.setName("Mouse Logitech")
       → product.setPrice(250.00)
       → product.setCategory(categoria "Tecnología")
    → productRepository.save(product)
        │
        ▼
[5] ProductRepository (interface) → Spring Data JPA proxy
    → Hibernate Session abre transacción (@Transactional activo)
    → @PrePersist onPrePersist() → product.dateCreated = LocalDateTime.now()
    → Hibernate genera:
       INSERT INTO product (sku, name, price, date_created, category_id)
       VALUES ('TEC-002', 'Mouse Logitech', 250.00, '2026-05-29T15:30:00', 2)
    → PostgreSQL (o H2) ejecuta el INSERT
    → Retorna Product{id=7, sku="TEC-002", ...} con id asignado
    → Hibernate commit → cierra transacción
        │
        ▼
[6] ProductServiceImpl (continuación)
    → toResponseDTO(product):
       → new CategoryResponseDTO(2, "Tecnología", "Dispositivos...")
       → new ProductResponseDTO(7, "TEC-002", "Mouse Logitech", 250.00,
                                 LocalDateTime.now(), categoryDTO)
    → retorna ProductResponseDTO
        │
        ▼
[7] ProductController (continuación)
    → return ResponseEntity.status(201).body(productResponseDTO)
        │
        ▼
[8] Jackson (serializador JSON)
    → serializa ProductResponseDTO a JSON:
    {
      "id": 7,
      "sku": "TEC-002",
      "name": "Mouse Logitech",
      "price": 250.00,
      "dateCreated": "2026-05-29T15:30:00",
      "category": {
        "id": 2,
        "name": "Tecnología",
        "description": "Dispositivos electrónicos"
      }
    }
        │
        ▼
HTTP Response: 201 Created con el JSON anterior
```

---

### Flujo Técnico — Manejo de Error (SKU Duplicado)

```
POST /api/v1/products
Body: { "sku": "TEC-001", "name": "USB 128GB", "price": 200.00, "categoryId": 2 }
        │
        ▼
[1] JwtAuthenticationFilter → token válido → autenticado
[2] SecurityConfig → autorizado
[3] ProductController → @Valid → validaciones pasan
        │
        ▼
[4] ProductServiceImpl.createProduct()
    → productRepository.existsBySku("TEC-001") → true (ya existe)
    → throw new SkuAlreadyExistsException("TEC-001")
        │
        ▼
[5] Excepción flota (RuntimeException, no chequeada)
    pasa por ProductController sin ser capturada
    pasa por Spring DispatcherServlet
        │
        ▼
[6] GlobalExceptionHandler (@RestControllerAdvice)
    → @ExceptionHandler(SkuAlreadyExistsException.class) se activa
    → buildError(HttpStatus.CONFLICT, "El SKU 'TEC-001' ya está registrado en el catálogo")
    → return ResponseEntity.status(409).body({
         "timestamp": "2026-05-29T15:31:00",
         "status": 409,
         "error": "Conflict",
         "message": "El SKU 'TEC-001' ya está registrado en el catálogo"
       })
        │
        ▼
HTTP Response: 409 Conflict con JSON de error
```

---

## 12. CONCLUSIÓN ORIENTADA AL PROBLEMA

### ¿Qué problema resuelve el proyecto `catalog-demo`?

El proyecto resuelve la necesidad de una tienda universitaria de tener un **catálogo digital centralizado** de sus productos, con gestión segura para el administrador y acceso público para los clientes. Concretamente:

1. **Centralización:** un solo sistema es la fuente de verdad del inventario (no hojas de cálculo dispersas).
2. **Organización:** los productos se clasifican en categorías con reglas de integridad (nombres únicos, no eliminar si tiene productos).
3. **Integridad:** reglas de negocio automatizadas evitan SKUs duplicados y datos inconsistentes.
4. **Control de acceso:** solo el personal autorizado puede modificar el catálogo; los clientes consultan sin barreras.
5. **Exposición estándar:** API REST documentada que puede ser consumida por cualquier frontend o sistema externo.

---

### ¿Cómo la arquitectura hexagonal ayuda a resolver ese problema?

La arquitectura en capas con elementos hexagonales permite que cada problema tenga exactamente un lugar donde resolverse:

```
PROBLEMA DE NEGOCIO          CAPA QUE LO RESUELVE
───────────────────────────  ────────────────────────────────────────
¿Cómo expongo el catálogo?   API (controllers, security, Swagger)
¿Qué puede hacer el sistema? Application (interfaces-puertos, DTOs)
¿Qué reglas rigen el negocio? Domain (entidades, excepciones)
¿Cómo persisto los datos?    Infrastructure (repositorios, servicios)
```

Si mañana se decide cambiar la base de datos de PostgreSQL a MongoDB, solo cambia la capa Infrastructure — los controllers, los DTOs y las reglas de negocio permanecen intactos.

---

### ¿Cómo contribuye cada capa a la solución?

| Capa             | Contribución concreta al catálogo de la tienda                          |
|------------------|-------------------------------------------------------------------------|
| **API**          | Hace accesible el catálogo desde cualquier cliente HTTP                 |
| **Application**  | Define los contratos de qué puede hacer el sistema y cómo se intercambia la información |
| **Domain**       | Garantiza que las reglas del negocio (SKU único, integridad referencial) sean imposibles de violar |
| **Infrastructure**| Hace que el catálogo persista, sea buscable y esté protegido por autenticación JWT |

---

### ¿Cómo contribuye cada módulo al objetivo del negocio?

| Módulo / Componente            | Contribución directa al objetivo del catálogo                             |
|--------------------------------|---------------------------------------------------------------------------|
| `ProductController`            | Permite que clientes y admins accedan al inventario de artículos          |
| `CategoryController`           | Permite organizar el catálogo en secciones con integridad                 |
| `AuthController`               | Controla quién puede modificar el catálogo                                |
| `GlobalExceptionHandler`       | Comunica errores de negocio de forma comprensible a los usuarios          |
| `SecurityConfig`               | Implementa la política "catálogo público / gestión privada"               |
| `OpenApiConfig`                | Facilita la integración con frontends y sistemas externos                 |
| `ProductService/CategoryService`| Contratos estables que permiten evolucionar implementaciones sin romper clientes |
| `ProductRequestDTO/UpdateDTO`  | Definen los formularios de ingreso de artículos con validaciones de negocio |
| `ProductResponseDTO`           | Define la ficha completa de un artículo (con categoría embebida)          |
| `PagedResponseDTO<T>`          | Permite navegar catálogos grandes de forma eficiente                      |
| `Product` + `Category`         | Representan los objetos reales de la tienda en forma persistible           |
| 5 excepciones de dominio       | Implementan las reglas de negocio haciéndolas imposibles de violar        |
| `ProductServiceImpl`           | Coordina las 6 operaciones del inventario de artículos                    |
| `CategoryServiceImpl`          | Coordina las 5 operaciones de gestión de secciones                        |
| `ProductRepository`            | Persiste el inventario con capacidades de búsqueda avanzada               |
| `CategoryRepository`           | Persiste las secciones con verificación de unicidad                       |
| `JwtService`                   | Emite y valida los pases de acceso del administrador                      |
| `JwtAuthenticationFilter`      | Verifica la identidad del solicitante en cada petición                    |
| `CatalogApplication`           | Arranca todo el sistema — sin él no existe la tienda digital              |

---

### ¿Qué valor aporta el sistema a sus usuarios?

**Para el Administrador:**
- Gestión del catálogo desde cualquier lugar con conexión a internet.
- Protección automática contra errores comunes (SKU duplicados, referencias inconsistentes).
- Documentación interactiva disponible en `/swagger-ui.html`.
- Persistencia garantizada: los cambios no se pierden entre sesiones.

**Para el Cliente/Estudiante:**
- Acceso inmediato al catálogo sin registrarse.
- Búsqueda inteligente por nombre o categoría.
- Información completa en una sola respuesta (producto + categoría embebida).
- Navegación eficiente mediante paginación.

---

### ¿La implementación actual satisface completamente las necesidades del negocio?

**Satisface el núcleo (~75%). Lo que está completo:**

| Necesidad de negocio                    | Estado    |
|-----------------------------------------|:---------:|
| CRUD completo de productos              | Completo  |
| CRUD completo de categorías             | Completo  |
| Búsqueda con filtros y paginación       | Completo  |
| Protección JWT de operaciones admin     | Completo  |
| Documentación Swagger UI                | Completo  |
| Persistencia relacional (H2 / Postgres) | Completo  |
| Manejo de errores de negocio            | Completo  |
| Validaciones de entrada                 | Completo  |
| Integración con frontend (CORS)         | Pendiente |

---

### ¿Qué funcionalidades faltan para resolver completamente el problema planteado?

1. **CORS no configurado** — ningún frontend en dominio diferente puede consumir la API. Es el bloqueador más inmediato para la integración real.

2. **Gestión de múltiples administradores** — solo existe el usuario `admin` hardcodeado. Una tienda real tiene múltiples empleados con credenciales propias. Requiere entidad `User` persistida en BD.

3. **Control de stock/inventario** — el catálogo muestra qué artículos existen pero no cuántas unidades hay disponibles. Falta el campo `stock` en `Product` y la lógica de "artículo agotado".

4. **Imágenes de productos** — un catálogo visual necesita fotos de los artículos. Requiere integración con almacenamiento de archivos.

5. **Historial de cambios de precio** — para auditoría y análisis de negocio, sería valioso registrar cuándo cambió el precio de un artículo y su valor anterior.

6. **Búsqueda avanzada** — la búsqueda actual es por nombre parcial y categoría. Un catálogo profesional permitiría filtrar por rango de precio (`price BETWEEN ? AND ?`), fecha de ingreso y ordenamiento personalizado.

7. **Gestión de pedidos** — el catálogo permite ver productos pero no comprarlos. Un sistema completo de tienda requeriría carrito de compras, órdenes y procesamiento de pagos.

8. **Refresh token** — el token expira en 24h sin mecanismo de renovación. El administrador debe re-autenticarse manualmente al expirar.

**Para el alcance académico del proyecto, el sistema está completo y demuestra correctamente los principios de diseño solicitados: arquitectura hexagonal, SOLID, DDD, patrones de diseño y separación de responsabilidades en un contexto de negocio real y comprensible.**

---

*Documento generado el 2026-05-29 basado en análisis exhaustivo del código fuente real de `catalog-demo`. Cada justificación técnica fue verificada contra el código fuente de los 30 archivos Java del proyecto.*
