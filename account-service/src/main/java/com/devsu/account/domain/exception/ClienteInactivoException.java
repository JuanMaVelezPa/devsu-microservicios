package com.devsu.account.domain.exception;

public class ClienteInactivoException extends DomainException {

    public ClienteInactivoException() {
        super("CLIENTE_INACTIVO", "Cliente inactivo");
    }
}
