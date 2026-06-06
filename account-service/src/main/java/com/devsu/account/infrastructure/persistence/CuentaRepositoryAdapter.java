package com.devsu.account.infrastructure.persistence;

import com.devsu.account.application.port.CuentaRepositoryPort;
import com.devsu.account.domain.model.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;

    public CuentaRepositoryAdapter(CuentaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        return jpaRepository.save(cuenta);
    }

    @Override
    public Optional<Cuenta> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Cuenta> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.existsByNumeroCuenta(numeroCuenta);
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta);
    }

    @Override
    public List<Cuenta> findByClienteIdOrderByIdAsc(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByIdAsc(clienteId);
    }
}
