package com.devsu.account.api.mapper;

import com.devsu.account.api.dto.CuentaReporteResponse;
import com.devsu.account.api.dto.MovimientoReporteResponse;
import com.devsu.account.api.dto.ReporteResponse;
import com.devsu.account.application.dto.CuentaReporteView;
import com.devsu.account.application.dto.MovimientoReporteView;
import com.devsu.account.application.dto.ReporteView;

public final class ReporteApiMapper {

    private ReporteApiMapper() {
    }

    public static ReporteResponse toResponse(ReporteView view) {
        return new ReporteResponse(
                view.cliente(),
                view.fechaDesde(),
                view.fechaHasta(),
                view.cuentas().stream().map(ReporteApiMapper::toCuentaResponse).toList()
        );
    }

    private static CuentaReporteResponse toCuentaResponse(CuentaReporteView view) {
        return new CuentaReporteResponse(
                view.numeroCuenta(),
                view.saldoActual(),
                view.movimientos().stream().map(ReporteApiMapper::toMovimientoResponse).toList()
        );
    }

    private static MovimientoReporteResponse toMovimientoResponse(MovimientoReporteView view) {
        return new MovimientoReporteResponse(view.fecha(), view.valor(), view.saldoResultante());
    }
}
