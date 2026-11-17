package com.examen.mx.clienteapi.exception;

public class NombreDuplicadoException extends RuntimeException {
    public NombreDuplicadoException() {
        super("Ya existe un cliente con ese nombre completo");
    }
}
