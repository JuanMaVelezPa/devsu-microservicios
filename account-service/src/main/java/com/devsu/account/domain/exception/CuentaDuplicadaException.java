package com.devsu.account.domain.exception;

public class CuentaDuplicadaException extends DomainException {

    public CuentaDuplicadaException() {
        super("CUENTA_DUPLICADA", "Numero de cuenta duplicado");
    }
}
