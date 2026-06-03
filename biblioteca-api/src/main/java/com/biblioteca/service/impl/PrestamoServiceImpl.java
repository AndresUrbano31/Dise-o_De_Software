package com.biblioteca.service.impl;

import com.biblioteca.dto.PrestamoRequest;
import com.biblioteca.dto.PrestamoResponse;
import com.biblioteca.model.Ejemplar;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.enums.EstadoEjemplar;
import com.biblioteca.model.enums.EstadoPrestamo;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.PrestamoRepository;
import com.biblioteca.repository.UsuarioRepository;
import com.biblioteca.service.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final UsuarioRepository usuarioRepository;

    public PrestamoServiceImpl(
            PrestamoRepository prestamoRepository,
            EjemplarRepository ejemplarRepository,
            UsuarioRepository usuarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public PrestamoResponse crearPrestamo(PrestamoRequest request) {
        if (!usuarioRepository.existsById(request.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + request.getUsuarioId());
        }

        Ejemplar ejemplar = ejemplarRepository.findById(request.getEjemplarId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejemplar no encontrado con id: " + request.getEjemplarId()));

        if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El ejemplar no esta disponible para prestamo");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuarioId(request.getUsuarioId());
        prestamo.setEjemplarId(request.getEjemplarId());
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionEsperada(request.getFechaDevolucionEsperada());
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);

        ejemplar.setEstado(EstadoEjemplar.PRESTADO);
        ejemplarRepository.save(ejemplar);

        return mapToResponse(prestamoGuardado);
    }

    @Override
    @Transactional
    public PrestamoResponse registrarDevolucion(String prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestamo no encontrado con id: " + prestamoId));

        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El prestamo ya fue devuelto o no esta activo");
        }

        Ejemplar ejemplar = ejemplarRepository.findById(prestamo.getEjemplarId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejemplar no encontrado con id: " + prestamo.getEjemplarId()));

        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        Prestamo prestamoActualizado = prestamoRepository.save(prestamo);

        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
        ejemplarRepository.save(ejemplar);

        return mapToResponse(prestamoActualizado);
    }

    @Override
    public PrestamoResponse consultarPrestamo(String prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestamo no encontrado con id: " + prestamoId));
        return mapToResponse(prestamo);
    }

    @Override
    public List<PrestamoResponse> listarPrestamos() {
        return prestamoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PrestamoResponse mapToResponse(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getUsuarioId(),
                prestamo.getEjemplarId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionEsperada(),
                prestamo.getEstado()
        );
    }
}

