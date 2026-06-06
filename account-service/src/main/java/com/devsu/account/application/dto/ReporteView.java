package com.devsu.account.application.dto;

import java.time.LocalDate;
import java.util.List;

public record ReporteView(
        String cliente,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        List<CuentaReporteView> cuentas
) {
}
