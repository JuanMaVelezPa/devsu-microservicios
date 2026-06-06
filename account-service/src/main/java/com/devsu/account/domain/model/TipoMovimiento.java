package com.devsu.account.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de movimiento inferido del signo del valor")
public enum TipoMovimiento {
    @Schema(description = "Valor positivo")
    DEPOSITO,
    @Schema(description = "Valor negativo")
    RETIRO
}
