package com.devsu.account.application;

import com.devsu.account.application.dto.CuentaReporteView;
import com.devsu.account.application.dto.MovimientoReporteView;
import com.devsu.account.application.dto.ReporteQuery;
import com.devsu.account.application.dto.ReporteView;
import com.devsu.account.application.port.ClienteReferenciaRepositoryPort;
import com.devsu.account.application.port.CuentaRepositoryPort;
import com.devsu.account.application.port.MovimientoRepositoryPort;
import com.devsu.account.domain.exception.ClienteNotFoundException;
import com.devsu.account.domain.model.ClienteReferencia;
import com.devsu.account.domain.model.Cuenta;
import com.devsu.account.domain.model.Movimiento;
import com.devsu.account.infrastructure.observability.BusinessMetrics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteApplicationService {

    private final ClienteReferenciaRepositoryPort clienteReferenciaRepository;
    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;
    private final BusinessMetrics businessMetrics;

    public ReporteApplicationService(
            ClienteReferenciaRepositoryPort clienteReferenciaRepository,
            CuentaRepositoryPort cuentaRepository,
            MovimientoRepositoryPort movimientoRepository,
            BusinessMetrics businessMetrics) {
        this.clienteReferenciaRepository = clienteReferenciaRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.businessMetrics = businessMetrics;
    }

    public ReporteView generate(ReporteQuery query) {
        validateQuery(query);

        ClienteReferencia cliente = clienteReferenciaRepository.findByNombreIgnoreCase(query.cliente().trim())
                .orElseThrow(ClienteNotFoundException::new);

        List<CuentaReporteView> cuentas = cuentaRepository.findByClienteIdOrderByIdAsc(cliente.getId()).stream()
                .map(cuenta -> toCuentaReporteView(cuenta, query))
                .toList();

        businessMetrics.incrementReporteGenerado();
        return new ReporteView(
                cliente.getNombre(),
                query.fechaDesde(),
                query.fechaHasta(),
                cuentas
        );
    }

    private void validateQuery(ReporteQuery query) {
        if (query.fechaDesde() == null) {
            throw new IllegalArgumentException("fechaDesde: es obligatoria");
        }
        if (query.fechaHasta() == null) {
            throw new IllegalArgumentException("fechaHasta: es obligatoria");
        }
        if (query.cliente() == null || query.cliente().isBlank()) {
            throw new IllegalArgumentException("cliente: es obligatorio");
        }
        if (query.fechaDesde().isAfter(query.fechaHasta())) {
            throw new IllegalArgumentException("fechaDesde: no puede ser posterior a fechaHasta");
        }
    }

    private CuentaReporteView toCuentaReporteView(Cuenta cuenta, ReporteQuery query) {
        List<MovimientoReporteView> movimientos = movimientoRepository
                .findByCuentaIdAndFechaBetweenOrderByFechaAscIdAsc(
                        cuenta.getId(), query.fechaDesde(), query.fechaHasta())
                .stream()
                .map(this::toMovimientoReporteView)
                .toList();

        return new CuentaReporteView(cuenta.getNumeroCuenta(), cuenta.getSaldo(), movimientos);
    }

    private MovimientoReporteView toMovimientoReporteView(Movimiento movimiento) {
        return new MovimientoReporteView(
                movimiento.getFecha(),
                movimiento.getValor(),
                movimiento.getSaldoResultante()
        );
    }
}
