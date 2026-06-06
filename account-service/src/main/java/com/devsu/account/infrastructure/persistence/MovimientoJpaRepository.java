package com.devsu.account.infrastructure.persistence;

import com.devsu.account.domain.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoJpaRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaIdAndFechaBetweenOrderByFechaAscIdAsc(
            Long cuentaId, LocalDate fechaDesde, LocalDate fechaHasta);
}
