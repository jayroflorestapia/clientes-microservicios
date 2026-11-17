package com.examen.mx.clienteapi.exception;

import com.examen.mx.clienteapi.dto.ErrorCampo;
import com.examen.mx.clienteapi.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(ApiExceptionHandler.class);

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

    @ExceptionHandler(NombreDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarNombreDuplicado(
            NombreDuplicadoException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Ya existe un cliente con ese nombre completo",
                List.of(),
                request
        );
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorResponse> manejarDatosInvalidos(
            DatosInvalidosException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "La petición contiene datos inválidos",
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

    @ExceptionHandler(ServicioDatosNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> manejarServicioNoDisponible(
            ServicioDatosNoDisponibleException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.SERVICE_UNAVAILABLE,
                "El servicio de datos no respondió dentro del tiempo esperado",
                List.of(),
                request
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> manejarParametroInvalido(
            Exception ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "El identificador o parámetro de la petición no es válido",
                List.of(),
                request
        );
    }

    @ExceptionHandler(ErrorServicioInternoException.class)
    public ResponseEntity<ErrorResponse> manejarErrorDelServicioInterno(
            ErrorServicioInternoException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno",
                List.of(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorInesperado(
            Exception ex,
            HttpServletRequest request
    ) {
        // No registrar el cuerpo ni los datos personales de la petición.
        log.error("Error inesperado en cliente-api. Tipo={}",
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
