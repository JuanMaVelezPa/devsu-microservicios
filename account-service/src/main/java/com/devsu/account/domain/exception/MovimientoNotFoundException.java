package com.devsu.account.domain.exception;

public class MovimientoNotFoundException extends ResourceNotFoundException {

    public MovimientoNotFoundException() {
        super("MOVIMIENTO_NOT_FOUND", "Movimiento no encontrado");
    }
}
