package com.devsu.account.api.mapper;

import com.devsu.account.api.dto.CuentaRequest;
import com.devsu.account.api.dto.CuentaResponse;
import com.devsu.account.api.dto.CuentaUpdateRequest;
import com.devsu.account.api.dto.PageResponse;
import com.devsu.account.application.dto.CuentaCommand;
import com.devsu.account.application.dto.CuentaPageView;
import com.devsu.account.application.dto.CuentaUpdateCommand;
import com.devsu.account.application.dto.CuentaView;

public final class CuentaApiMapper {

    private CuentaApiMapper() {
    }

    public static CuentaCommand toCommand(CuentaRequest request) {
        return new CuentaCommand(
                request.clienteId(),
                request.numeroCuenta(),
                request.tipoCuenta(),
                request.saldoInicial(),
                request.estado()
        );
    }

    public static CuentaUpdateCommand toUpdateCommand(CuentaUpdateRequest request) {
        return new CuentaUpdateCommand(request.tipoCuenta(), request.estado());
    }

    public static CuentaResponse toResponse(CuentaView view) {
        return new CuentaResponse(
                view.id(),
                view.clienteId(),
                view.numeroCuenta(),
                view.tipoCuenta(),
                view.saldo(),
                view.estado()
        );
    }

    public static PageResponse<CuentaResponse> toPageResponse(CuentaPageView page) {
        return new PageResponse<>(
                page.content().stream().map(CuentaApiMapper::toResponse).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.first(),
                page.last()
        );
    }
}
