package com.devsu.account.infrastructure.persistence;

import com.devsu.account.domain.model.ClienteReferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteReferenciaJpaRepository extends JpaRepository<ClienteReferencia, Long> {

    Optional<ClienteReferencia> findByNombre(String nombre);
}
