# DIAGRAMAS UML — catalog-demo

> **Fuente de verdad:** este documento fue generado leyendo el código fuente real del proyecto.
> **Fecha de generación:** 2026-05-30
> **Versión del artefacto:** 3.0.0 (pom.xml)

---

## SECCIÓN 0 — Introducción

El proyecto `catalog-demo` implementa la **Plataforma Catalog Demo**, el catálogo digital de la tienda universitaria (`university.shop`). Cualquier miembro de la comunidad universitaria puede consultar productos y categorías sin autenticarse; solo el administrador de la tienda accede con credenciales (JWT) para crear, editar o eliminar registros.

**Stack tecnológico detectado en `pom.xml` y `application.yml`:**

| Componente | Versión |
|---|---|
| Spring Boot | 3.2.5 |
| Java | 17 (compilación) / 26 (ejecución actual) |
| Spring Data MongoDB | incluido en Spring Boot 3.2.5 |
| MongoDB Atlas | cluster `biblioteca-cluster` (M0, región AWS) |
| Spring Security | 6.2.4 |
| JJWT (JSON Web Tokens) | 0.12.3 |
| Springdoc OpenAPI 3 / Swagger UI | 2.5.0 |
| Swagger UI (webjars) | 5.13.0 |
| Bean Validation (Hibernate Validator) | 8.0.1.Final |

**Instrucción para el lector:** cada sección de este documento contiene la información estructurada necesaria para construir el diagrama correspondiente en Visual Paradigm Community Edition. Las cajas, flechas y estereotipos descritos aquí deben replicarse fielmente usando el estilo UML de referencia: clases en cajas azul claro (`#A8D5F0`), estereotipos `<<...>>` en cursiva encima del nombre, herencia con flecha de triángulo blanco abierto, dependencia con línea punteada y flecha abierta.

---

## SECCIÓN 1 — Diagrama de Contexto

**Propósito:** mostrar el sistema como caja negra y los actores externos que interactúan con él.
**Tipo de diagrama en Visual Paradigm:** Use Case Diagram (sin casos de uso internos, solo actores y sistema).

---

### 1.1 Sistema central

| Campo | Valor |
|---|---|
| Nombre | `Plataforma Catalog Demo` |
| Nombre técnico (`spring.application.name`) | `product-catalog-demo` |
| Forma | Óvalo azul (`#A8D5F0`) |
| Descripción | API REST para gestión del catálogo digital de la tienda universitaria. Permite administrar productos y categorías con autenticación JWT. Endpoints de lectura públicos; escritura requiere Bearer token. |

---

### 1.2 Actores externos

Rectángulos rosados (`#E8C5E0`) con el nombre del actor en negrita.

#### Actor 1 — Administrador

- **Nombre:** Administrador
- **Descripción:** Personal de la tienda universitaria responsable del inventario digital.
- **Evidencia en el código:** `SecurityConfig.java` protege `POST/PUT/DELETE` en `/api/v1/products/**` y `/api/v1/categories/**` con `anyRequest().authenticated()`. El único usuario en memoria es `admin / admin123` con rol `ROLE_ADMIN` (definido en `SecurityConfig.userDetailsService()`).

#### Actor 2 — Cliente / Estudiante

- **Nombre:** Cliente / Estudiante
- **Descripción:** Miembro de la comunidad universitaria que consulta el catálogo sin autenticarse.
- **Evidencia en el código:** `SecurityConfig.java` declara `.requestMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/categories/**").permitAll()`. Lectura pública confirmada.

#### Actor 3 — MongoDB Atlas

- **Nombre:** MongoDB Atlas
- **Descripción:** Base de datos documental en la nube donde se persisten categorías y productos.
- **Evidencia en el código:** dependencia `spring-boot-starter-data-mongodb` en `pom.xml`. URI `mongodb+srv://...@biblioteca-cluster.xm2z9kq.mongodb.net/catalog_demo` en `application.yml`.

#### Actor 4 — Frontend (frontend_shopizer) *(actor inferido)*

- **Nombre:** SPA Frontend
- **Descripción:** Aplicación React corriendo en `http://localhost:5173`. Consume esta API vía HTTP. Documentado en `docs/frontend-integration/`.
- **Nota:** este actor es externo al repositorio `catalog-demo` pero es el consumidor principal de la API.

---

### 1.3 Flujos (flechas etiquetadas)

| Actor | Dir | Sistema | Etiqueta |
|---|---|---|---|
| Administrador | → | Plataforma Catalog Demo | POST/PUT/DELETE con JWT — gestión de categorías y productos |
| Plataforma Catalog Demo | → | Administrador | Confirmaciones (201/200/204) y errores estructurados (4xx/5xx) |
| Cliente / Estudiante | → | Plataforma Catalog Demo | GET — consulta del catálogo (sin token) |
| Plataforma Catalog Demo | → | Cliente / Estudiante | Catálogo paginado / detalle de producto |
| SPA Frontend | → | Plataforma Catalog Demo | Peticiones HTTP (axios, Bearer JWT cuando aplica) |
| Plataforma Catalog Demo | ↔ | MongoDB Atlas | Persistencia de documentos (TCP/IP sobre TLS, puerto 27017) |

---

### 1.4 Notas para Visual Paradigm

- Tipo: **Use Case Diagram** (sin casos de uso — solo sistema + actores + flujos).
- Sistema: óvalo central (`#A8D5F0`), etiqueta centrada.
- Actores: rectángulos fuera del óvalo (`#E8C5E0`), uno a cada lado.
- Flechas: líneas con punta de flecha abierta, etiqueta de texto en el medio.
- MongoDB Atlas: dibujarlo abajo del sistema, como actor externo de infraestructura.

---

## SECCIÓN 2 — Diagrama de Despliegue

**Propósito:** topología física del sistema — dispositivos, entornos de ejecución, artefactos y conexiones reales.
**Tipo de diagrama en Visual Paradigm:** Deployment Diagram.

---

### 2.1 Nodos (de afuera hacia adentro)

#### Nodo 1 — Dispositivo del Usuario

```
<<device>> Dispositivo del Usuario
  └─ <<executionEnvironment>> Navegador Web / Móvil
       └─ <<artifact>> SPA frontend_shopizer
            Tecnología: React 18 + Vite + TypeScript
            URL: http://localhost:5173 (desarrollo)
```

#### Nodo 2 — Servidor de Aplicación (Local / CI)

