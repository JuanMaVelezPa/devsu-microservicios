package com.devsu.account.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record CuentaReporteView(
        String numeroCuenta,
        BigDecimal saldoActual,
        List<MovimientoReporteView> movimientos
) {
}
