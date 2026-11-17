package com.examen.mx.clienteapi.exception;

public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException() {
        super("No existe el cliente solicitado");
    }
}
