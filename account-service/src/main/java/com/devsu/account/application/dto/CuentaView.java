package com.devsu.account.application.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;

import java.math.BigDecimal;
import java.util.List;

public record CuentaView(
        Long id,
        Long clienteId,
        String numeroCuenta,
        TipoCuenta tipoCuenta,
        BigDecimal saldo,
        EstadoCuenta estado
) {
}
