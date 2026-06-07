package com.devsu.account.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoCommand(
        String numeroCuenta,
        BigDecimal valor,
        LocalDateTime fecha
) {
}
