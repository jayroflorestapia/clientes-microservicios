package com.examen.mx.clientedatos.dto;


import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClienteRequest(
        @NotBlank
        @Size(min = 1, max = 50)
        String nombre,

        @NotBlank
        @Size(min = 1, max = 50)
        String apellidoPaterno,

        @Size(max = 50)
        String apellidoMaterno,

        @NotNull
        @PastOrPresent
        LocalDate fechaNacimiento,

        @NotBlank
        @Pattern(regexp = "^[MF]$")
        String sexo,

        @NotBlank
        @Email
        @Size(max = 100)
        String correo,

        @NotBlank
        @Pattern(regexp = "^[0-9]{10}$")
        String telefono
) {}