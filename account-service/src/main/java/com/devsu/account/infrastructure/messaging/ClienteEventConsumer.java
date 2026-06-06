package com.devsu.account.infrastructure.messaging;

import com.devsu.account.api.context.CorrelationContext;
import com.devsu.account.api.filter.CorrelationIdFilter;
import com.devsu.account.application.ClienteReferenciaSyncService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class ClienteEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClienteEventConsumer.class);
    private static final String HEADER_EVENT_ID = "eventId";
    private static final String HEADER_EVENT_TYPE = "eventType";
    private static final String HEADER_CORRELATION_ID = "correlationId";

    private final ClienteReferenciaSyncService syncService;

    public ClienteEventConsumer(ClienteReferenciaSyncService syncService) {
        this.syncService = syncService;
    }

    @KafkaListener(topics = "${devsu.kafka.topic-client-events}")
    public void onClienteEvent(ConsumerRecord<String, String> record) {
        UUID eventId = parseUuidHeader(record, HEADER_EVENT_ID);
        String eventType = readHeader(record, HEADER_EVENT_TYPE);
        String correlationId = readHeader(record, HEADER_CORRELATION_ID);

        if (eventId == null || eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("Headers Kafka obligatorios: eventId, eventType");
        }

        try {
            applyCorrelationContext(correlationId);
            syncService.processEvent(eventId, eventType, record.value());
        } finally {
            MDC.clear();
            CorrelationContext.clear();
        }
    }

    private void applyCorrelationContext(String correlationId) {
        if (correlationId != null && !correlationId.isBlank()) {
            MDC.put(CorrelationIdFilter.MDC_KEY, correlationId);
            CorrelationContext.set(correlationId);
        }
    }

    private UUID parseUuidHeader(ConsumerRecord<String, String> record, String headerName) {
        String value = readHeader(record, headerName);
        if (value == null || value.isBlank()) {
            return null;
        }
        return UUID.fromString(value);
    }

    private String readHeader(ConsumerRecord<String, String> record, String headerName) {
        var header = record.headers().lastHeader(headerName);
        if (header == null) {
            return null;
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}
