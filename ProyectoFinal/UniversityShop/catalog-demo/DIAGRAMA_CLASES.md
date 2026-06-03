# Diagrama de Clases de Dominio — University Shop (Catálogo)

> Proyecto: catalog-demo · Spring Boot + MongoDB  
> Generado para: Visual Paradigm  
> Capa cubierta: Dominio + DTOs de negocio relevantes + Excepciones de dominio

---

## Entidades de Dominio

---

### Producto

> `@Document(collection = "products")` — Aggregate Root del Bounded Context Catálogo

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | id | String |
| - | sku | String |
| - | nombre | String |
| - | precio | BigDecimal |
| - | fechaCreacion | LocalDateTime |
| - | categoriaId | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | getId() | String |
| + | setId(id) | void |
| + | getSku() | String |
| + | setSku(sku) | void |
| + | getName() | String |
| + | setName(name) | void |
| + | getPrice() | BigDecimal |
| + | setPrice(price) | void |
| + | getDateCreated() | LocalDateTime |
| + | setDateCreated(d) | void |
| + | getCategoryId() | String |
| + | setCategoryId(categoryId) | void |

---

### Categoría

> `@Document(collection = "categories")`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | id | String |
| - | nombre | String |
| - | descripcion | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | getId() | String |
| + | setId(id) | void |
| + | getName() | String |
| + | setName(name) | void |
| + | getDescription() | String |
| + | setDescription(desc) | void |

---

## DTOs de Negocio

---

### RespuestaProducto

> `ProductResponseDTO` — DTO de salida que embebe la categoría asociada

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | id | String |
| - | sku | String |
| - | nombre | String |
| - | precio | BigDecimal |
| - | fechaCreacion | LocalDateTime |
| - | categoria | RespuestaCategoría |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaProducto() | — |
| + | RespuestaProducto(id, sku, nombre, precio, fechaCreacion, categoria) | — |
| + | getId() | String |
| + | getSku() | String |
| + | getName() | String |
| + | getPrice() | BigDecimal |
| + | getDateCreated() | LocalDateTime |
| + | getCategory() | RespuestaCategoría |

---

### RespuestaCategoría

> `CategoryResponseDTO` — DTO de salida; también embebido en RespuestaProducto

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | id | String |
| - | nombre | String |
| - | descripcion | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaCategoría() | — |
| + | RespuestaCategoría(id, nombre, descripcion) | — |
| + | getId() | String |
| + | getName() | String |
| + | getDescription() | String |

---

### RespuestaPaginada\<T\>

> `PagedResponseDTO<T>` — Wrapper genérico para respuestas paginadas

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | contenido | List\<T\> |
| - | pagina | int |
| - | tamaño | int |
| - | totalElementos | long |
| - | totalPaginas | int |
| - | primero | boolean |
| - | ultimo | boolean |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaPaginada(contenido, pagina, tamaño, totalElementos, totalPaginas, primero, ultimo) | — |
| + | getContent() | List\<T\> |
| + | getPage() | int |
| + | getSize() | int |
| + | getTotalElements() | long |
| + | getTotalPages() | int |
| + | isFirst() | boolean |
| + | isLast() | boolean |

---

### SolicitudAutenticacion

> `AuthRequestDTO` — Datos de login enviados por el cliente

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | usuario | String |
| - | contrasena | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | getUsername() | String |
| + | setUsername(username) | void |
| + | getPassword() | String |
| + | setPassword(password) | void |

---

### RespuestaAutenticacion

> `AuthResponseDTO` — Token JWT devuelto tras autenticación exitosa

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | token | String |
| - | tipo | String |
| - | mensaje | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaAutenticacion(token) | — |
| + | getToken() | String |
| + | getType() | String |
| + | getMessage() | String |

---

### RespuestaError

> `ApiErrorResponse` — `record` Java; estructura unificada de respuesta de error de la API

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | timestamp | String |
| - | estado | int |
| - | error | String |
| - | mensaje | String |
| - | ruta | String |
| - | erroresCampo | List\<DetalleErrorCampo\> |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaError(timestamp, status, error, message, path, fieldErrors) | — |
| + | timestamp() | String |
| + | status() | int |
| + | error() | String |
| + | message() | String |
| + | path() | String |
| + | fieldErrors() | List\<DetalleErrorCampo\> |
| + «static» | of(status, error, message, path) | RespuestaError |
| + «static» | ofValidation(message, path, fieldErrors) | RespuestaError |

---

### DetalleErrorCampo

> `ApiErrorResponse.FieldErrorDetail` — `record` anidado; describe un campo inválido en errores 400

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | campo | String |
| - | mensaje | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | DetalleErrorCampo(field, message) | — |
| + | field() | String |
| + | message() | String |

---

## Excepciones de Dominio

---

### CategoriaTieneProductos

> `CategoryHasProductsException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | categoriaId | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | CategoriaTieneProductos(categoryId) | — |
| + | getCategoryId() | String |

---

### NombreCategoriaExistente

> `CategoryNameAlreadyExistsException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | nombre | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | NombreCategoriaExistente(name) | — |
| + | getName() | String |

