package com.devsu.account.api.controller;

import com.devsu.account.api.context.CorrelationContext;
import com.devsu.account.api.dto.ApiResponse;
import com.devsu.account.api.dto.CuentaRequest;
import com.devsu.account.api.dto.CuentaResponse;
import com.devsu.account.api.dto.CuentaUpdateRequest;
import com.devsu.account.api.dto.PageResponse;
import com.devsu.account.api.mapper.CuentaApiMapper;
import com.devsu.account.application.CuentaApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cuentas", description = "CRU de cuentas bancarias (sin DELETE). Requiere cliente_referencia sincronizada por Kafka.")
public class CuentaController {

    private final CuentaApplicationService cuentaService;

    public CuentaController(CuentaApplicationService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    @Operation(summary = "Crear cuenta", description = "Valida que el clienteId exista en cliente_referencia y este activo.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cuenta creada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CLIENTE_NOT_FOUND - sin proyeccion Kafka"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "CUENTA_DUPLICADA - numeroCuenta repetido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "CLIENTE_INACTIVO")
    })
    public ResponseEntity<ApiResponse<CuentaResponse>> create(@Valid @RequestBody CuentaRequest request) {
        CuentaResponse response = CuentaApiMapper.toResponse(
                cuentaService.create(CuentaApiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, CorrelationContext.get()));
    }

    @GetMapping
    @Operation(summary = "Listar cuentas", description = "Paginacion: page (base 0), size (default 20, max 100).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado paginado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR")
    })
    public ApiResponse<PageResponse<CuentaResponse>> list(
            @Parameter(example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(example = "20") @RequestParam(defaultValue = "20") int size) {
        cuentaService.validatePagination(page, size);
        return ApiResponse.success(
                CuentaApiMapper.toPageResponse(cuentaService.list(page, size)),
                CorrelationContext.get());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CUENTA_NOT_FOUND")
    })
    public ApiResponse<CuentaResponse> getById(
            @Parameter(example = "1") @PathVariable Long id) {
        return ApiResponse.success(
                CuentaApiMapper.toResponse(cuentaService.getById(id)),
                CorrelationContext.get());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuenta", description = "Permite cambiar tipoCuenta y estado (CRU sin delete).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cuenta actualizada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CUENTA_NOT_FOUND")
    })
    public ApiResponse<CuentaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CuentaUpdateRequest request) {
        return ApiResponse.success(
                CuentaApiMapper.toResponse(
                        cuentaService.update(id, CuentaApiMapper.toUpdateCommand(request))),
                CorrelationContext.get());
    }
}
