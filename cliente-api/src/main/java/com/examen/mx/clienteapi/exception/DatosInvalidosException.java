package com.examen.mx.clienteapi.exception;

public class DatosInvalidosException extends RuntimeException {
    public DatosInvalidosException() {
        super("La petición contiene datos inválidos");
    }
}
