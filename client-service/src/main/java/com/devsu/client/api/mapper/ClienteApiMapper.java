package com.devsu.client.api.mapper;

import com.devsu.client.api.dto.ClienteRequest;
import com.devsu.client.api.dto.ClienteResponse;
import com.devsu.client.api.dto.PageResponse;
import com.devsu.client.application.dto.ClienteCommand;
import com.devsu.client.application.dto.ClientePageView;
import com.devsu.client.application.dto.ClienteView;

public final class ClienteApiMapper {

    private ClienteApiMapper() {
    }

    public static ClienteCommand toCommand(ClienteRequest request) {
        return new ClienteCommand(
                request.nombre(),
                request.identificacion(),
                request.direccion(),
                request.telefono(),
                request.contrasena(),
                request.estado(),
                request.genero(),
                request.edad()
        );
    }

    public static ClienteResponse toResponse(ClienteView view) {
        return new ClienteResponse(
                view.id(),
                view.nombre(),
                view.identificacion(),
                view.direccion(),
                view.telefono(),
                view.genero(),
                view.edad(),
                view.estado()
        );
    }

    public static PageResponse<ClienteResponse> toPageResponse(ClientePageView page) {
        return new PageResponse<>(
                page.content().stream().map(ClienteApiMapper::toResponse).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.first(),
                page.last()
        );
    }
}
