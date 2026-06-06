package com.devsu.account.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoCommand(
        String numeroCuenta,
        BigDecimal valor,
        LocalDate fecha
) {
}
