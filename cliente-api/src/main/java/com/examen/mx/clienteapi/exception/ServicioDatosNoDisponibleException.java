package com.examen.mx.clienteapi.exception;

public class ServicioDatosNoDisponibleException extends RuntimeException {
    public ServicioDatosNoDisponibleException() {
        super("El servicio de datos no está disponible");
    }
}
