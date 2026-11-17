package com.examen.mx.clientedatos.repository;

import com.examen.mx.clientedatos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByNombreCompletoNormalizado(String nombreNormalizado);

    boolean existsByNombreCompletoNormalizadoAndIdNot(
            String nombreNormalizado,
            Long id
    );
}
