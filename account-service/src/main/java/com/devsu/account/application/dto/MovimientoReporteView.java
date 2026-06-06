package com.devsu.account.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoReporteView(
        LocalDate fecha,
        BigDecimal valor,
        BigDecimal saldoResultante
) {
}
