package com.devsu.account.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado operativo de la cuenta")
public enum EstadoCuenta {
    @Schema(description = "Cuenta habilitada para movimientos")
    ACTIVA,
    @Schema(description = "Cuenta deshabilitada")
    INACTIVA
}
