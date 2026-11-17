package com.examen.mx.clientedatos.exception;

public class ClienteDuplicadoException extends RuntimeException{
    public ClienteDuplicadoException() {
        super("Ya existe un cliente con ese nombre completo");
    }
}
