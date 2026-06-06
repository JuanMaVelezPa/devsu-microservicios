package com.devsu.account.application.port;

import com.devsu.account.domain.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepositoryPort {

    Movimiento save(Movimiento movimiento);

    Optional<Movimiento> findById(Long id);

    Page<Movimiento> findAll(Pageable pageable);

    List<Movimiento> findByCuentaIdAndFechaBetweenOrderByFechaAscIdAsc(
            Long cuentaId, LocalDate fechaDesde, LocalDate fechaHasta);
}
