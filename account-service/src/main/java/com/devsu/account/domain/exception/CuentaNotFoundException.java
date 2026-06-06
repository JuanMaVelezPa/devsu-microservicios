package com.devsu.account.domain.exception;

public class CuentaNotFoundException extends ResourceNotFoundException {

    public CuentaNotFoundException() {
        super("CUENTA_NOT_FOUND", "Cuenta no encontrada");
    }
}
