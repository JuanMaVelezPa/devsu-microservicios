package com.devsu.account.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record CuentaReporteResponse(
        String numeroCuenta,
        BigDecimal saldoActual,
        List<MovimientoReporteResponse> movimientos
) {
}
