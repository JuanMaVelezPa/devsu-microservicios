package com.devsu.client.api.controller;

import com.devsu.client.api.context.CorrelationContext;
import com.devsu.client.api.dto.ApiResponse;
import com.devsu.client.api.dto.ClienteRequest;
import com.devsu.client.api.dto.ClienteResponse;
import com.devsu.client.api.dto.PageResponse;
import com.devsu.client.api.mapper.ClienteApiMapper;
import com.devsu.client.application.ClienteApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteApplicationService clienteService;

    public ClienteController(ClienteApplicationService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> create(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = ClienteApiMapper.toResponse(
                clienteService.create(ClienteApiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, CorrelationContext.get()));
    }

    @GetMapping
    public ApiResponse<PageResponse<ClienteResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        clienteService.validatePagination(page, size);
        return ApiResponse.success(
                ClienteApiMapper.toPageResponse(clienteService.list(page, size)),
                CorrelationContext.get());
    }

    @GetMapping("/{id}")
    public ApiResponse<ClienteResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(
                ClienteApiMapper.toResponse(clienteService.getById(id)),
                CorrelationContext.get());
    }

    @PutMapping("/{id}")
    public ApiResponse<ClienteResponse> update(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return ApiResponse.success(
                ClienteApiMapper.toResponse(clienteService.update(id, ClienteApiMapper.toCommand(request))),
                CorrelationContext.get());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<ClienteResponse> delete(@PathVariable Long id) {
        return ApiResponse.success(
                ClienteApiMapper.toResponse(clienteService.deleteLogical(id)),
                CorrelationContext.get());
    }
}
