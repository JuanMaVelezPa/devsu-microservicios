package com.devsu.account.application.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;

import java.math.BigDecimal;

public record CuentaCommand(
        Long clienteId,
        String numeroCuenta,
        TipoCuenta tipoCuenta,
        BigDecimal saldoInicial,
        EstadoCuenta estado
) {
}
