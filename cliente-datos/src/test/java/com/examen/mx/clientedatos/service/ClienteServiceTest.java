package com.examen.mx.clientedatos.service;

import com.examen.mx.clientedatos.dto.ClienteRequest;
import com.examen.mx.clientedatos.dto.ClienteResponse;
import com.examen.mx.clientedatos.model.Cliente;
import com.examen.mx.clientedatos.repository.ClienteRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class ClienteServiceTest {

    private final ClienteRepository repository = mock(ClienteRepository.class);
    private final ClienteService service = new ClienteService(repository);

    @Test
    void crear_guardaClienteYDevuelveRespuestaConId() {
        ClienteRequest request = new ClienteRequest(
                " Ana ",
                "García",
                "López",
                LocalDate.of(1995, 4, 23),
                "F",
                "ana.garcia@example.com",
                "5512345678"
        );

        when(repository.existsByNombreCompletoNormalizado(
                "3:ana6:garcía5:lopez"
        )).thenReturn(false);
        when(repository.saveAndFlush(any(Cliente.class))).thenAnswer(invocacion -> {
            Cliente cliente = invocacion.getArgument(0);
            cliente.setId(1L);
            return cliente;
        });

        ClienteResponse response = service.crear(request);

        assertEquals(1L, response.id());
        assertEquals(" Ana ", response.nombre());
        assertEquals("García", response.apellidoPaterno());
        assertEquals("ana.garcia@example.com", response.correo());
        verify(repository).existsByNombreCompletoNormalizado(
                "3:ana6:garcía5:lopez"
        );
        verify(repository).saveAndFlush(any(Cliente.class));
    }
}