```
<<device>> Máquina del Desarrollador / Servidor
  └─ <<executionEnvironment>> JVM 17+ (Java 17 mínimo, Java 26 observado)
       └─ <<executionEnvironment>> Spring Boot 3.2.5 (Tomcat 10.1.20 embebido)
            └─ <<artifact>> shop-catalog-demo-3.0.0.jar
                 Contiene:
                 ├─ <<component>> API REST (controllers + filtros)
                 │    Rutas base: /api/v1/**
                 │    Puerto: 8080
                 ├─ <<component>> Spring Security + JWT Filter
                 │    Filtro: JwtAuthenticationFilter
                 │    Algoritmo JWT: HS256
                 ├─ <<component>> Swagger UI / OpenAPI 3
                 │    URL: /swagger-ui.html
                 │    Spec: /v3/api-docs
                 └─ <<component>> Spring Data MongoDB Repositories
                      Driver: mongodb-driver-sync 4.11.2
```

#### Nodo 3 — MongoDB Atlas (Nube)

```
<<cloud>> MongoDB Atlas — AWS São Paulo (sa-east-1)
  └─ <<executionEnvironment>> Cluster biblioteca-cluster (M0 Free Tier)
       └─ <<database>> Base de datos: catalog_demo
            Colecciones:
            ├─ categories  (documentos de tipo Category)
            └─ products    (documentos de tipo Product)
            Índices únicos:
            ├─ categories.name  (único)
            └─ products.sku     (único)
```

---

### 2.2 Conexiones entre nodos

| Origen | → | Destino | Protocolo | Notas |
|---|---|---|---|---|
| Navegador Web / Móvil | → | Spring Boot (puerto 8080) | HTTP/HTTPS | `Authorization: Bearer <JWT>` en endpoints protegidos. CORS habilitado para `http://localhost:5173` y `http://localhost:3000`. |
| Spring Boot (Tomcat) | → | MongoDB Atlas | TCP/IP sobre TLS (puerto 27017) | Driver sync: `mongodb-driver-sync 4.11.2`. Autenticación con usuario y contraseña en la URI `mongodb+srv`. |

---

### 2.3 Notas para Visual Paradigm

- Tipo: **Deployment Diagram**.
- Cajas 3D apiladas: los nodos anidados se dibujan dentro del nodo padre (no como cajas sueltas).
- Estereotipos `<< >>` en cursiva encima de cada caja.
- Conexiones entre nodos: línea continua gruesa con etiqueta de protocolo.
- MongoDB Atlas puede dibujarse con el icono de nube (disponible en Visual Paradigm).

---

## SECCIÓN 3 — Diagrama de Clases

**Propósito:** representar todas las clases del sistema con atributos, métodos y relaciones.
**Tipo de diagrama en Visual Paradigm:** Class Diagram.

---

### 3.1 Inventario de clases por paquete

---

#### `Category` — `com.university.shop.domain`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring / MongoDB:** `@Document(collection = "categories")`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:**
| Visibilidad | Nombre | Tipo | Restricción / Anotación |
|---|---|---|---|
| - | id | `String` | `@Id` (ObjectId de MongoDB) |
| - | name | `String` | `@Indexed(unique = true)` |
| - | description | `String` | — |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | getId() | `String` |
| + | setId(String id) | `void` |
| + | getName() | `String` |
| + | setName(String name) | `void` |
| + | getDescription() | `String` |
| + | setDescription(String desc) | `void` |

**Relaciones:**
- Es referenciada por `Product` mediante el campo `categoryId: String` (asociación lógica por ID).
- Gestionada por `CategoryRepository`.

---

#### `Product` — `com.university.shop.domain`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring / MongoDB:** `@Document(collection = "products")`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:**
| Visibilidad | Nombre | Tipo | Restricción / Anotación |
|---|---|---|---|
| - | id | `String` | `@Id` (ObjectId de MongoDB) |
| - | sku | `String` | `@Indexed(unique = true)` |
| - | name | `String` | — |
| - | price | `BigDecimal` | — |
| - | dateCreated | `LocalDateTime` | `@CreatedDate` (asignado automáticamente por `@EnableMongoAuditing`) |
| - | categoryId | `String` | `@Indexed` (referencia a `Category._id`) |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | getId() | `String` |
| + | setId(String id) | `void` |
| + | getSku() | `String` |
| + | setSku(String sku) | `void` |
| + | getName() | `String` |
| + | setName(String name) | `void` |
| + | getPrice() | `BigDecimal` |
| + | setPrice(BigDecimal price) | `void` |
| + | getDateCreated() | `LocalDateTime` |
| + | setDateCreated(LocalDateTime d) | `void` |
| + | getCategoryId() | `String` |
| + | setCategoryId(String categoryId) | `void` |

**Relaciones:**
- Asociación lógica (por clave) con `Category`: `categoryId: String` referencia `Category.id`.
- Gestionado por `ProductRepository`.

---

#### `CategoryRepository` — `com.university.shop.infrastructure`

**Estereotipo / Tipo Java:** `interface`
**Anotaciones Spring:** `@Repository`
**Hereda de:** `MongoRepository<Category, String>` (Spring Data MongoDB)
**Implementa:** —

**Métodos declarados (adicionales a los heredados):**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | existsByName(String name) | `boolean` |
| + | existsByNameAndIdNot(String name, String id) | `boolean` |

**Métodos heredados de `MongoRepository` (relevantes):**
| Firma | Retorno |
|---|---|
| save(Category entity) | `Category` |
| findById(String id) | `Optional<Category>` |
| findAll() | `List<Category>` |
| deleteById(String id) | `void` |
| existsById(String id) | `boolean` |

**Relaciones:**
- Gestiona entidades de tipo `Category`.

---

#### `ProductRepository` — `com.university.shop.infrastructure`

**Estereotipo / Tipo Java:** `interface`
**Anotaciones Spring:** `@Repository`
**Hereda de:** `MongoRepository<Product, String>` (Spring Data MongoDB)
**Implementa:** —

**Métodos declarados (adicionales a los heredados):**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | existsBySku(String sku) | `boolean` |
| + | findByNameContainingIgnoreCase(String name, Pageable pageable) | `Page<Product>` |
| + | findByCategoryId(String categoryId, Pageable pageable) | `Page<Product>` |
| + | findByNameContainingIgnoreCaseAndCategoryId(String name, String categoryId, Pageable pageable) | `Page<Product>` |
| + | countByCategoryId(String categoryId) | `long` |

**Métodos heredados de `MongoRepository` (relevantes):**
| Firma | Retorno |
|---|---|
| save(Product entity) | `Product` |
| findById(String id) | `Optional<Product>` |
| findAll(Pageable pageable) | `Page<Product>` |
| deleteById(String id) | `void` |
| existsById(String id) | `boolean` |

