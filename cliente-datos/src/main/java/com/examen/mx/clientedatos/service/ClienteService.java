package com.examen.mx.clientedatos.service;

import com.examen.mx.clientedatos.dto.ClienteEliminadoResponse;
import com.examen.mx.clientedatos.dto.ClienteRequest;
import com.examen.mx.clientedatos.dto.ClienteResponse;
import com.examen.mx.clientedatos.exception.ClienteDuplicadoException;
import com.examen.mx.clientedatos.exception.ClienteNoEncontradoException;
import com.examen.mx.clientedatos.model.Cliente;
import com.examen.mx.clientedatos.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        String normalizado = normalizarNombreCompleto(request);

        if (repository.existsByNombreCompletoNormalizado(normalizado)) {
            throw new ClienteDuplicadoException();
        }

        Cliente cliente = new Cliente();
        copiarDatos(request, cliente);
        cliente.setNombreCompletoNormalizado(normalizado);

        return convertir(repository.saveAndFlush(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponse consultar(Long id) {
        return convertir(buscarPorId(id));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarPorId(id);
        String normalizado = normalizarNombreCompleto(request);

        if (repository.existsByNombreCompletoNormalizadoAndIdNot(
                normalizado, id)) {
            throw new ClienteDuplicadoException();
        }

        copiarDatos(request, cliente);
        cliente.setNombreCompletoNormalizado(normalizado);

        return convertir(repository.saveAndFlush(cliente));
    }

    @Transactional
    public ClienteEliminadoResponse eliminar(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
        return new ClienteEliminadoResponse(id, "Cliente eliminado");
    }

    private Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    private void copiarDatos(ClienteRequest request, Cliente cliente) {
        cliente.setNombre(request.nombre());
        cliente.setApellidoPaterno(request.apellidoPaterno());
        cliente.setApellidoMaterno(request.apellidoMaterno());
        cliente.setFechaNacimiento(request.fechaNacimiento());
        cliente.setSexo(request.sexo());
        cliente.setCorreo(request.correo());
        cliente.setTelefono(request.telefono());
    }

    private ClienteResponse convertir(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellidoPaterno(),
                cliente.getApellidoMaterno(),
                cliente.getFechaNacimiento(),
                cliente.getSexo(),
                cliente.getCorreo(),
                cliente.getTelefono()
        );
    }

    private String normalizarNombreCompleto(ClienteRequest request) {
        String nombre = normalizar(request.nombre());
        String paterno = normalizar(request.apellidoPaterno());
        String materno = normalizar(request.apellidoMaterno());

        return nombre.length() + ":" + nombre
                + paterno.length() + ":" + paterno
                + materno.length() + ":" + materno;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.strip()
                .replaceAll("(?U)\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
