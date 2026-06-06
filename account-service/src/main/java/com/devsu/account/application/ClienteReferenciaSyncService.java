package com.devsu.account.application;

import com.devsu.account.application.dto.ClienteEliminadoPayload;
import com.devsu.account.application.dto.ClienteEventPayload;
import com.devsu.account.application.event.ClienteEventType;
import com.devsu.account.application.port.ClienteReferenciaRepositoryPort;
import com.devsu.account.application.port.ProcessedEventRepositoryPort;
import com.devsu.account.domain.model.ClienteReferencia;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ClienteReferenciaSyncService {

    private static final Logger log = LoggerFactory.getLogger(ClienteReferenciaSyncService.class);

    private final ClienteReferenciaRepositoryPort clienteReferenciaRepository;
    private final ProcessedEventRepositoryPort processedEventRepository;
    private final ObjectMapper objectMapper;

    public ClienteReferenciaSyncService(
            ClienteReferenciaRepositoryPort clienteReferenciaRepository,
            ProcessedEventRepositoryPort processedEventRepository,
            ObjectMapper objectMapper) {
        this.clienteReferenciaRepository = clienteReferenciaRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processEvent(UUID eventId, String eventType, String payloadJson) {
        if (processedEventRepository.existsByEventId(eventId)) {
            log.debug("Evento ya procesado: eventId={}", eventId);
            return;
        }

        switch (eventType) {
            case ClienteEventType.CREADO, ClienteEventType.ACTUALIZADO -> upsertCliente(readClientePayload(payloadJson));
            case ClienteEventType.ELIMINADO -> deactivateCliente(readEliminadoPayload(payloadJson).id());
            default -> throw new IllegalArgumentException("Tipo de evento no soportado: " + eventType);
        }

        processedEventRepository.markProcessed(eventId);
        log.info("Evento procesado: eventId={} eventType={}", eventId, eventType);
    }

    private void upsertCliente(ClienteEventPayload payload) {
        requireClientePayload(payload);
        ClienteReferencia referencia = clienteReferenciaRepository.findById(payload.id())
                .orElseGet(ClienteReferencia::new);
        referencia.setId(payload.id());
        referencia.setNombre(payload.nombre());
        referencia.setIdentificacion(payload.identificacion());
        referencia.setActivo(payload.activo());
        referencia.setSyncedAt(LocalDateTime.now());
        clienteReferenciaRepository.save(referencia);
    }

    private void deactivateCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("id: es obligatorio en ClienteEliminado");
        }
        clienteReferenciaRepository.findById(clienteId).ifPresent(referencia -> {
            referencia.setActivo(false);
            referencia.setSyncedAt(LocalDateTime.now());
            clienteReferenciaRepository.save(referencia);
        });
    }

    private ClienteEventPayload readClientePayload(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, ClienteEventPayload.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Payload ClienteCreado/Actualizado invalido", ex);
        }
    }

    private ClienteEliminadoPayload readEliminadoPayload(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, ClienteEliminadoPayload.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Payload ClienteEliminado invalido", ex);
        }
    }

    private void requireClientePayload(ClienteEventPayload payload) {
        if (payload.id() == null || payload.nombre() == null || payload.nombre().isBlank()
                || payload.identificacion() == null || payload.identificacion().isBlank()
                || payload.activo() == null) {
            throw new IllegalArgumentException("Payload ClienteCreado/Actualizado incompleto");
        }
    }
}
