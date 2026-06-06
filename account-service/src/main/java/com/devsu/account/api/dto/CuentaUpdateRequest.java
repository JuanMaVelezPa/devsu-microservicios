package com.devsu.account.api.dto;

import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para actualizar tipo y estado de una cuenta (CRU sin delete)")
public record CuentaUpdateRequest(
        @Schema(example = "CORRIENTE")
        @NotNull TipoCuenta tipoCuenta,
        @Schema(example = "ACTIVA")
        @NotNull EstadoCuenta estado
) {
}
