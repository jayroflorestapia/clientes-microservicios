package com.examen.mx.clienteapi.service;

import com.examen.mx.clienteapi.cliente.ClienteDatosClient;
import com.examen.mx.clienteapi.dto.ClienteEliminadoResponse;
import com.examen.mx.clienteapi.dto.ClienteRequest;
import com.examen.mx.clienteapi.dto.ClienteResponse;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteDatosClient clienteDatosClient;

    public ClienteService(ClienteDatosClient clienteDatosClient) {
        this.clienteDatosClient = clienteDatosClient;
    }

    public ClienteResponse crear(ClienteRequest request) {
        return clienteDatosClient.crear(request);
    }

    public ClienteResponse consultar(Long id) {
        return clienteDatosClient.consultar(id);
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        return clienteDatosClient.actualizar(id, request);
    }

    public ClienteEliminadoResponse eliminar(Long id) {
        return clienteDatosClient.eliminar(id);
    }

}
