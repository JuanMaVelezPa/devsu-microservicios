package com.devsu.account.application.port;

import com.devsu.account.domain.model.ClienteReferencia;

import java.util.Optional;

public interface ClienteReferenciaRepositoryPort {

    Optional<ClienteReferencia> findById(Long id);

    ClienteReferencia save(ClienteReferencia clienteReferencia);
}