---

### CategoriaNoEncontrada

> `CategoryNotFoundException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | categoriaId | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | CategoriaNoEncontrada(categoryId) | — |
| + | getCategoryId() | String |

---

### ProductoNoEncontrado

> `ProductNotFoundException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | productoId | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | ProductoNoEncontrado(productId) | — |
| + | getProductId() | String |

---

### SkuYaExiste

> `SkuAlreadyExistsException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | sku | String |

**Métodos:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | SkuYaExiste(sku) | — |
| + | getSku() | String |

---

## Relaciones

| Origen | Tipo de relación | Destino | Multiplicidad | Descripción |
|---|---|---|---|---|
| Producto | Asociación (→) | Categoría | N : 1 | `categoriaId` referencia el `id` de Categoría por clave |
| RespuestaProducto | Composición (◆) | RespuestaCategoría | 1 | La respuesta del producto embebe los datos completos de la categoría |
| RespuestaPaginada | Agregación (◇) | RespuestaProducto | 0..* | Una página contiene una lista de respuestas de producto |
| RespuestaError | Composición (◆) | DetalleErrorCampo | 0..* | Un error de validación puede tener múltiples detalles de campo |
| CategoriaTieneProductos | Herencia (▷) | RuntimeException | — | Excepción de dominio no verificada |
| NombreCategoriaExistente | Herencia (▷) | RuntimeException | — | Excepción de dominio no verificada |
| CategoriaNoEncontrada | Herencia (▷) | RuntimeException | — | Excepción de dominio no verificada |
| ProductoNoEncontrado | Herencia (▷) | RuntimeException | — | Excepción de dominio no verificada |
| SkuYaExiste | Herencia (▷) | RuntimeException | — | Excepción de dominio no verificada |

---

## Notas para Visual Paradigm

### Colores de fondo por tipo de clase
| Tipo | Color | Hex |
|---|---|---|
| Entidad de dominio (`@Document`) | Azul claro | `#A8D5F0` |
| DTO de negocio | Azul claro | `#A8D5F0` |
| Enumeración (`«enumeration»`) | Verde claro | `#A8F0D5` |
| Excepción de dominio | Naranja claro | `#FFD7AA` |
| Todas las clases — borde | Azul medio | `#5B9BD5` |

### Disposición recomendada
```
                    ┌──────────────────────────────────────────────────┐
                    │               ENTIDADES CENTRALES                │
                    │                                                  │
                    │    [Categoría]  ◄────────  [Producto]            │
                    │                                                  │
                    └──────────────────────────────────────────────────┘
                         ▲                           ▲
                         │                           │
               ┌─────────┴──────────┐   ┌───────────┴──────────────┐
               │     DTOs           │   │       DTOs               │
               │  [RespuestaCategoría]  │  [RespuestaProducto] ◆──► [RespuestaCategoría]
               │                    │   │  [RespuestaPaginada]      │
               └────────────────────┘   └──────────────────────────┘

   ┌─────────────────────────────────────────────────────────────────────────┐
   │                    EXCEPCIONES DE DOMINIO                               │
   │  [RuntimeException]                                                     │
   │       ▲         ▲           ▲              ▲           ▲               │
   │  [CatTiene]  [NombreCat] [CatNoEnc]  [ProdNoEnc]  [SkuYaEx]           │
   └─────────────────────────────────────────────────────────────────────────┘

   ┌────────────────────────────────────────┐
   │   AUTH + ERROR (esquina derecha)       │
   │  [SolicitudAuth]  [RespuestaAuth]      │
   │  [RespuestaError] ◆──► [DetalleError]  │
   └────────────────────────────────────────┘
```

### Conteo total de clases: 14
| # | Clase en español | Clase en código | Tipo |
|---|---|---|---|
| 1 | Producto | `Product` | Entidad dominio |
| 2 | Categoría | `Category` | Entidad dominio |
| 3 | RespuestaProducto | `ProductResponseDTO` | DTO negocio |
| 4 | RespuestaCategoría | `CategoryResponseDTO` | DTO negocio |
| 5 | RespuestaPaginada | `PagedResponseDTO<T>` | DTO negocio |
| 6 | SolicitudAutenticacion | `AuthRequestDTO` | DTO negocio |
| 7 | RespuestaAutenticacion | `AuthResponseDTO` | DTO negocio |
| 8 | RespuestaError | `ApiErrorResponse` | DTO negocio (record) |
| 9 | DetalleErrorCampo | `FieldErrorDetail` | DTO negocio (record) |
| 10 | CategoriaTieneProductos | `CategoryHasProductsException` | Excepción dominio |
| 11 | NombreCategoriaExistente | `CategoryNameAlreadyExistsException` | Excepción dominio |
| 12 | CategoriaNoEncontrada | `CategoryNotFoundException` | Excepción dominio |
| 13 | ProductoNoEncontrado | `ProductNotFoundException` | Excepción dominio |
| 14 | SkuYaExiste | `SkuAlreadyExistsException` | Excepción dominio |