---

#### `CategoryService` — `com.university.shop.application.port`

**Estereotipo / Tipo Java:** `interface` (puerto de entrada — Hexagonal Architecture)
**Anotaciones Spring:** ninguna
**Hereda de:** ninguna

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | createCategory(CategoryRequestDTO request) | `CategoryResponseDTO` |
| + | getAllCategories() | `List<CategoryResponseDTO>` |
| + | getCategoryById(String id) | `CategoryResponseDTO` |
| + | updateCategory(String id, CategoryRequestDTO request) | `CategoryResponseDTO` |
| + | deleteCategory(String id) | `void` |

**Excepciones declaradas:**
- `getCategoryById`, `updateCategory`, `deleteCategory` → `CategoryNotFoundException`
- `updateCategory` → `CategoryNameAlreadyExistsException`
- `deleteCategory` → `CategoryHasProductsException`

---

#### `ProductService` — `com.university.shop.application.port`

**Estereotipo / Tipo Java:** `interface` (puerto de entrada)
**Anotaciones Spring:** ninguna
**Hereda de:** ninguna

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | createProduct(ProductRequestDTO request) | `ProductResponseDTO` |
| + | getAllProducts() | `List<ProductResponseDTO>` |
| + | searchProducts(String name, String categoryId, int page, int size) | `PagedResponseDTO<ProductResponseDTO>` |
| + | getProductById(String id) | `ProductResponseDTO` |
| + | updateProduct(String id, ProductUpdateDTO request) | `ProductResponseDTO` |
| + | deleteProduct(String id) | `void` |

---

#### `CategoryServiceImpl` — `com.university.shop.infrastructure.service`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@Service`
**Hereda de:** ninguna
**Implementa:** `CategoryService`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | categoryRepository | `CategoryRepository` |
| - | productRepository | `ProductRepository` |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | createCategory(CategoryRequestDTO request) | `CategoryResponseDTO` |
| + | getAllCategories() | `List<CategoryResponseDTO>` |
| + | getCategoryById(String id) | `CategoryResponseDTO` |
| + | updateCategory(String id, CategoryRequestDTO request) | `CategoryResponseDTO` |
| + | deleteCategory(String id) | `void` |
| - | toResponseDTO(Category category) | `CategoryResponseDTO` |

**Relaciones:**
- Implementa `CategoryService` (flecha de realización — triángulo blanco con línea punteada).
- Depende de `CategoryRepository` (inyección por constructor).
- Depende de `ProductRepository` (para `countByCategoryId` en `deleteCategory`).

---

#### `ProductServiceImpl` — `com.university.shop.infrastructure.service`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@Service`
**Hereda de:** ninguna
**Implementa:** `ProductService`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | productRepository | `ProductRepository` |
| - | categoryRepository | `CategoryRepository` |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | createProduct(ProductRequestDTO request) | `ProductResponseDTO` |
| + | getAllProducts() | `List<ProductResponseDTO>` |
| + | searchProducts(String name, String categoryId, int page, int size) | `PagedResponseDTO<ProductResponseDTO>` |
| + | getProductById(String id) | `ProductResponseDTO` |
| + | updateProduct(String id, ProductUpdateDTO request) | `ProductResponseDTO` |
| + | deleteProduct(String id) | `void` |
| - | validateCategoryExists(String categoryId) | `void` |
| - | toResponseDTO(Product product) | `ProductResponseDTO` |

**Relaciones:**
- Implementa `ProductService`.
- Depende de `ProductRepository`.
- Depende de `CategoryRepository` (para validar existencia de categoría y para el mapeo en `toResponseDTO`).

---

#### `AuthController` — `com.university.shop.api.controller`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@RestController`, `@RequestMapping("/api/v1/auth")`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | authManager | `AuthenticationManager` |
| - | jwtService | `JwtService` |

**Métodos:**
| Visibilidad | Firma HTTP | Retorno |
|---|---|---|
| + `@PostMapping("/login")` | login(@Valid @RequestBody AuthRequestDTO request) | `ResponseEntity<AuthResponseDTO>` |

**Relaciones:**
- Depende de `AuthRequestDTO` (entrada).
- Depende de `AuthResponseDTO` (salida).
- Depende de `JwtService` (generación del token).
- Depende de `AuthenticationManager` (Spring Security — no es clase del proyecto).

---

#### `CategoryController` — `com.university.shop.api.controller`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@RestController`, `@RequestMapping("/api/v1/categories")`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | categoryService | `CategoryService` |

**Métodos:**
| Visibilidad | Firma HTTP | Retorno |
|---|---|---|
| + `@PostMapping` | createCategory(@Valid @RequestBody CategoryRequestDTO) | `ResponseEntity<CategoryResponseDTO>` |
| + `@GetMapping` | getAllCategories() | `ResponseEntity<List<CategoryResponseDTO>>` |
| + `@GetMapping("/{id}")` | getCategoryById(@PathVariable String id) | `ResponseEntity<CategoryResponseDTO>` |
| + `@PutMapping("/{id}")` | updateCategory(@PathVariable String id, @Valid @RequestBody CategoryRequestDTO) | `ResponseEntity<CategoryResponseDTO>` |
| + `@DeleteMapping("/{id}")` | deleteCategory(@PathVariable String id) | `ResponseEntity<Void>` |

**Relaciones:**
- Depende de `CategoryService` (interfaz — nunca de la impl directa).
- Usa `CategoryRequestDTO` y `CategoryResponseDTO`.

---

#### `ProductController` — `com.university.shop.api.controller`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@RestController`, `@RequestMapping("/api/v1/products")`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | productService | `ProductService` |

**Métodos:**
| Visibilidad | Firma HTTP | Retorno |
|---|---|---|
| + `@GetMapping` | searchProducts(String name, String categoryId, int page, int size) | `ResponseEntity<PagedResponseDTO<ProductResponseDTO>>` |
| + `@GetMapping("/all")` | getAllProducts() | `ResponseEntity<List<ProductResponseDTO>>` |
| + `@GetMapping("/{id}")` | getProductById(@PathVariable String id) | `ResponseEntity<ProductResponseDTO>` |
| + `@PostMapping` | createProduct(@Valid @RequestBody ProductRequestDTO) | `ResponseEntity<ProductResponseDTO>` |
| + `@PutMapping("/{id}")` | updateProduct(@PathVariable String id, @Valid @RequestBody ProductUpdateDTO) | `ResponseEntity<ProductResponseDTO>` |
| + `@DeleteMapping("/{id}")` | deleteProduct(@PathVariable String id) | `ResponseEntity<Void>` |

