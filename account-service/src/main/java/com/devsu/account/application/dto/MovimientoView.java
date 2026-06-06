package com.devsu.account.application.dto;

import com.devsu.account.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MovimientoView(
        Long id,
        Long cuentaId,
        String numeroCuenta,
        LocalDate fecha,
        TipoMovimiento tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldoResultante
) {
}
