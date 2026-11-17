package com.examen.mx.clienteapi.exception;

public class ErrorServicioInternoException extends RuntimeException {
    public ErrorServicioInternoException() {
        super("Ocurrió un error interno");
    }
}
