package com.devsu.account.application;

import com.devsu.account.application.event.ClienteEventType;
import com.devsu.account.domain.model.ClienteReferencia;
import com.devsu.account.infrastructure.persistence.ClienteReferenciaJpaRepository;
import com.devsu.account.infrastructure.persistence.ProcessedEventJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClienteReferenciaSyncServiceTest {

    @Autowired
    private ClienteReferenciaSyncService syncService;

    @Autowired
    private ClienteReferenciaJpaRepository clienteReferenciaRepository;

    @Autowired
    private ProcessedEventJpaRepository processedEventRepository;

    @BeforeEach
    void cleanDatabase() {
        processedEventRepository.deleteAll();
        clienteReferenciaRepository.deleteAll();
    }

    @Test
    void shouldUpsertAndDeactivateClienteReferencia() {
        UUID createdEventId = UUID.randomUUID();
        String createPayload = """
                {"id":901,"nombre":"Jose Lema","identificacion":"1234567890","activo":true}
                """;

        syncService.processEvent(createdEventId, ClienteEventType.CREADO, createPayload);

        ClienteReferencia referencia = clienteReferenciaRepository.findById(901L).orElseThrow();
        assertThat(referencia.getNombre()).isEqualTo("Jose Lema");
        assertThat(referencia.isActivo()).isTrue();
        assertThat(processedEventRepository.existsById(createdEventId)).isTrue();

        UUID updatedEventId = UUID.randomUUID();
        String updatePayload = """
                {"id":901,"nombre":"Jose Lema Actualizado","identificacion":"1234567890","activo":true}
                """;
        syncService.processEvent(updatedEventId, ClienteEventType.ACTUALIZADO, updatePayload);

        assertThat(clienteReferenciaRepository.findById(901L).orElseThrow().getNombre())
                .isEqualTo("Jose Lema Actualizado");

        UUID deletedEventId = UUID.randomUUID();
        syncService.processEvent(deletedEventId, ClienteEventType.ELIMINADO, "{\"id\":901}");

        assertThat(clienteReferenciaRepository.findById(901L).orElseThrow().isActivo()).isFalse();
    }

    @Test
    void shouldIgnoreDuplicateEventId() {
        UUID eventId = UUID.randomUUID();
        String payload = """
                {"id":902,"nombre":"Marianela Montalvo","identificacion":"0987654321","activo":true}
                """;

        syncService.processEvent(eventId, ClienteEventType.CREADO, payload);
        syncService.processEvent(eventId, ClienteEventType.CREADO, payload);

        assertThat(clienteReferenciaRepository.findAll()).hasSize(1);
        assertThat(processedEventRepository.count()).isEqualTo(1);
    }
}
