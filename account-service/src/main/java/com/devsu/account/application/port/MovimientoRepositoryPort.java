package com.devsu.account.application.port;

import com.devsu.account.domain.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MovimientoRepositoryPort {

    Movimiento save(Movimiento movimiento);

    Optional<Movimiento> findById(Long id);

    Page<Movimiento> findAll(Pageable pageable);
}
