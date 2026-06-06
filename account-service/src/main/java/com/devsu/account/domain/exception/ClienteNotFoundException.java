package com.devsu.account.domain.exception;

public class ClienteNotFoundException extends ResourceNotFoundException {

    public ClienteNotFoundException() {
        super("CLIENTE_NOT_FOUND", "Cliente no encontrado");
    }
}
