package com.devsu.account.infrastructure.persistence;

import com.devsu.account.domain.model.ClienteReferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteReferenciaJpaRepository extends JpaRepository<ClienteReferencia, Long> {
}
