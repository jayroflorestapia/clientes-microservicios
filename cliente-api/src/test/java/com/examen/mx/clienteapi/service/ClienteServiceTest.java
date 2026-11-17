package com.examen.mx.clienteapi.service;

import com.examen.mx.clienteapi.cliente.ClienteDatosClient;
import com.examen.mx.clienteapi.dto.ClienteRequest;
import com.examen.mx.clienteapi.dto.ClienteResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteDatosClient clienteDatosClient;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void crear_delegaEnClienteDatosYDevuelveElClienteCreado() {
        ClienteRequest request = new ClienteRequest(
                "Ana", "Pérez", "López", LocalDate.of(1990, 5, 20),
                "F", "ana@example.com", "5512345678");
        ClienteResponse esperado = new ClienteResponse(
                42L, "Ana", "Pérez", "López", LocalDate.of(1990, 5, 20),
                "F", "ana@example.com", "5512345678");
        when(clienteDatosClient.crear(request)).thenReturn(esperado);

        ClienteResponse resultado = clienteService.crear(request);

        assertEquals(esperado, resultado);
        verify(clienteDatosClient).crear(request);
    }
}