**Relaciones:**
- Depende de `ProductService` (interfaz).
- Usa `ProductRequestDTO`, `ProductUpdateDTO`, `ProductResponseDTO`, `PagedResponseDTO`.

---

#### `HealthController` — `com.university.shop.api.controller`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@RestController`, `@RequestMapping("/api/v1/health")`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:** ninguno

**Métodos:**
| Visibilidad | Firma HTTP | Retorno |
|---|---|---|
| + `@GetMapping` | health() | `ResponseEntity<Map<String, String>>` |

**Notas:** devuelve `{ "status": "UP", "service": "catalog-demo", "timestamp": "<ISO8601>" }`. Endpoint público sin autenticación.

---

#### `JwtService` — `com.university.shop.infrastructure.security`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@Service`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:**
| Visibilidad | Nombre | Tipo | Restricción |
|---|---|---|---|
| - | signingKey | `SecretKey` | Calculado de `jwt.secret` (HS256, ≥ 32 bytes UTF-8) |
| - | expirationMs | `long` | Leído de `jwt.expiration-ms` (default 86400000 ms = 24h) |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | generateToken(String username) | `String` |
| + | isValid(String token) | `boolean` |
| + | extractUsername(String token) | `String` |

---

#### `JwtAuthenticationFilter` — `com.university.shop.infrastructure.security`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@Component`
**Hereda de:** `OncePerRequestFilter` (Spring Security)
**Implementa:** —

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | jwtService | `JwtService` |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| # (protected) | doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain) | `void` |

**Lógica:** lee el header `Authorization: Bearer <token>`, valida con `JwtService`, y si es válido registra la autenticación en `SecurityContextHolder` con rol `ROLE_ADMIN`.

---

#### `GlobalExceptionHandler` — `com.university.shop.api.exception`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@RestControllerAdvice`
**Hereda de:** ninguna
**Implementa:** ninguna

**Métodos:**
| Visibilidad | Firma (manejador) | Retorno | HTTP |
|---|---|---|---|
| + | handleNotFound(RuntimeException ex, HttpServletRequest req) | `ResponseEntity<ApiErrorResponse>` | 404 |
| + | handleConflict(RuntimeException ex, HttpServletRequest req) | `ResponseEntity<ApiErrorResponse>` | 409 |
| + | handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) | `ResponseEntity<ApiErrorResponse>` | 400 |
| + | handleUnauthorized(Exception ex, HttpServletRequest req) | `ResponseEntity<ApiErrorResponse>` | 401 |
| + | handleForbidden(AccessDeniedException ex, HttpServletRequest req) | `ResponseEntity<ApiErrorResponse>` | 403 |
| + | handleGeneric(Exception ex, HttpServletRequest req) | `ResponseEntity<ApiErrorResponse>` | 500 |

**Relaciones:**
- Produce `ApiErrorResponse` en todos los casos.

---

#### `SecurityConfig` — `com.university.shop.api.config`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@Configuration`, `@EnableWebSecurity`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | jwtFilter | `JwtAuthenticationFilter` |
| - | corsConfigurationSource | `CorsConfigurationSource` |

**Métodos (`@Bean`):**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | securityFilterChain(HttpSecurity http) | `SecurityFilterChain` |
| + | authenticationManager(AuthenticationConfiguration config) | `AuthenticationManager` |
| + | userDetailsService(PasswordEncoder encoder) | `UserDetailsService` |
| + | passwordEncoder() | `PasswordEncoder` |

**Reglas de acceso declaradas:**
- `GET /api/v1/products/**` y `GET /api/v1/categories/**` → `permitAll()`
- `/api/v1/auth/**`, `/api/v1/health`, `/swagger-ui.html`, `/swagger-ui/**`, `/api-docs/**`, `/v3/api-docs/**` → `permitAll()`
- Cualquier otro request → `authenticated()`
- `AuthenticationEntryPoint` personalizado: responde `401` con JSON `ApiErrorResponse` cuando no hay token.

---

#### `CorsConfig` — `com.university.shop.api.config`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@Configuration`, `@ConfigurationProperties(prefix = "app.cors")`
**Hereda de:** ninguna
**Implementa:** ninguna

**Atributos:**
| Visibilidad | Nombre | Tipo | Valor por defecto |
|---|---|---|---|
| - | allowedOrigins | `List<String>` | `[http://localhost:5173, http://localhost:3000]` |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | corsConfigurationSource() `@Bean` | `CorsConfigurationSource` |
| + | getAllowedOrigins() | `List<String>` |
| + | setAllowedOrigins(List<String>) | `void` |

**Configuración CORS resultante:**
- Métodos: `GET, POST, PUT, PATCH, DELETE, OPTIONS`
- Headers: `*`
- Headers expuestos: `Authorization, Location, Content-Disposition`
- `allowCredentials`: false
- `maxAge`: 3600 s
- Patrón: `/api/**`

---

#### `OpenApiConfig` — `com.university.shop.api.config`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@Configuration`
**Hereda de:** ninguna
**Implementa:** ninguna

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | catalogOpenAPI() `@Bean` | `OpenAPI` |

**Configura:**
- Título: `Catalog API` v1.0.0
- Servidores: `http://localhost:8080` (dev), `https://api.catalog-demo.example.com` (prod placeholder)
- Esquema de seguridad: `bearerAuth` (HTTP Bearer JWT)

---

#### `MongoConfig` — `com.university.shop.infrastructure.config`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@Configuration`, `@EnableMongoAuditing`
**Hereda de:** ninguna
**Implementa:** ninguna

**Propósito:** habilita la auditoría de MongoDB para que `@CreatedDate` en `Product.dateCreated` se asigne automáticamente al insertar el documento.
**Métodos:** ninguno (solo activación vía anotación).

---

#### `CatalogApplication` — `com.university.shop`

**Estereotipo / Tipo Java:** `class`
**Anotaciones Spring:** `@SpringBootApplication`, `@ConfigurationPropertiesScan`
**Hereda de:** ninguna
**Implementa:** ninguna

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + `static` | main(String[] args) | `void` |

---

### DTOs — `com.university.shop.application.dto`

---

#### `AuthRequestDTO`

**Tipo Java:** `class` | **Anotaciones:** ninguna de Spring (Bean Validation sí)

**Atributos:**
| Visibilidad | Nombre | Tipo | Restricción |
|---|---|---|---|
| - | username | `String` | `@NotBlank` |
| - | password | `String` | `@NotBlank` |

**Métodos:** getters y setters estándar.

---

#### `AuthResponseDTO`

**Tipo Java:** `class`

