package com.biblioteca.controller;

import com.biblioteca.dto.EjemplarRequest;
import com.biblioteca.dto.EjemplarResponse;
import com.biblioteca.service.EjemplarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ejemplares")
public class EjemplarController {

    private final EjemplarService ejemplarService;

    public EjemplarController(EjemplarService ejemplarService) {
        this.ejemplarService = ejemplarService;
    }

    @PostMapping
    public ResponseEntity<EjemplarResponse> crearEjemplar(@RequestBody EjemplarRequest request) {
        EjemplarResponse response = ejemplarService.crearEjemplar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EjemplarResponse>> listarEjemplares() {
        return ResponseEntity.ok(ejemplarService.listarEjemplares());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjemplarResponse> consultarEjemplar(@PathVariable String id) {
        return ResponseEntity.ok(ejemplarService.consultarEjemplar(id));
    }
}

