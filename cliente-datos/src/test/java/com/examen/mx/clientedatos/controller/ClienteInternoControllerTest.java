package com.examen.mx.clientedatos.controller;

import com.examen.mx.clientedatos.dto.ClienteRequest;
import com.examen.mx.clientedatos.dto.ClienteResponse;
import com.examen.mx.clientedatos.exception.ClienteDatosExceptionHandler;
import com.examen.mx.clientedatos.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClienteInternoControllerTest {

    private ClienteService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(ClienteService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ClienteInternoController(service))
                .setControllerAdvice(new ClienteDatosExceptionHandler())
                .build();
    }

    @Test
    void crear_conDatosValidosDevuelve201YElCliente() throws Exception {
        ClienteRequest request = new ClienteRequest(
                "Ana",
                "García",
                "López",
                LocalDate.of(1995, 4, 23),
                "F",
                "ana.garcia@example.com",
                "5512345678"
        );
        when(service.crear(request)).thenReturn(new ClienteResponse(
                1L,
                "Ana",
                "García",
                "López",
                LocalDate.of(1995, 4, 23),
                "F",
                "ana.garcia@example.com",
                "5512345678"
        ));

        mockMvc.perform(post("/internal/clientes")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellidoPaterno": "García",
                                  "apellidoMaterno": "López",
                                  "fechaNacimiento": "1995-04-23",
                                  "sexo": "F",
                                  "correo": "ana.garcia@example.com",
                                  "telefono": "5512345678"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.telefono").value("5512345678"));
    }

    @Test
    void crear_conTelefonoInvalidoDevuelve400ConDetalleDelCampo() throws Exception {
        mockMvc.perform(post("/internal/clientes")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellidoPaterno": "García",
                                  "apellidoMaterno": "López",
                                  "fechaNacimiento": "1995-04-23",
                                  "sexo": "F",
                                  "correo": "ana.garcia@example.com",
                                  "telefono": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/internal/clientes"))
                .andExpect(jsonPath("$.detalles[0].campo").value("telefono"));

        verifyNoInteractions(service);
    }
}
