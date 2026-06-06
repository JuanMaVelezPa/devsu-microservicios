package com.devsu.client.application;

import com.devsu.client.application.dto.ClienteCommand;
import com.devsu.client.application.dto.ClientePageView;
import com.devsu.client.application.dto.ClienteView;
import com.devsu.client.application.port.ClienteRepositoryPort;
import com.devsu.client.domain.exception.ClienteDuplicadoException;
import com.devsu.client.domain.exception.ClienteNotFoundException;
import com.devsu.client.domain.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteApplicationService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final ClienteRepositoryPort clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteApplicationService(ClienteRepositoryPort clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ClienteView create(ClienteCommand command) {
        requireContrasenaOnCreate(command.contrasena());
        if (clienteRepository.existsByIdentificacion(command.identificacion())) {
            throw new ClienteDuplicadoException();
        }
        Cliente cliente = mapToEntity(new Cliente(), command);
        cliente.setContrasena(passwordEncoder.encode(command.contrasena()));
        return toView(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClientePageView list(int page, int size) {
        PageRequest pageable = PageRequest.of(page, normalizeSize(size), Sort.by("id").ascending());
        Page<Cliente> result = clienteRepository.findAll(pageable);
        return new ClientePageView(
                result.getContent().stream().map(this::toView).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @Transactional(readOnly = true)
    public ClienteView getById(Long id) {
        return toView(findClienteOrThrow(id));
    }

    public ClienteView update(Long id, ClienteCommand command) {
        Cliente cliente = findClienteOrThrow(id);
        validateIdentificacionUnica(cliente, command.identificacion());
        applyPersonaFields(cliente, command);
        if (command.contrasena() != null && !command.contrasena().isBlank()) {
            cliente.setContrasena(passwordEncoder.encode(command.contrasena()));
        }
        cliente.setEstado(command.estado());
        return toView(clienteRepository.save(cliente));
    }

    public ClienteView deleteLogical(Long id) {
        Cliente cliente = findClienteOrThrow(id);
        cliente.setEstado(false);
        return toView(clienteRepository.save(cliente));
    }

    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page: debe ser >= 0");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("size: debe estar entre 1 y " + MAX_SIZE);
        }
    }

    private Cliente findClienteOrThrow(Long id) {
        return clienteRepository.findById(id).orElseThrow(ClienteNotFoundException::new);
    }

    private void validateIdentificacionUnica(Cliente cliente, String identificacion) {
        clienteRepository.findByIdentificacion(identificacion)
                .filter(existing -> !existing.getId().equals(cliente.getId()))
                .ifPresent(existing -> {
                    throw new ClienteDuplicadoException();
                });
    }

    private void requireContrasenaOnCreate(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("contrasena: es obligatoria");
        }
    }

    private int normalizeSize(int size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private Cliente mapToEntity(Cliente cliente, ClienteCommand command) {
        applyPersonaFields(cliente, command);
        cliente.setEstado(command.estado());
        return cliente;
    }

    private void applyPersonaFields(Cliente cliente, ClienteCommand command) {
        cliente.setNombre(command.nombre());
        cliente.setIdentificacion(command.identificacion());
        cliente.setDireccion(command.direccion());
        cliente.setTelefono(command.telefono());
        cliente.setGenero(command.genero());
        cliente.setEdad(command.edad());
    }

    private ClienteView toView(Cliente cliente) {
        return new ClienteView(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getIdentificacion(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.isEstado()
        );
    }
}
