package com.devsu.account.application;

import com.devsu.account.application.dto.MovimientoCommand;
import com.devsu.account.application.dto.MovimientoPageView;
import com.devsu.account.application.dto.MovimientoView;
import com.devsu.account.application.port.CuentaRepositoryPort;
import com.devsu.account.application.port.MovimientoRepositoryPort;
import com.devsu.account.domain.exception.CuentaNotFoundException;
import com.devsu.account.domain.exception.MovimientoNotFoundException;
import com.devsu.account.domain.exception.SaldoNoDisponibleException;
import com.devsu.account.domain.model.Cuenta;
import com.devsu.account.domain.model.Movimiento;
import com.devsu.account.domain.model.TipoMovimiento;
import com.devsu.account.infrastructure.observability.BusinessMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovimientoApplicationService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final MovimientoRepositoryPort movimientoRepository;
    private final CuentaRepositoryPort cuentaRepository;
    private final BusinessMetrics businessMetrics;

    public MovimientoApplicationService(
            MovimientoRepositoryPort movimientoRepository,
            CuentaRepositoryPort cuentaRepository,
            BusinessMetrics businessMetrics) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.businessMetrics = businessMetrics;
    }

    public MovimientoView register(MovimientoCommand command) {
        requireValorDistintoDeCero(command.valor());

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(command.numeroCuenta())
                .orElseThrow(CuentaNotFoundException::new);

        LocalDateTime fecha = command.fecha() != null ? command.fecha() : LocalDateTime.now();
        BigDecimal saldoResultante = cuenta.getSaldo().add(command.valor());
        TipoMovimiento tipoMovimiento = resolveTipoMovimiento(command.valor());

        if (tipoMovimiento == TipoMovimiento.RETIRO && saldoResultante.compareTo(BigDecimal.ZERO) < 0) {
            businessMetrics.incrementMovimientoRechazo("saldo_insuficiente");
            throw new SaldoNoDisponibleException();
        }

        cuenta.setSaldo(saldoResultante);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaId(cuenta.getId());
        movimiento.setFecha(fecha);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setValor(command.valor());
        movimiento.setSaldoResultante(saldoResultante);

        businessMetrics.incrementMovimiento(tipoMovimiento.name().toLowerCase());
        return toView(movimientoRepository.save(movimiento), cuenta.getNumeroCuenta());
    }

    @Transactional(readOnly = true)
    public MovimientoPageView list(int page, int size) {
        PageRequest pageable = PageRequest.of(page, normalizeSize(size), Sort.by("fecha").ascending().and(Sort.by("id").ascending()));
        Page<Movimiento> result = movimientoRepository.findAll(pageable);
        Map<Long, String> numerosCuenta = resolveNumerosCuenta(result.getContent());

        return new MovimientoPageView(
                result.getContent().stream()
                        .map(movimiento -> toView(movimiento, numerosCuenta.get(movimiento.getCuentaId())))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @Transactional(readOnly = true)
    public MovimientoView getById(Long id) {
        Movimiento movimiento = findMovimientoOrThrow(id);
        String numeroCuenta = cuentaRepository.findById(movimiento.getCuentaId())
                .map(Cuenta::getNumeroCuenta)
                .orElseThrow(CuentaNotFoundException::new);
        return toView(movimiento, numeroCuenta);
    }

    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page: debe ser >= 0");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("size: debe estar entre 1 y " + MAX_SIZE);
        }
    }

    private void requireValorDistintoDeCero(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("valor: no puede ser 0");
        }
    }

    private TipoMovimiento resolveTipoMovimiento(BigDecimal valor) {
        return valor.compareTo(BigDecimal.ZERO) > 0 ? TipoMovimiento.DEPOSITO : TipoMovimiento.RETIRO;
    }

    private Movimiento findMovimientoOrThrow(Long id) {
        return movimientoRepository.findById(id).orElseThrow(MovimientoNotFoundException::new);
    }

    private Map<Long, String> resolveNumerosCuenta(List<Movimiento> movimientos) {
        Set<Long> cuentaIds = movimientos.stream()
                .map(Movimiento::getCuentaId)
                .collect(Collectors.toSet());

        return cuentaIds.stream()
                .map(cuentaRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toMap(Cuenta::getId, Cuenta::getNumeroCuenta));
    }

    private int normalizeSize(int size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private MovimientoView toView(Movimiento movimiento, String numeroCuenta) {
        return new MovimientoView(
                movimiento.getId(),
                movimiento.getCuentaId(),
                numeroCuenta,
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                movimiento.getValor(),
                movimiento.getSaldoResultante()
        );
    }
}
