package com.devsu.client.infrastructure.observability;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {

    private final MeterRegistry meterRegistry;

    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementClienteOperacion(String operacion) {
        meterRegistry.counter("devsu.cliente.operaciones", "operacion", operacion).increment();
    }

    public void incrementOutboxPublicado(String eventType) {
        meterRegistry.counter("devsu.outbox.publicados", "event_type", eventType).increment();
    }
}
