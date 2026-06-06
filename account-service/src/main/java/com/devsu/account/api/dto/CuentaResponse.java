package com.devsu.account.api.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;

import java.math.BigDecimal;

public record CuentaResponse(
        Long id,
        Long clienteId,
        String numeroCuenta,
        TipoCuenta tipoCuenta,
        BigDecimal saldo,
        EstadoCuenta estado
) {
}
