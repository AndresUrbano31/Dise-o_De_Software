package com.biblioteca.model;

import com.biblioteca.model.enums.EstadoEjemplar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ejemplares")
public class Ejemplar {

    @Id
    private String id;

    private String libroId;
    private String codigoInventario;
    private EstadoEjemplar estado;
}

