package com.biblioteca.service.impl;

import com.biblioteca.dto.EjemplarRequest;
import com.biblioteca.dto.EjemplarResponse;
import com.biblioteca.model.Ejemplar;
import com.biblioteca.model.enums.EstadoEjemplar;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.service.EjemplarService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EjemplarServiceImpl implements EjemplarService {

    private final EjemplarRepository ejemplarRepository;

    public EjemplarServiceImpl(EjemplarRepository ejemplarRepository) {
        this.ejemplarRepository = ejemplarRepository;
    }

    @Override
    public EjemplarResponse crearEjemplar(EjemplarRequest request) {
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setLibroId(request.getLibroId());
        ejemplar.setCodigoInventario(request.getCodigoInventario());
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);

        Ejemplar ejemplarGuardado = ejemplarRepository.save(ejemplar);
        return mapToResponse(ejemplarGuardado);
    }

    @Override
    public EjemplarResponse consultarEjemplar(String id) {
        Ejemplar ejemplar = ejemplarRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejemplar no encontrado con id: " + id));
        return mapToResponse(ejemplar);
    }

    @Override
    public List<EjemplarResponse> listarEjemplares() {
        return ejemplarRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EjemplarResponse mapToResponse(Ejemplar ejemplar) {
        return new EjemplarResponse(
                ejemplar.getId(),
                ejemplar.getLibroId(),
                ejemplar.getCodigoInventario(),
                ejemplar.getEstado()
        );
    }
}

