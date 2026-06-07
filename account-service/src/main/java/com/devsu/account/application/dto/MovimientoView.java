package com.devsu.account.application.dto;

import com.devsu.account.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoView(
        Long id,
        Long cuentaId,
        String numeroCuenta,
        LocalDateTime fecha,
        TipoMovimiento tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldoResultante
) {
}
