package com.devsu.account.api.controller;

import com.devsu.account.api.context.CorrelationContext;
import com.devsu.account.api.dto.ApiResponse;
import com.devsu.account.api.dto.CuentaRequest;
import com.devsu.account.api.dto.CuentaResponse;
import com.devsu.account.api.dto.CuentaUpdateRequest;
import com.devsu.account.api.dto.PageResponse;
import com.devsu.account.api.mapper.CuentaApiMapper;
import com.devsu.account.application.CuentaApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaApplicationService cuentaService;

    public CuentaController(CuentaApplicationService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaResponse>> create(@Valid @RequestBody CuentaRequest request) {
        CuentaResponse response = CuentaApiMapper.toResponse(
                cuentaService.create(CuentaApiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, CorrelationContext.get()));
    }

    @GetMapping
    public ApiResponse<PageResponse<CuentaResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        cuentaService.validatePagination(page, size);
        return ApiResponse.success(
                CuentaApiMapper.toPageResponse(cuentaService.list(page, size)),
                CorrelationContext.get());
    }

    @GetMapping("/{id}")
    public ApiResponse<CuentaResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(
                CuentaApiMapper.toResponse(cuentaService.getById(id)),
                CorrelationContext.get());
    }

    @PutMapping("/{id}")
    public ApiResponse<CuentaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CuentaUpdateRequest request) {
        return ApiResponse.success(
                CuentaApiMapper.toResponse(
                        cuentaService.update(id, CuentaApiMapper.toUpdateCommand(request))),
                CorrelationContext.get());
    }
}
