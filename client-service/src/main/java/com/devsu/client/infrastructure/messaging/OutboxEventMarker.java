package com.devsu.client.infrastructure.messaging;

import com.devsu.client.infrastructure.persistence.OutboxEvent;
import com.devsu.client.infrastructure.persistence.OutboxEventJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OutboxEventMarker {

    private final OutboxEventJpaRepository outboxRepository;

    public OutboxEventMarker(OutboxEventJpaRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void markPublished(UUID eventId) {
        OutboxEvent event = outboxRepository.findById(eventId).orElseThrow();
        event.setPublishedAt(LocalDateTime.now());
        outboxRepository.save(event);
    }
}
