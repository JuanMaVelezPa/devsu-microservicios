package com.devsu.account.api.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Cuenta bancaria")
public record CuentaResponse(
        @Schema(example = "1")
        Long id,
        @Schema(description = "Referencia al cliente sincronizado por Kafka", example = "1")
        Long clienteId,
        @Schema(example = "478758")
        String numeroCuenta,
        @Schema(example = "AHORROS")
        TipoCuenta tipoCuenta,
        @Schema(description = "Saldo actual de la cuenta", example = "1425")
        BigDecimal saldo,
        @Schema(example = "ACTIVA")
        EstadoCuenta estado
) {
}
