package com.biblioteca.dto;

import com.biblioteca.model.enums.EstadoEjemplar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EjemplarResponse {

    private String id;
    private String libroId;
    private String codigoInventario;
    private EstadoEjemplar estado;
}

