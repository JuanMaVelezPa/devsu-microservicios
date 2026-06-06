package com.devsu.account.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoRequest(
        @NotBlank String numeroCuenta,
        @NotNull BigDecimal valor,
        LocalDate fecha
) {
}
