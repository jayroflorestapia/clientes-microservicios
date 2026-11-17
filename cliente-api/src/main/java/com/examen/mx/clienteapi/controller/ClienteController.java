package com.examen.mx.clienteapi.controller;

import com.examen.mx.clienteapi.dto.ClienteCreadoResponse;
import com.examen.mx.clienteapi.dto.ClienteEliminadoResponse;
import com.examen.mx.clienteapi.dto.ClienteRequest;
import com.examen.mx.clienteapi.dto.ClienteResponse;
import com.examen.mx.clienteapi.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClienteCreadoResponse> crear(
            @Valid @RequestBody ClienteRequest request
    ) {
        ClienteResponse creado = service.crear(request);

        URI location = URI.create("/api/v1/clientes/" + creado.id());

        return ResponseEntity
                .created(location)
                .body(new ClienteCreadoResponse(creado.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> consultar(
            @PathVariable Long id
    ) {
        validarId(id);
        return ResponseEntity.ok(service.consultar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request
    ) {
        validarId(id);
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ClienteEliminadoResponse> eliminar(
            @PathVariable Long id
    ) {
        validarId(id);
        return ResponseEntity.ok(service.eliminar(id));
    }

    private void validarId(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("El id debe ser mayor que cero");
        }
    }
}
