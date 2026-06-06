package com.devsu.account.application.port;

import java.util.UUID;

public interface ProcessedEventRepositoryPort {

    boolean existsByEventId(UUID eventId);

    void markProcessed(UUID eventId);
}
