package com.devsu.client.application;

import com.devsu.client.application.dto.ClienteCommand;
import com.devsu.client.application.port.ClienteOutboxPort;
import com.devsu.client.application.port.ClienteRepositoryPort;
import com.devsu.client.domain.exception.ClienteDuplicadoException;
import com.devsu.client.domain.model.Cliente;
import com.devsu.client.infrastructure.observability.BusinessMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteApplicationServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ClienteOutboxPort clienteOutboxPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BusinessMetrics businessMetrics;

    @InjectMocks
    private ClienteApplicationService clienteService;

    @Test
    void shouldCreateClienteAndEnqueueOutboxEvent() {
        ClienteCommand command = sampleCommand("1234567890");
        when(clienteRepository.existsByIdentificacion("1234567890")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encoded-secret");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        var view = clienteService.create(command);

        assertThat(view.id()).isEqualTo(1L);
        assertThat(view.nombre()).isEqualTo("Jose Lema");
        assertThat(view.estado()).isTrue();
        verify(clienteOutboxPort).enqueueCreated(any(Cliente.class));
        verify(businessMetrics).incrementClienteOperacion("create");
    }

    @Test
    void shouldThrowWhenIdentificacionDuplicada() {
        when(clienteRepository.existsByIdentificacion("1234567890")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.create(sampleCommand("1234567890")))
                .isInstanceOf(ClienteDuplicadoException.class);

        verify(clienteRepository, never()).save(any());
        verify(clienteOutboxPort, never()).enqueueCreated(any());
    }

    @Test
    void shouldPerformBajaLogicaOnDelete() {
        Cliente existing = new Cliente();
        existing.setId(7L);
        existing.setNombre("Jose Lema");
        existing.setIdentificacion("1234567890");
        existing.setEstado(true);
        when(clienteRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(clienteRepository.save(existing)).thenReturn(existing);

        var view = clienteService.deleteLogical(7L);

        assertThat(view.estado()).isFalse();
        verify(clienteOutboxPort).enqueueDeleted(7L);
        verify(businessMetrics).incrementClienteOperacion("delete");
    }

    @Test
    void shouldRequireContrasenaOnCreate() {
        ClienteCommand command = new ClienteCommand(
                "Jose Lema", "1234567890", "Dir", "099", " ", true, null, null);

        assertThatThrownBy(() -> clienteService.create(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contrasena");
    }

    private static ClienteCommand sampleCommand(String identificacion) {
        return new ClienteCommand(
                "Jose Lema",
                identificacion,
                "Otavalo sn y principal",
                "098254785",
                "1234",
                true,
                null,
                null);
    }
}
