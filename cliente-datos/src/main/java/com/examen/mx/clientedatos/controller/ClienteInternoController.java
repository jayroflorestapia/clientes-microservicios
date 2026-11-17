package com.examen.mx.clientedatos.controller;


import com.examen.mx.clientedatos.dto.ClienteEliminadoResponse;
import com.examen.mx.clientedatos.dto.ClienteRequest;
import com.examen.mx.clientedatos.dto.ClienteResponse;
import com.examen.mx.clientedatos.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/clientes")
public class ClienteInternoController {

    private final ClienteService service;

    public ClienteInternoController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody ClienteRequest request) {
        return service.crear(request);
    }

    @GetMapping("/{id}")
    public ClienteResponse consultar(@PathVariable Long id) {
        return service.consultar(id);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request
    ) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ClienteEliminadoResponse eliminar(@PathVariable Long id) {
        return service.eliminar(id);
    }
}
