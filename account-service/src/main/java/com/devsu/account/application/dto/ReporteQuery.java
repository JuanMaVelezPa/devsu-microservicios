package com.devsu.account.application.dto;

import java.time.LocalDate;

public record ReporteQuery(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        String cliente
) {
}
