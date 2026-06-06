package com.devsu.client.infrastructure.persistence;

import com.devsu.client.api.context.CorrelationContext;
import com.devsu.client.application.event.ClienteEventType;
import com.devsu.client.application.port.ClienteOutboxPort;
import com.devsu.client.domain.model.Cliente;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class ClienteOutboxAdapter implements ClienteOutboxPort {

    private final OutboxEventJpaRepository outboxRepository;

    public ClienteOutboxAdapter(OutboxEventJpaRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Override
    public void enqueueCreated(Cliente cliente) {
        saveEvent(ClienteEventType.CREADO, cliente.getId(), clientePayload(cliente));
    }

    @Override
    public void enqueueUpdated(Cliente cliente) {
        saveEvent(ClienteEventType.ACTUALIZADO, cliente.getId(), clientePayload(cliente));
    }

    @Override
    public void enqueueDeleted(Long clienteId) {
        saveEvent(ClienteEventType.ELIMINADO, clienteId, Map.of("id", clienteId));
    }

    private Map<String, Object> clientePayload(Cliente cliente) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", cliente.getId());
        payload.put("nombre", cliente.getNombre());
        payload.put("identificacion", cliente.getIdentificacion());
        payload.put("activo", cliente.isEstado());
        return payload;
    }

    private void saveEvent(String eventType, Long aggregateId, Map<String, Object> payload) {
        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setAggregateType(ClienteEventType.AGGREGATE_TYPE);
        event.setAggregateId(aggregateId);
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setCorrelationId(parseCorrelationId(CorrelationContext.get()));
        outboxRepository.save(event);
    }

    private UUID parseCorrelationId(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(correlationId);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
