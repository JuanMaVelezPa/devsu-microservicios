package com.devsu.client.infrastructure.messaging;

import com.devsu.client.infrastructure.config.DevsuProperties;
import com.devsu.client.infrastructure.observability.BusinessMetrics;
import com.devsu.client.infrastructure.persistence.OutboxEvent;
import com.devsu.client.infrastructure.persistence.OutboxEventJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
@ConditionalOnProperty(prefix = "devsu.outbox", name = "publisher-enabled", havingValue = "true", matchIfMissing = true)
public class OutboxKafkaPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxKafkaPublisher.class);
    private static final int BATCH_SIZE = 50;

    private final OutboxEventJpaRepository outboxRepository;
    private final OutboxEventMarker outboxEventMarker;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;
    private final BusinessMetrics businessMetrics;

    public OutboxKafkaPublisher(
            OutboxEventJpaRepository outboxRepository,
            OutboxEventMarker outboxEventMarker,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            DevsuProperties devsuProperties,
            BusinessMetrics businessMetrics) {
        this.outboxRepository = outboxRepository;
        this.outboxEventMarker = outboxEventMarker;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = devsuProperties.getKafka().getTopicClientEvents();
        this.businessMetrics = businessMetrics;
    }

    @Scheduled(fixedDelayString = "${devsu.outbox.publish-interval-ms:3000}")
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxRepository.findPendingEvents(PageRequest.of(0, BATCH_SIZE));
        for (OutboxEvent event : pending) {
            publishSingle(event);
        }
    }

    private void publishSingle(OutboxEvent event) {
        try {
            String payloadJson = writePayload(event.getPayload());
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    topic,
                    String.valueOf(event.getAggregateId()),
                    payloadJson
            );
            addHeader(record, "eventId", event.getId().toString());
            addHeader(record, "eventType", event.getEventType());
            if (event.getCorrelationId() != null) {
                addHeader(record, "correlationId", event.getCorrelationId().toString());
            }

            kafkaTemplate.send(record).get();
            outboxEventMarker.markPublished(event.getId());
            businessMetrics.incrementOutboxPublicado(event.getEventType());
            log.info(
                    "Outbox publicado: eventId={} eventType={} aggregateId={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateId()
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Publicacion outbox interrumpida: eventId={}", event.getId());
        } catch (ExecutionException ex) {
            log.error("Error publicando outbox eventId={}: {}", event.getId(), ex.getMessage());
        } catch (JsonProcessingException ex) {
            log.error("Error serializando outbox eventId={}: {}", event.getId(), ex.getMessage());
        }
    }

    private String writePayload(Map<String, Object> payload) throws JsonProcessingException {
        return objectMapper.writeValueAsString(payload);
    }

    private void addHeader(ProducerRecord<String, String> record, String key, String value) {
        record.headers().add(key, value.getBytes(StandardCharsets.UTF_8));
    }
}
