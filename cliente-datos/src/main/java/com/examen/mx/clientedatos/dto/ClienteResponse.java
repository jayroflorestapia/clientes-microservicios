package com.examen.mx.clientedatos.dto;

import java.time.LocalDate;

public record  ClienteResponse (
        Long id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        LocalDate fechaNacimiento,
        String sexo,
        String correo,
        String telefono
) {
}
