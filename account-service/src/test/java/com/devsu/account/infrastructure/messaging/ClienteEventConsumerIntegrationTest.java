package com.devsu.account.infrastructure.messaging;

import com.devsu.account.application.event.ClienteEventType;
import com.devsu.account.infrastructure.config.DevsuProperties;
import com.devsu.account.infrastructure.persistence.ClienteReferenciaJpaRepository;
import com.devsu.account.infrastructure.persistence.ProcessedEventJpaRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "devsu.client.events")
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.listener.auto-startup=true"
})
class ClienteEventConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private DevsuProperties devsuProperties;

    @Autowired
    private ClienteReferenciaJpaRepository clienteReferenciaRepository;

    @Autowired
    private ProcessedEventJpaRepository processedEventRepository;

    @Test
    void shouldConsumeKafkaEventAndPersistClienteReferencia() throws Exception {
        UUID eventId = UUID.randomUUID();
        String payload = """
                {"id":10,"nombre":"Cliente Kafka","identificacion":"5555555555","activo":true}
                """;

        ProducerRecord<String, String> record = new ProducerRecord<>(
                devsuProperties.getKafka().getTopicClientEvents(),
                "10",
                payload
        );
        record.headers().add("eventId", eventId.toString().getBytes(StandardCharsets.UTF_8));
        record.headers().add("eventType", ClienteEventType.CREADO.getBytes(StandardCharsets.UTF_8));
        record.headers().add("correlationId", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record).get();

        for (int attempt = 0; attempt < 20 && clienteReferenciaRepository.findById(10L).isEmpty(); attempt++) {
            Thread.sleep(500);
        }

        assertThat(clienteReferenciaRepository.findById(10L)).isPresent();
        assertThat(processedEventRepository.existsById(eventId)).isTrue();

        var referencia = clienteReferenciaRepository.findById(10L).orElseThrow();
        assertThat(referencia.getNombre()).isEqualTo("Cliente Kafka");
        assertThat(referencia.getIdentificacion()).isEqualTo("5555555555");
    }
}
