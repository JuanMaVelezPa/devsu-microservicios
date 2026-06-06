package com.devsu.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Reporte de estado de cuenta por cliente y rango de fechas (F4 del reto)")
public record ReporteResponse(
        @Schema(description = "Nombre del cliente consultado", example = "Marianela Montalvo")
        String cliente,
        @Schema(example = "2022-02-01")
        LocalDate fechaDesde,
        @Schema(example = "2022-02-28")
        LocalDate fechaHasta,
        @Schema(description = "Cuentas del cliente con movimientos en el rango y saldo actual")
        List<CuentaReporteResponse> cuentas
) {
}