**Atributos:**
| Visibilidad | Nombre | Tipo | Valor |
|---|---|---|---|
| - `final` | token | `String` | Recibido en constructor |
| - `final` | type | `String` | `"Bearer"` (constante de clase) |
| - `final` | message | `String` | `"Autenticación exitosa..."` (constante de clase) |

**Métodos:** getters. No tiene setters (inmutable).

---

#### `CategoryRequestDTO`

**Tipo Java:** `class`

**Atributos:**
| Visibilidad | Nombre | Tipo | Restricción |
|---|---|---|---|
| - | name | `String` | `@NotBlank`, `@Size(max = 100)` |
| - | description | `String` | `@Size(max = 500)` (opcional) |

**Métodos:** getters y setters.

---

#### `CategoryResponseDTO`

**Tipo Java:** `class` (DTO de salida — solo getters)

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | id | `String` |
| - | name | `String` |
| - | description | `String` |

**Métodos:** getters. No tiene setters públicos para campos de negocio.

---

#### `ProductRequestDTO`

**Tipo Java:** `class`

**Atributos:**
| Visibilidad | Nombre | Tipo | Restricción |
|---|---|---|---|
| - | sku | `String` | `@NotBlank`, `@Size(max = 100)` |
| - | name | `String` | `@NotBlank`, `@Size(max = 255)` |
| - | price | `BigDecimal` | `@NotNull`, `@DecimalMin("0.01")`, `@Digits(integer=10, fraction=2)` |
| - | categoryId | `String` | `@NotBlank` |

**Métodos:** getters y setters.

---

#### `ProductResponseDTO`

**Tipo Java:** `class` (DTO de salida — solo getters)

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | id | `String` |
| - | sku | `String` |
| - | name | `String` |
| - | price | `BigDecimal` |
| - | dateCreated | `LocalDateTime` |
| - | category | `CategoryResponseDTO` |

**Métodos:** getters. Constructor completo.

**Relaciones:**
- Composición con `CategoryResponseDTO` (campo embebido `category`).

---

#### `ProductUpdateDTO`

**Tipo Java:** `class`

**Atributos:**
| Visibilidad | Nombre | Tipo | Restricción |
|---|---|---|---|
| - | name | `String` | `@NotBlank`, `@Size(max = 255)` |
| - | price | `BigDecimal` | `@NotNull`, `@DecimalMin("0.01")`, `@Digits(integer=10, fraction=2)` |
| - | categoryId | `String` | `@NotBlank` |

**Nota:** el campo `sku` NO está incluido — es inmutable por diseño de negocio.

**Métodos:** getters y setters.

---

#### `PagedResponseDTO<T>`

**Tipo Java:** `class` genérica

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | content | `List<T>` |
| - | page | `int` |
| - | size | `int` |
| - | totalElements | `long` |
| - | totalPages | `int` |
| - | first | `boolean` |
| - | last | `boolean` |

**Métodos:** constructor completo + getters. No tiene setters.

---

#### `ApiErrorResponse` — `com.university.shop.api.exception`

**Tipo Java:** `record`
**Anotaciones:** `@JsonInclude(JsonInclude.Include.NON_NULL)`

**Componentes del record:**
| Nombre | Tipo |
|---|---|
| timestamp | `String` (ISO 8601) |
| status | `int` (código HTTP) |
| error | `String` |
| message | `String` |
| path | `String` |
| fieldErrors | `List<FieldErrorDetail>` (nullable, omitido si null) |

**Record interno:** `FieldErrorDetail(String field, String message)`

**Métodos de fábrica estáticos:**
| Firma | Propósito |
|---|---|
| `of(int status, String error, String message, String path)` | Errores sin fieldErrors |
| `ofValidation(String message, String path, List<FieldErrorDetail>)` | Errores de validación con fieldErrors |

---

### Excepciones de dominio — `com.university.shop.domain.exception`

Todas extienden `RuntimeException` (excepciones no chequeadas).

| Clase | Campo extra | Mensaje generado | HTTP resultante |
|---|---|---|---|
| `CategoryNotFoundException` | `String categoryId` (final) | `"La categoría con ID {id} no fue encontrada."` | 404 |
| `CategoryNameAlreadyExistsException` | `String name` (final) | `"Ya existe una categoría con el nombre '{name}'."` | 409 |
| `CategoryHasProductsException` | `String categoryId` (final) | `"La categoría con id {id} tiene productos asociados..."` | 409 |
| `ProductNotFoundException` | `String productId` (final) | `"El producto con ID {id} no fue encontrado."` | 404 |
| `SkuAlreadyExistsException` | `String sku` (final) | `"El SKU '{sku}' ya está registrado en el catálogo."` | 409 |

Cada excepción tiene: constructor con el campo, `super(mensaje)`, y getter del campo.

---

### 3.2 Tabla resumen de relaciones (vista global)

| Origen | Tipo de relación | Destino | Multiplicidad | Notas |
|---|---|---|---|---|
| `Product` | Asociación lógica (campo) | `Category` | `*..1` | `categoryId: String` referencia `Category.id` — no es FK de JPA, es referencia por clave en MongoDB |
| `ProductResponseDTO` | Composición | `CategoryResponseDTO` | `1..1` | Campo `category: CategoryResponseDTO` embebido |
| `CategoryServiceImpl` | Realización | `CategoryService` | — | Flecha triángulo blanco + línea punteada |
| `ProductServiceImpl` | Realización | `ProductService` | — | Flecha triángulo blanco + línea punteada |
| `CategoryServiceImpl` | Dependencia | `CategoryRepository` | — | Inyección por constructor |
| `CategoryServiceImpl` | Dependencia | `ProductRepository` | — | Usada para `countByCategoryId` en `deleteCategory` |
| `ProductServiceImpl` | Dependencia | `ProductRepository` | — | Inyección por constructor |
| `ProductServiceImpl` | Dependencia | `CategoryRepository` | — | Para validar categoría + mapear DTO |
| `CategoryController` | Dependencia | `CategoryService` | — | Solo conoce la interfaz |
| `ProductController` | Dependencia | `ProductService` | — | Solo conoce la interfaz |
| `AuthController` | Dependencia | `JwtService` | — | Para generar token |
| `JwtAuthenticationFilter` | Dependencia | `JwtService` | — | Para validar token |
| `JwtAuthenticationFilter` | Herencia | `OncePerRequestFilter` | — | Clase de Spring Security (externa al proyecto) |
| `SecurityConfig` | Dependencia | `JwtAuthenticationFilter` | — | Añade el filtro a la cadena |
| `SecurityConfig` | Dependencia | `CorsConfigurationSource` | — | Inyectado como bean de `CorsConfig` |
| `GlobalExceptionHandler` | Producción | `ApiErrorResponse` | — | Construye instancias del record en todos los handlers |
| `CategoryRepository` | Herencia | `MongoRepository<Category, String>` | — | Interfaz de Spring Data (externa) |
| `ProductRepository` | Herencia | `MongoRepository<Product, String>` | — | Interfaz de Spring Data (externa) |
| Todas las excepciones | Herencia | `RuntimeException` | — | Tipo `extends` (clase de Java estándar) |

