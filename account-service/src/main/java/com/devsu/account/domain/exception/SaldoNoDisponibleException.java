package com.devsu.account.domain.exception;

public class SaldoNoDisponibleException extends DomainException {

    public SaldoNoDisponibleException() {
        super("SALDO_NO_DISPONIBLE", "Saldo no disponible");
    }
}
