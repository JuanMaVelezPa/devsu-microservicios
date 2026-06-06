package com.devsu.account.api.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CuentaRequest(
        @NotNull Long clienteId,
        @NotBlank String numeroCuenta,
        @NotNull TipoCuenta tipoCuenta,
        @NotNull @DecimalMin("0") BigDecimal saldoInicial,
        EstadoCuenta estado
) {
}
