package com.devsu.account.infrastructure.persistence;

import com.devsu.account.domain.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaJpaRepository extends JpaRepository<Cuenta, Long> {

    boolean existsByNumeroCuenta(String numeroCuenta);
}
