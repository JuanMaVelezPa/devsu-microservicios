package com.devsu.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Registro de un movimiento (deposito o retiro)")
public record MovimientoRequest(
        @Schema(description = "Numero de cuenta destino", example = "478758")
        @NotBlank String numeroCuenta,
        @Schema(description = "Valor positivo = deposito, negativo = retiro", example = "-575")
        @NotNull BigDecimal valor,
        @Schema(description = "Fecha contable (ISO-8601). Si se omite, usa la fecha actual.", example = "2022-02-01")
        LocalDate fecha
) {
}
