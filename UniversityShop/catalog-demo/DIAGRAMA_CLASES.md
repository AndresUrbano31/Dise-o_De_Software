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

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| *(sin constructores explícitos — Spring Data instancia vía reflección)* | | |

---

### Categoría

> `@Document(collection = "categories")`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | id | String |
| - | nombre | String |
| - | descripcion | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| *(sin constructores explícitos)* | | |

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

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaProducto(id, sku, nombre, precio, fechaCreacion, categoria) | — |

---

### RespuestaCategoría

> `CategoryResponseDTO` — DTO de salida; también embebido en RespuestaProducto

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | id | String |
| - | nombre | String |
| - | descripcion | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaCategoría(id, nombre, descripcion) | — |

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

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaPaginada(contenido, pagina, tamaño, totalElementos, totalPaginas, primero, ultimo) | — |

---

### SolicitudAutenticacion

> `AuthRequestDTO` — Datos de login enviados por el cliente

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | usuario | String |
| - | contrasena | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| *(solo getters/setters)* | | |

---

### RespuestaAutenticacion

> `AuthResponseDTO` — Token JWT devuelto tras autenticación exitosa

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | token | String |
| - | tipo | String |
| - | mensaje | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | RespuestaAutenticacion(token) | — |

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

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | of(estado, error, mensaje, ruta) | RespuestaError |
| + | ofValidacion(mensaje, ruta, erroresCampo) | RespuestaError |

---

### DetalleErrorCampo

> `ApiErrorResponse.FieldErrorDetail` — `record` anidado; describe un campo inválido en errores 400

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | campo | String |
| - | mensaje | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| *(record — constructor canónico implícito)* | | |

---

## Excepciones de Dominio

---

### CategoriaTieneProductos

> `CategoryHasProductsException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | categoriaId | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | CategoriaTieneProductos(categoriaId) | — |

---

### NombreCategoriaExistente

> `CategoryNameAlreadyExistsException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | nombre | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | NombreCategoriaExistente(nombre) | — |

---

### CategoriaNoEncontrada

> `CategoryNotFoundException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | categoriaId | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | CategoriaNoEncontrada(categoriaId) | — |

---

### ProductoNoEncontrado

> `ProductNotFoundException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | productoId | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | ProductoNoEncontrado(productoId) | — |

---

### SkuYaExiste

> `SkuAlreadyExistsException extends RuntimeException`

**Atributos:**
| Visibilidad | Nombre | Tipo |
|---|---|---|
| - | sku | String |

**Métodos principales:**
| Visibilidad | Firma | Retorno |
|---|---|---|
| + | SkuYaExiste(sku) | — |

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