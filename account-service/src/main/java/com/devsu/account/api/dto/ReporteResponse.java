package com.devsu.account.api.dto;

import java.time.LocalDate;
import java.util.List;

public record ReporteResponse(
        String cliente,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        List<CuentaReporteResponse> cuentas
) {
}
