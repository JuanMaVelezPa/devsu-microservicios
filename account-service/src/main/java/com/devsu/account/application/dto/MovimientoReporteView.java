package com.devsu.account.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoReporteView(
        LocalDateTime fecha,
        BigDecimal valor,
        BigDecimal saldoResultante
) {
}