---

### 3.3 Notas para Visual Paradigm

- Tipo: **Class Diagram**.
- Tres compartimentos por caja: nombre (negrita), atributos (`- nombre: Tipo`), métodos (`+ método(): Retorno`).
- Estereotipos `<<interface>>`, `<<record>>`, `<<exception>>` encima del nombre cuando aplique.
- Flecha de herencia: triángulo blanco abierto apuntando a la clase padre.
- Flecha de realización (implementa interfaz): triángulo blanco con línea punteada apuntando a la interfaz.
- Asociación: línea continua con multiplicidad en los extremos.
- Dependencia (uses): línea punteada con flecha abierta.
- Composición: línea con rombo negro en el lado del "dueño".
- Sugerencia: dividir en tres sub-vistas si el diagrama completo se vuelve denso:
  1. **Sub-vista Dominio:** `Category`, `Product`, las 5 excepciones.
  2. **Sub-vista Servicios:** las 2 interfaces + las 2 implementaciones + los 2 repositorios.
  3. **Sub-vista API:** los 4 controllers + los 8 DTOs + `GlobalExceptionHandler`.

---

## SECCIÓN 4 — Diagrama de Componentes

**Propósito:** mostrar los módulos lógicos del sistema, sus interfaces expuestas y sus dependencias.
**Tipo de diagrama en Visual Paradigm:** Component Diagram.

---

### 4.1 Componentes lógicos

#### C0 — `<<component>> SPA Frontend (frontend_shopizer)` *(externo)*

- **Tecnología:** React 18 + Vite + TypeScript.
- **URL:** `http://localhost:5173` (desarrollo).
- **Rol:** consumidor de esta API. No pertenece al repositorio `catalog-demo`.

---

#### C1 — `<<component>> API Gateway / Seguridad`

- **Clases que lo forman:** `SecurityConfig`, `CorsConfig`, `JwtAuthenticationFilter`, `GlobalExceptionHandler`.
- **Responsabilidad:** punto de entrada HTTP. Valida CORS, verifica el JWT, enruta al módulo correcto, captura y transforma excepciones en JSON uniforme.
- **Interfaces expuestas:**
  - `HTTP /api/v1/**` (todas las rutas de la API)
  - `HTTP /swagger-ui.html` y `/v3/api-docs` (documentación)
  - `HTTP /api/v1/health` (health check)

---

#### C2 — `<<component>> Módulo de Autenticación`

- **Clases:** `AuthController`, `JwtService`.
- **Responsabilidad:** emite tokens JWT para el administrador. Verifica credenciales a través de `AuthenticationManager` de Spring Security.
- **Interfaces expuestas:** `POST /api/v1/auth/login`.
- **Dependencias:** `AuthenticationManager` (Spring Security), `JwtService`.

---

#### C3 — `<<component>> Módulo de Categorías`

- **Clases:** `CategoryController`, `CategoryService` (interfaz), `CategoryServiceImpl`.
- **Responsabilidad:** CRUD completo de categorías. GET público; POST/PUT/DELETE protegido con JWT.
- **Interfaces expuestas:**
  - `GET /api/v1/categories` — lista todas
  - `GET /api/v1/categories/{id}` — una por ID
  - `POST /api/v1/categories` — crear (requiere JWT)
  - `PUT /api/v1/categories/{id}` — actualizar (requiere JWT)
  - `DELETE /api/v1/categories/{id}` — eliminar (requiere JWT)
- **Puerto (lollipop):** `CategoryService` con métodos `createCategory`, `getAllCategories`, `getCategoryById`, `updateCategory`, `deleteCategory`.

---

#### C4 — `<<component>> Módulo de Productos`

- **Clases:** `ProductController`, `ProductService` (interfaz), `ProductServiceImpl`.
- **Responsabilidad:** CRUD completo de productos con paginación y búsqueda por nombre y categoría.
- **Interfaces expuestas:**
  - `GET /api/v1/products` — búsqueda paginada (público)
  - `GET /api/v1/products/all` — sin paginación (público)
  - `GET /api/v1/products/{id}` — detalle (público)
  - `POST /api/v1/products` — crear (requiere JWT)
  - `PUT /api/v1/products/{id}` — actualizar (requiere JWT)
  - `DELETE /api/v1/products/{id}` — eliminar (requiere JWT)
- **Puerto (lollipop):** `ProductService` con métodos `createProduct`, `getAllProducts`, `searchProducts`, `getProductById`, `updateProduct`, `deleteProduct`.

---

#### C5 — `<<component>> Capa de Persistencia`

- **Clases:** `CategoryRepository`, `ProductRepository`, `MongoConfig`.
- **Responsabilidad:** acceso a MongoDB Atlas. Spring Data MongoDB genera la implementación de los repositorios en tiempo de ejecución.
- **Interfaces expuestas (hacia los servicios):** `CategoryRepository`, `ProductRepository`.

---

#### C6 — `<<component>> MongoDB Atlas` *(externo)*

- **Responsabilidad:** persistencia durable de categorías y productos.
- **Base de datos:** `catalog_demo` con colecciones `categories` y `products`.

---

#### C7 — `<<component>> OpenAPI / Swagger UI`

- **Clases:** `OpenApiConfig`.
- **Responsabilidad:** documentación interactiva de la API.
- **Interfaces expuestas:** `GET /swagger-ui.html`, `GET /v3/api-docs`.

---

### 4.2 Interfaces expuestas (lollipops en el diagrama)

| Componente | Puerto / Interface | Métodos |
|---|---|---|
| C1 — API Gateway | `HTTP /api/v1/**` | Todas las rutas listadas arriba |
| C3 — Módulo Categorías | `CategoryService` | `createCategory`, `getAllCategories`, `getCategoryById`, `updateCategory`, `deleteCategory` |
| C4 — Módulo Productos | `ProductService` | `createProduct`, `getAllProducts`, `searchProducts`, `getProductById`, `updateProduct`, `deleteProduct` |
| C5 — Persistencia | `CategoryRepository` | CRUD + `existsByName`, `existsByNameAndIdNot` |
| C5 — Persistencia | `ProductRepository` | CRUD + `existsBySku`, búsquedas paginadas, `countByCategoryId` |

