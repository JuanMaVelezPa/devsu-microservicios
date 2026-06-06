package com.devsu.account.api.mapper;

import com.devsu.account.api.dto.MovimientoRequest;
import com.devsu.account.api.dto.MovimientoResponse;
import com.devsu.account.api.dto.PageResponse;
import com.devsu.account.application.dto.MovimientoCommand;
import com.devsu.account.application.dto.MovimientoPageView;
import com.devsu.account.application.dto.MovimientoView;

public final class MovimientoApiMapper {

    private MovimientoApiMapper() {
    }

    public static MovimientoCommand toCommand(MovimientoRequest request) {
        return new MovimientoCommand(request.numeroCuenta(), request.valor(), request.fecha());
    }

    public static MovimientoResponse toResponse(MovimientoView view) {
        return new MovimientoResponse(
                view.id(),
                view.cuentaId(),
                view.numeroCuenta(),
                view.fecha(),
                view.tipoMovimiento(),
                view.valor(),
                view.saldoResultante()
        );
    }

    public static PageResponse<MovimientoResponse> toPageResponse(MovimientoPageView page) {
        return new PageResponse<>(
                page.content().stream().map(MovimientoApiMapper::toResponse).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.first(),
                page.last()
        );
    }
}
