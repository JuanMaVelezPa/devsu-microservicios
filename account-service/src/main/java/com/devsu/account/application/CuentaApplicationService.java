package com.devsu.account.application;

import com.devsu.account.application.dto.CuentaCommand;
import com.devsu.account.application.dto.CuentaPageView;
import com.devsu.account.application.dto.CuentaUpdateCommand;
import com.devsu.account.application.dto.CuentaView;
import com.devsu.account.application.port.ClienteReferenciaRepositoryPort;
import com.devsu.account.application.port.CuentaRepositoryPort;
import com.devsu.account.domain.exception.ClienteInactivoException;
import com.devsu.account.domain.exception.ClienteReferenciaNotFoundException;
import com.devsu.account.domain.exception.CuentaDuplicadaException;
import com.devsu.account.domain.exception.CuentaNotFoundException;
import com.devsu.account.domain.model.ClienteReferencia;
import com.devsu.account.domain.model.Cuenta;
import com.devsu.account.domain.model.EstadoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuentaApplicationService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final CuentaRepositoryPort cuentaRepository;
    private final ClienteReferenciaRepositoryPort clienteReferenciaRepository;

    public CuentaApplicationService(
            CuentaRepositoryPort cuentaRepository,
            ClienteReferenciaRepositoryPort clienteReferenciaRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteReferenciaRepository = clienteReferenciaRepository;
    }

    public CuentaView create(CuentaCommand command) {
        requireClienteReferenciaActiva(command.clienteId());
        if (cuentaRepository.existsByNumeroCuenta(command.numeroCuenta())) {
            throw new CuentaDuplicadaException();
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setClienteId(command.clienteId());
        cuenta.setNumeroCuenta(command.numeroCuenta());
        cuenta.setTipoCuenta(command.tipoCuenta());
        cuenta.setSaldo(command.saldoInicial());
        cuenta.setEstado(command.estado() != null ? command.estado() : EstadoCuenta.ACTIVA);
        return toView(cuentaRepository.save(cuenta));
    }

    @Transactional(readOnly = true)
    public CuentaPageView list(int page, int size) {
        PageRequest pageable = PageRequest.of(page, normalizeSize(size), Sort.by("id").ascending());
        Page<Cuenta> result = cuentaRepository.findAll(pageable);
        return new CuentaPageView(
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
    public CuentaView getById(Long id) {
        return toView(findCuentaOrThrow(id));
    }

    public CuentaView update(Long id, CuentaUpdateCommand command) {
        Cuenta cuenta = findCuentaOrThrow(id);
        cuenta.setTipoCuenta(command.tipoCuenta());
        cuenta.setEstado(command.estado());
        return toView(cuentaRepository.save(cuenta));
    }

    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page: debe ser >= 0");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("size: debe estar entre 1 y " + MAX_SIZE);
        }
    }

    private void requireClienteReferenciaActiva(Long clienteId) {
        ClienteReferencia referencia = clienteReferenciaRepository.findById(clienteId)
                .orElseThrow(ClienteReferenciaNotFoundException::new);
        if (!referencia.isActivo()) {
            throw new ClienteInactivoException();
        }
    }

    private Cuenta findCuentaOrThrow(Long id) {
        return cuentaRepository.findById(id).orElseThrow(CuentaNotFoundException::new);
    }

    private int normalizeSize(int size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private CuentaView toView(Cuenta cuenta) {
        return new CuentaView(
                cuenta.getId(),
                cuenta.getClienteId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(),
                cuenta.getSaldo(),
                cuenta.getEstado()
        );
    }
}
