package com.devsu.account.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoReporteResponse(
        LocalDate fecha,
        BigDecimal valor,
        BigDecimal saldoResultante
) {
}
