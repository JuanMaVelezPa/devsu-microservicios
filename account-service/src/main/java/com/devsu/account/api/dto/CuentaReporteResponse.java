package com.devsu.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Resumen de cuenta dentro de un reporte")
public record CuentaReporteResponse(
        @Schema(example = "225487")
        String numeroCuenta,
        @Schema(description = "Saldo actual al generar el reporte", example = "700")
        BigDecimal saldoActual,
        @Schema(description = "Movimientos del rango; vacio si no hubo actividad")
        List<MovimientoReporteResponse> movimientos
) {
}
