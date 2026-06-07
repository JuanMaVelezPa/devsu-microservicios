package com.devsu.account.infrastructure.persistence;

import com.devsu.account.application.port.MovimientoRepositoryPort;
import com.devsu.account.domain.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MovimientoRepositoryAdapter implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository jpaRepository;

    public MovimientoRepositoryAdapter(MovimientoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Movimiento save(Movimiento movimiento) {
        return jpaRepository.save(movimiento);
    }

    @Override
    public Optional<Movimiento> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Movimiento> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public List<Movimiento> findByCuentaIdAndFechaBetweenOrderByFechaAscIdAsc(
            Long cuentaId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return jpaRepository.findByCuentaIdAndFechaBetweenOrderByFechaAscIdAsc(cuentaId, fechaDesde, fechaHasta);
    }
}
