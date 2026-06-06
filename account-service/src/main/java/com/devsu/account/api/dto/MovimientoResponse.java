package com.devsu.account.api.dto;

import com.devsu.account.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoResponse(
        Long id,
        Long cuentaId,
        String numeroCuenta,
        LocalDate fecha,
        TipoMovimiento tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldoResultante
) {
}
