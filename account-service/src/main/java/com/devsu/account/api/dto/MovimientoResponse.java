package com.devsu.account.api.dto;

import com.devsu.account.domain.model.TipoMovimiento;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Movimiento registrado con saldo resultante")
public record MovimientoResponse(
        @Schema(example = "1")
        Long id,
        @Schema(example = "1")
        Long cuentaId,
        @Schema(example = "478758")
        String numeroCuenta,
        @Schema(example = "2022-02-01T09:15:30")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fecha,
        @Schema(description = "DEPOSITO o RETIRO segun el signo del valor", example = "RETIRO")
        TipoMovimiento tipoMovimiento,
        @Schema(example = "-575")
        BigDecimal valor,
        @Schema(description = "Saldo de la cuenta tras aplicar el movimiento", example = "1425")
        BigDecimal saldoResultante
) {
}
