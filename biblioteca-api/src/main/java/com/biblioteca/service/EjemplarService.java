package com.biblioteca.service;

import com.biblioteca.dto.EjemplarRequest;
import com.biblioteca.dto.EjemplarResponse;

import java.util.List;

public interface EjemplarService {

    EjemplarResponse crearEjemplar(EjemplarRequest request);

    EjemplarResponse consultarEjemplar(String id);

    List<EjemplarResponse> listarEjemplares();
}