---

### 4.3 Conexiones entre componentes

| De | → | A | Naturaleza | Etiqueta / Protocolo |
|---|---|---|---|---|
| SPA Frontend (C0) | → | API Gateway (C1) | HTTP | Peticiones axios; `Authorization: Bearer <JWT>` para operaciones de escritura |
| API Gateway (C1) | → | Módulo Autenticación (C2) | Ruteo HTTP | `POST /api/v1/auth/login` |
| API Gateway (C1) | → | Módulo Categorías (C3) | Ruteo HTTP + validación JWT | `/api/v1/categories/**` |
| API Gateway (C1) | → | Módulo Productos (C4) | Ruteo HTTP + validación JWT | `/api/v1/products/**` |
| API Gateway (C1) | → | OpenAPI/Swagger (C7) | Publicación docs | `/swagger-ui.html`, `/v3/api-docs` |
| Módulo Categorías (C3) | → | Persistencia (C5) | Dependencia Java | `CategoryRepository` (CRUD) |
| Módulo Productos (C4) | → | Persistencia (C5) | Dependencia Java | `ProductRepository` + `CategoryRepository` (validación) |
| Persistencia (C5) | → | MongoDB Atlas (C6) | TCP/IP TLS 27017 | Spring Data MongoDB Driver 4.11.2 |

---

### 4.4 Notas para Visual Paradigm

- Tipo: **Component Diagram**.
- Cada componente: rectángulo con el icono UML de componente (dos rectángulos pequeños sobresaliendo en la esquina superior derecha).
- Estereotipo `<<component>>` en cursiva encima.
- Interfaces como lollipops (círculo + línea recta saliendo del componente).
- Los componentes externos (C0 y C6) se dibujan fuera del rectángulo principal del sistema.
- Dependencias entre componentes: flechas punteadas con punta abierta.
- Las rutas HTTP se pueden anotar como notas adjuntas a las flechas.

---

## SECCIÓN 5 — Diagrama de Desarrollo (Arquitectura por Capas)

**Propósito:** mostrar la arquitectura hexagonal del backend con TODAS las clases organizadas por capa. Estilo: 5 marcos rectangulares grandes apilados verticalmente.
**Tipo de diagrama en Visual Paradigm:** Package Diagram o Class Diagram con paquetes.

---

### 5.1 Capa API (Controllers + Configuración)

**Paquete Java:** `com.university.shop.api`
**Color de marco sugerido:** `#A8D5F0` (azul claro)
**Etiqueta del marco:** `Capa API — Adaptadores de Entrada HTTP`

Clases en este marco:

| Clase | Estereotipo | Responsabilidad de negocio |
|---|---|---|
| `AuthController` | `<<RestController>>` | Puerta de acceso administrativa: emite tokens JWT al administrador. |
| `CategoryController` | `<<RestController>>` | Vitrina de las secciones del catálogo: exposición HTTP de categorías. |
| `ProductController` | `<<RestController>>` | Ventana principal del catálogo: exposición HTTP del inventario. |
| `HealthController` | `<<RestController>>` | Verificación de disponibilidad del servicio para el frontend. |
| `GlobalExceptionHandler` | `<<RestControllerAdvice>>` | Traductor de errores de dominio a respuestas HTTP comprensibles. |
| `SecurityConfig` | `<<Configuration>>` | Política de acceso: quién puede ver el catálogo y quién puede tocarlo. |
| `CorsConfig` | `<<Configuration>>` | Permiso de origen cruzado: qué frontends pueden consumir la API. |
| `OpenApiConfig` | `<<Configuration>>` | Portal de documentación interactiva de la API. |

---

### 5.2 Capa DTO (Contratos de datos)

**Paquetes Java:** `com.university.shop.application.dto` y `com.university.shop.api.exception`
**Color de marco sugerido:** `#D5E8F0` (azul muy claro)
**Etiqueta del marco:** `DTOs — Contratos de Datos de Entrada y Salida`

| Clase | Dirección | Uso |
|---|---|---|
| `AuthRequestDTO` | Entrada | Login del administrador: usuario y contraseña. |
| `AuthResponseDTO` | Salida | Token JWT + tipo + mensaje de instrucción. |
| `CategoryRequestDTO` | Entrada | Crear o actualizar una categoría (nombre + descripción). |
| `CategoryResponseDTO` | Salida | Datos de una categoría que devuelve la API. Embebido en `ProductResponseDTO`. |
| `ProductRequestDTO` | Entrada | Registrar un nuevo producto (SKU, nombre, precio, categoría). |
| `ProductResponseDTO` | Salida | Datos completos de un producto incluyendo categoría embebida. |
| `ProductUpdateDTO` | Entrada | Actualizar producto: nombre, precio, categoría. Sin SKU (inmutable). |
| `PagedResponseDTO<T>` | Salida | Envuelve cualquier lista con metadatos de paginación. |
| `ApiErrorResponse` | Salida | Estructura uniforme de error: timestamp, status, error, message, path, fieldErrors. |

---

### 5.3 Capa Application / Service (Puertos y Casos de Uso)

**Paquetes Java:** `com.university.shop.application.port` e `com.university.shop.infrastructure.service` + `com.university.shop.infrastructure.security`
**Color de marco sugerido:** `#F0E8A8` (amarillo claro)
**Etiqueta del marco:** `Capa Application — Puertos y Casos de Uso`

**Interfaces (Puertos de entrada):**

| Interfaz | Estereotipo | Casos de uso que declara |
|---|---|---|
| `CategoryService` | `<<interface>>` | `createCategory`, `getAllCategories`, `getCategoryById`, `updateCategory`, `deleteCategory` |
| `ProductService` | `<<interface>>` | `createProduct`, `getAllProducts`, `searchProducts`, `getProductById`, `updateProduct`, `deleteProduct` |

**Implementaciones (Adaptadores de salida hacia repositorios):**

| Clase | Estereotipo | Implementa | Resumen de lógica |
|---|---|---|---|
| `CategoryServiceImpl` | `<<Service>>` | `CategoryService` | Valida unicidad de nombre, protege borrado si hay productos, mapea a/desde DTOs. |
| `ProductServiceImpl` | `<<Service>>` | `ProductService` | Valida unicidad de SKU, valida categoría existente, búsqueda dinámica por filtros, mapeo con categoría embebida. |

**Servicios de infraestructura de seguridad:**

