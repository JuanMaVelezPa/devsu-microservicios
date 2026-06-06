package com.devsu.account.infrastructure.persistence;

import com.devsu.account.domain.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoJpaRepository extends JpaRepository<Movimiento, Long> {
}
