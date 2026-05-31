package com.university.shop.application.dto;

import java.util.List;

/**
 * Wrapper genérico para respuestas paginadas.
 *
 * Cuando hay muchos productos, no se devuelven todos de una vez.
 * Este DTO envuelve una "página" de resultados con metadatos de
 * navegación que el frontend usa para renderizar la paginación.
 *
 * Ejemplo de JSON:
 * {
 *   "content": [ {...}, {...} ],
 *   "page": 0,
 *   "size": 10,
 *   "totalElements": 47,
 *   "totalPages": 5,
 *   "last": false
 * }
 */
public class PagedResponseDTO<T> {

    /** Lista de elementos en la página actual. */
    private List<T> content;

    /** Número de página actual (empieza en 0). */
    private int page;

    /** Cantidad de elementos por página. */
    private int size;

    /** Total de elementos en toda la base de datos. */
    private long totalElements;

    /** Total de páginas disponibles. */
    private int totalPages;

    /** true si esta es la primera página. */
    private boolean first;

    /** true si esta es la última página. */
    private boolean last;

    public PagedResponseDTO(List<T> content, int page, int size,
                             long totalElements, int totalPages,
                             boolean first, boolean last) {
        this.content       = content;
        this.page          = page;
        this.size          = size;
        this.totalElements = totalElements;
        this.totalPages    = totalPages;
        this.first         = first;
        this.last          = last;
    }

    // ── Getters ────────────────────────────────────────────────────
    public List<T> getContent()      { return content; }
    public int getPage()             { return page; }
    public int getSize()             { return size; }
    public long getTotalElements()   { return totalElements; }
    public int getTotalPages()       { return totalPages; }
    public boolean isFirst()         { return first; }
    public boolean isLast()          { return last; }
}