| Clase | Estereotipo | Responsabilidad |
|---|---|---|
| `JwtService` | `<<Service>>` | Genera tokens JWT firmados con HS256 y los valida/parsea. |
| `JwtAuthenticationFilter` | `<<Component>>` | Intercepta cada petición HTTP, extrae y valida el JWT, registra la autenticación en el SecurityContext. Extiende `OncePerRequestFilter`. |

---

### 5.4 Capa Repository (Infraestructura de Persistencia)

**Paquete Java:** `com.university.shop.infrastructure` + `com.university.shop.infrastructure.config`
**Color de marco sugerido:** `#E8A8D5` (rosado claro)
**Etiqueta del marco:** `Capa Infrastructure — Repositorios (Adaptadores de Salida)`

| Interfaz | Hereda de | Métodos propios |
|---|---|---|
| `CategoryRepository` | `MongoRepository<Category, String>` | `existsByName(String)`, `existsByNameAndIdNot(String, String)` |
| `ProductRepository` | `MongoRepository<Product, String>` | `existsBySku(String)`, `findByNameContainingIgnoreCase(String, Pageable)`, `findByCategoryId(String, Pageable)`, `findByNameContainingIgnoreCaseAndCategoryId(String, String, Pageable)`, `countByCategoryId(String)` |

**Configuración de infraestructura:**

| Clase | Estereotipo | Propósito |
|---|---|---|
| `MongoConfig` | `<<Configuration>>` | Habilita `@EnableMongoAuditing` para que `@CreatedDate` en `Product.dateCreated` se asigne automáticamente. |

---

### 5.5 Capa Domain (Modelo de Negocio)

**Paquete Java:** `com.university.shop.domain` y `com.university.shop.domain.exception`
**Color de marco sugerido:** `#A8F0D5` (verde claro)
**Etiqueta del marco:** `Capa Domain — Entidades y Reglas de Negocio`

**Entidades (documentos MongoDB):**

| Clase | Colección | Atributos clave |
|---|---|---|
| `Category` | `categories` | `id: String` (@Id), `name: String` (@Indexed unique), `description: String` |
| `Product` | `products` | `id: String` (@Id), `sku: String` (@Indexed unique), `name: String`, `price: BigDecimal`, `dateCreated: LocalDateTime` (@CreatedDate), `categoryId: String` (@Indexed) |

**Excepciones de dominio (todas extienden `RuntimeException`):**

| Clase | Campo | Cuándo se lanza |
|---|---|---|
| `CategoryNotFoundException` | `categoryId: String` | Al buscar, actualizar o eliminar una categoría que no existe. |
| `CategoryNameAlreadyExistsException` | `name: String` | Al crear o actualizar una categoría con nombre ya registrado. |
| `CategoryHasProductsException` | `categoryId: String` | Al intentar eliminar una categoría que aún tiene productos asociados. |
| `ProductNotFoundException` | `productId: String` | Al buscar, actualizar o eliminar un producto que no existe. |
| `SkuAlreadyExistsException` | `sku: String` | Al crear un producto con SKU ya registrado en el catálogo. |

---

### 5.6 Dependencias permitidas entre capas

Las flechas en el diagrama de capas SOLO apuntan hacia abajo (capas superiores dependen de inferiores, nunca al revés).

| De (capa superior) | → | A (capa inferior) | Permitido | Notas |
|---|---|---|---|---|
| API (Controllers) | → | DTOs | Sí | Los controllers reciben DTOs de entrada y retornan DTOs de salida. |
| API (Controllers) | → | Application (interfaces) | Sí | Los controllers dependen de `CategoryService` y `ProductService` (interfaces, no impls). |
| DTOs | → | Domain | No | Los DTOs son independientes del dominio por diseño. |
| Application (interfaces) | → | DTOs | Sí | Las firmas de las interfaces usan DTOs. |
| Application (ServiceImpl) | → | Repository | Sí | Las implementaciones usan repositorios para persistir. |
| Application (ServiceImpl) | → | Domain | Sí | Las impls instancian entidades y lanzan excepciones de dominio. |
| Repository | → | Domain | Sí | Los repositorios gestionan entidades. |
| Domain | → | (ninguna capa) | — | El dominio es independiente de todo lo externo. |

---

### 5.7 Notas para Visual Paradigm

- Tipo: **Package Diagram** (o Class Diagram con paquetes como marcos rectangulares).
- Cinco marcos rectangulares grandes apilados de arriba hacia abajo: API → DTO → Application → Repository → Domain.
- Etiqueta en la parte superior de cada marco (nombre de la capa).
- Dentro de cada marco: las clases listadas en las subsecciones anteriores.
- Flechas entre capas: solo hacia abajo, líneas punteadas con punta abierta.
- Las flechas NO se dibujan entre clases individuales en este diagrama — se dibujan entre los marcos de capa.
- Excepción: si Visual Paradigm soporta "nested packages", puedes usar paquetes reales de Java como sub-marcos dentro de cada capa.

---

## APÉNDICE — Configuración para Visual Paradigm Community Edition

### Estilo de colores sugerido (consistente con proyecto de referencia)

| Elemento | Color de fondo | Color de borde |
|---|---|---|
| Clases / Interfaces | `#A8D5F0` (azul claro) | `#5B9BD5` |
| Actores (diagrama de contexto) | `#E8C5E0` (rosado) | `#B56BAE` |
| Excepciones de dominio | `#FFD7AA` (naranja claro) | `#D48A00` |
| Componentes externos | `#D3D3D3` (gris claro) | `#808080` |
| Nodos de despliegue | `#E8F5E9` (verde claro) | `#388E3C` |
| Capa Domain | `#A8F0D5` | `#2E8B57` |
| Capa Repository | `#E8A8D5` | `#9B2F7A` |
| Capa Application | `#F0E8A8` | `#9B8A2F` |
| Capa DTO | `#D5E8F0` | `#5B9BD5` |
| Capa API | `#A8D5F0` | `#5B9BD5` |

### Orden de construcción recomendado

1. **Diagrama de Contexto** (~10 elementos, ~20 min): el más simple. Un óvalo, 4 actores, flechas etiquetadas.
2. **Diagrama de Despliegue** (~3 nodos principales, ~30 min): 3 nodos anidados, 2 conexiones.
3. **Diagrama de Componentes** (~8 componentes, ~45 min): componentes + lollipops + flechas entre ellos.
4. **Diagrama de Desarrollo** (~5 marcos, ~60 min): clases distribuidas en 5 capas.
5. **Diagrama de Clases** (~30 clases, ~2-3 horas): el más denso — usar las 3 sub-vistas sugeridas en la Sección 3.3.
