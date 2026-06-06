package com.devsu.account.domain.exception;

public class ClienteReferenciaNotFoundException extends DomainException {

    public ClienteReferenciaNotFoundException() {
        super("CLIENTE_NOT_FOUND", "Cliente no encontrado");
    }
}
