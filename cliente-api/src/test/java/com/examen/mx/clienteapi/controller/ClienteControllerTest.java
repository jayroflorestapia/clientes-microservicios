package com.examen.mx.clienteapi.controller;

import com.examen.mx.clienteapi.dto.ClienteResponse;
import com.examen.mx.clienteapi.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    void crear_conDatosValidosDevuelve201YLocation() throws Exception {
        when(clienteService.crear(any())).thenReturn(new ClienteResponse(
                42L, "Ana", "Pérez", "López", LocalDate.of(1990, 5, 20),
                "F", "ana@example.com", "5512345678"));

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellidoPaterno": "Pérez",
                                  "apellidoMaterno": "López",
                                  "fechaNacimiento": "1990-05-20",
                                  "sexo": "F",
                                  "correo": "ana@example.com",
                                  "telefono": "5512345678"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/clientes/42"))
                .andExpect(jsonPath("$.id").value(42));

        verify(clienteService).crear(any());
    }

    @Test
    void crear_conTelefonoInvalidoDevuelve400YDetalleDelCampo() throws Exception {
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellidoPaterno": "Pérez",
                                  "apellidoMaterno": "López",
                                  "fechaNacimiento": "1990-05-20",
                                  "sexo": "F",
                                  "correo": "ana@example.com",
                                  "telefono": "12345"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalles[0].campo").value("telefono"));

        verify(clienteService, never()).crear(any());
    }
}
