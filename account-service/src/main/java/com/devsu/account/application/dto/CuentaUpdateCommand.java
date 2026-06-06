package com.devsu.account.application.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;

public record CuentaUpdateCommand(
        TipoCuenta tipoCuenta,
        EstadoCuenta estado
) {
}
