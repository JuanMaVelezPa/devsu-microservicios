package com.devsu.account.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Linea de movimiento en el reporte (orden cronologico por fecha/hora)")
public record MovimientoReporteResponse(
        @Schema(example = "2022-02-10T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fecha,
        @Schema(description = "Positivo = deposito, negativo = retiro", example = "600")
        BigDecimal valor,
        @Schema(description = "Saldo tras el movimiento", example = "700")
        BigDecimal saldoResultante
) {
}
