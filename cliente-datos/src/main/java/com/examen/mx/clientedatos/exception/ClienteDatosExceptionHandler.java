package com.examen.mx.clientedatos.exception;

import com.examen.mx.clientedatos.dto.ErrorCampo;
import com.examen.mx.clientedatos.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ClienteDatosExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(ClienteDatosExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErrorCampo> detalles = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::convertirErrorCampo)
                .toList();

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "La petición contiene datos inválidos",
                detalles,
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "El JSON es inválido o contiene un formato incorrecto",
                List.of(),
                request
        );
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(
            ClienteNoEncontradoException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                List.of(),
                request
        );
    }

    @ExceptionHandler({
            ClienteDuplicadoException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorResponse> manejarDuplicado(
            Exception ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                "Ya existe un cliente con ese nombre completo",
                List.of(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorInesperado(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Error inesperado en cliente-datos. Tipo={}",
                ex.getClass().getSimpleName());

        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno",
                List.of(),
                request
        );
    }

    private ErrorCampo convertirErrorCampo(FieldError error) {
        String mensaje = error.getDefaultMessage() == null
                ? "Valor inválido"
                : error.getDefaultMessage();

        return new ErrorCampo(error.getField(), mensaje);
    }

    private ResponseEntity<ErrorResponse> construirRespuesta(
            HttpStatus status,
            String mensaje,
            List<ErrorCampo> detalles,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                mensaje,
                detalles,
                Instant.now(),
                request.getRequestURI(),
                ThreadContext.get("correlationId")
        );

        return ResponseEntity.status(status).body(body);
    }
}
