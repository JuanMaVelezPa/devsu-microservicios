package com.devsu.account.application.port;

import com.devsu.account.domain.model.ClienteReferencia;

import java.util.Optional;

public interface ClienteReferenciaRepositoryPort {

    Optional<ClienteReferencia> findById(Long id);

    Optional<ClienteReferencia> findByNombreIgnoreCase(String nombre);

    ClienteReferencia save(ClienteReferencia clienteReferencia);
}
