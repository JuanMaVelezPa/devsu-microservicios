package com.devsu.account.infrastructure.observability;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {

    private final MeterRegistry meterRegistry;

    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementCuentaOperacion(String operacion) {
        meterRegistry.counter("devsu.cuenta.operaciones", "operacion", operacion).increment();
    }

    public void incrementMovimiento(String tipo) {
        meterRegistry.counter("devsu.movimiento.operaciones", "tipo", tipo).increment();
    }

    public void incrementMovimientoRechazo(String motivo) {
        meterRegistry.counter("devsu.movimiento.rechazos", "motivo", motivo).increment();
    }

    public void incrementKafkaEventoProcesado(String eventType) {
        meterRegistry.counter("devsu.kafka.eventos.procesados", "event_type", eventType).increment();
    }

    public void incrementKafkaEventoDuplicado() {
        meterRegistry.counter("devsu.kafka.eventos.duplicados").increment();
    }

    public void incrementReporteGenerado() {
        meterRegistry.counter("devsu.reporte.generados").increment();
    }
}
