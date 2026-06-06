package com.devsu.client.application.port;

import com.devsu.client.domain.model.Cliente;

public interface ClienteOutboxPort {

    void enqueueCreated(Cliente cliente);

    void enqueueUpdated(Cliente cliente);

    void enqueueDeleted(Long clienteId);
}
