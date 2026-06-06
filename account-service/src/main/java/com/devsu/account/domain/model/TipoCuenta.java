package com.devsu.account.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de cuenta bancaria")
public enum TipoCuenta {
    @Schema(description = "Cuenta de ahorros")
    AHORROS,
    @Schema(description = "Cuenta corriente")
    CORRIENTE
}
