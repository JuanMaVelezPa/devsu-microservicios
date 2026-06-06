package com.devsu.account.api.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Datos para crear una cuenta bancaria")
public record CuentaRequest(
        @Schema(description = "ID del cliente en cliente_referencia (proyeccion Kafka)", example = "1")
        @NotNull Long clienteId,
        @Schema(description = "Numero de cuenta unico", example = "478758")
        @NotBlank String numeroCuenta,
        @Schema(description = "Tipo de cuenta", example = "AHORROS")
        @NotNull TipoCuenta tipoCuenta,
        @Schema(description = "Saldo inicial >= 0", example = "2000")
        @NotNull @DecimalMin("0") BigDecimal saldoInicial,
        @Schema(description = "Estado; por defecto ACTIVA si se omite", example = "ACTIVA")
        EstadoCuenta estado
) {
}
