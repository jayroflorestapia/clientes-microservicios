package com.examen.mx.clienteapi.cliente;

import com.examen.mx.clienteapi.dto.ClienteEliminadoResponse;
import com.examen.mx.clienteapi.dto.ClienteRequest;
import com.examen.mx.clienteapi.dto.ClienteResponse;
import com.examen.mx.clienteapi.exception.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

@Component
public class ClienteDatosClient {

    private final RestClient restClient;

    public ClienteDatosClient(RestClient clienteDatosRestClient) {
        this.restClient = clienteDatosRestClient;
    }

    public ClienteResponse crear(ClienteRequest request) {
        return ejecutar(() -> restClient.post()
                .uri("/internal/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ClienteResponse.class));
    }

    public ClienteResponse consultar(Long id) {
        return ejecutar(() -> restClient.get()
                .uri("/internal/clientes/{id}", id)
                .retrieve()
                .body(ClienteResponse.class));
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        return ejecutar(() -> restClient.put()
                .uri("/internal/clientes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ClienteResponse.class));
    }

    public ClienteEliminadoResponse eliminar(Long id) {
        return ejecutar(() -> restClient.delete()
                .uri("/internal/clientes/{id}", id)
                .retrieve()
                .body(ClienteEliminadoResponse.class));
    }

    private <T> T ejecutar(Supplier<T> llamada) {
        try {
            return llamada.get();
        } catch (ResourceAccessException ex) {
            // Incluye problemas de conexión y timeout.
            throw new ServicioDatosNoDisponibleException();
        } catch (RestClientResponseException ex) {
            int status = ex.getStatusCode().value();

            if (status == 404) {
                throw new ClienteNoEncontradoException();
            }
            if (status == 409) {
                // El servicio de datos usa 409 para nombre duplicado.
                throw new NombreDuplicadoException();
            }
            if (status == 400) {
                throw new DatosInvalidosException();
            }

            throw new ErrorServicioInternoException();
        }
    }
}
