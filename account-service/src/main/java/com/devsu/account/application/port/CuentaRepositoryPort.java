package com.devsu.account.application.port;

import com.devsu.account.domain.model.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CuentaRepositoryPort {

    Cuenta save(Cuenta cuenta);

    Optional<Cuenta> findById(Long id);

    Page<Cuenta> findAll(Pageable pageable);

    boolean existsByNumeroCuenta(String numeroCuenta);
}
