package com.examen.mx.clientedatos.exception;

public class ClienteNoEncontradoException extends RuntimeException{
    public ClienteNoEncontradoException(Long id) {
        super("No existe un cliente con el identificador " + id);
    }
}
