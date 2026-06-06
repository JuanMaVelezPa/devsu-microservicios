package com.devsu.account.api.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;
import jakarta.validation.constraints.NotNull;

public record CuentaUpdateRequest(
        @NotNull TipoCuenta tipoCuenta,
        @NotNull EstadoCuenta estado
) {
}
