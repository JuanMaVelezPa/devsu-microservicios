package com.devsu.client.domain.exception;

public class ClienteDuplicadoException extends DomainException {

    public ClienteDuplicadoException() {
        super("CLIENTE_DUPLICADO", "Identificacion duplicada");
    }
}
