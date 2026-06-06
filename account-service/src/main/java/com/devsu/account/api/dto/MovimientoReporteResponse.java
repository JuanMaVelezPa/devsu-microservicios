package com.devsu.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Linea de movimiento en el reporte")
public record MovimientoReporteResponse(
        @Schema(example = "2022-02-10")
        LocalDate fecha,
        @Schema(description = "Positivo = deposito, negativo = retiro", example = "600")
        BigDecimal valor,
        @Schema(description = "Saldo tras el movimiento", example = "700")
        BigDecimal saldoResultante
) {
}
