package com.examen.mx.clienteapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        int status,
        String mensaje,
        List<ErrorCampo> detalles,
        Instant timestamp,
        String path,
        String correlationId)
{}
