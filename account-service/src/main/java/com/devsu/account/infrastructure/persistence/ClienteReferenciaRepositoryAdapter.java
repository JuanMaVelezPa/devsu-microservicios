package com.devsu.account.infrastructure.persistence;

import com.devsu.account.application.port.ClienteReferenciaRepositoryPort;
import com.devsu.account.domain.model.ClienteReferencia;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ClienteReferenciaRepositoryAdapter implements ClienteReferenciaRepositoryPort {

    private final ClienteReferenciaJpaRepository jpaRepository;

    public ClienteReferenciaRepositoryAdapter(ClienteReferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ClienteReferencia> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<ClienteReferencia> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre);
    }

    @Override
    public ClienteReferencia save(ClienteReferencia clienteReferencia) {
        return jpaRepository.save(clienteReferencia);
    }
}
