package com.devsu.account.infrastructure.persistence;

import com.devsu.account.application.port.ProcessedEventRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepositoryPort {

    private final ProcessedEventJpaRepository jpaRepository;

    public ProcessedEventRepositoryAdapter(ProcessedEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByEventId(UUID eventId) {
        return jpaRepository.existsById(eventId);
    }

    @Override
    public void markProcessed(UUID eventId) {
        jpaRepository.save(new ProcessedEvent(eventId));
    }
}
