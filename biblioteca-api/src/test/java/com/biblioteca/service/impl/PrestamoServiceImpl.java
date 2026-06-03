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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceImplTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private EjemplarRepository ejemplarRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PrestamoServiceImpl prestamoService;

    @Test
    void crearPrestamo_debeCambiarEjemplarAPrestado() {
        PrestamoRequest request = new PrestamoRequest("user-1", "ejemplar-1", LocalDate.of(2026, 6, 1));
        Ejemplar ejemplar = new Ejemplar("ejemplar-1", "libro-1", "INV-001", EstadoEjemplar.DISPONIBLE);

        when(usuarioRepository.existsById("user-1")).thenReturn(true);
        when(ejemplarRepository.findById("ejemplar-1")).thenReturn(Optional.of(ejemplar));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(invocation -> {
            Prestamo p = invocation.getArgument(0);
            p.setId("prestamo-1");
            return p;
        });

        PrestamoResponse response = prestamoService.crearPrestamo(request);

        assertEquals("prestamo-1", response.getId());
        assertEquals(EstadoPrestamo.ACTIVO, response.getEstado());
        assertEquals(EstadoEjemplar.PRESTADO, ejemplar.getEstado());
        verify(ejemplarRepository).save(ejemplar);
    }

    @Test
    void crearPrestamo_debeFallarSiEjemplarNoDisponible() {
        PrestamoRequest request = new PrestamoRequest("user-1", "ejemplar-1", LocalDate.of(2026, 6, 1));
        Ejemplar ejemplar = new Ejemplar("ejemplar-1", "libro-1", "INV-001", EstadoEjemplar.PRESTADO);

        when(usuarioRepository.existsById("user-1")).thenReturn(true);
        when(ejemplarRepository.findById("ejemplar-1")).thenReturn(Optional.of(ejemplar));

        assertThrows(ResponseStatusException.class, () -> prestamoService.crearPrestamo(request));
    }

    @Test
    void registrarDevolucion_debeCambiarEstadoPrestamoYEjemplar() {
        Prestamo prestamo = new Prestamo(
                "prestamo-1",
                "user-1",
                "ejemplar-1",
                LocalDate.of(2026, 5, 19),
                LocalDate.of(2026, 6, 1),
                EstadoPrestamo.ACTIVO
        );
        Ejemplar ejemplar = new Ejemplar("ejemplar-1", "libro-1", "INV-001", EstadoEjemplar.PRESTADO);

        when(prestamoRepository.findById("prestamo-1")).thenReturn(Optional.of(prestamo));
        when(ejemplarRepository.findById("ejemplar-1")).thenReturn(Optional.of(ejemplar));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PrestamoResponse response = prestamoService.registrarDevolucion("prestamo-1");

        assertEquals(EstadoPrestamo.DEVUELTO, response.getEstado());
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplar.getEstado());
        verify(ejemplarRepository).save(ejemplar);
    }
}

