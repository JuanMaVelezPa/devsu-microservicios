package com.devsu.account.api.controller;

import com.devsu.account.api.context.CorrelationContext;
import com.devsu.account.api.dto.ApiResponse;
import com.devsu.account.api.dto.MovimientoRequest;
import com.devsu.account.api.dto.MovimientoResponse;
import com.devsu.account.api.dto.PageResponse;
import com.devsu.account.api.mapper.MovimientoApiMapper;
import com.devsu.account.application.MovimientoApplicationService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimientos")
@Tag(name = "Movimientos", description = "Registro de depositos y retiros. F3: retiro sin saldo -> HTTP 422 SALDO_NO_DISPONIBLE.")
public class MovimientoController {

    private final MovimientoApplicationService movimientoService;

    public MovimientoController(MovimientoApplicationService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar movimiento",
            description = """
                    Aplica un deposito (valor > 0) o retiro (valor < 0) sobre la cuenta indicada.
                    Actualiza el saldo y persiste el historial. Regla F3: si el retiro excede el saldo,
                    responde 422 con error.code = SALDO_NO_DISPONIBLE y mensaje exacto del reto.
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Movimiento registrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR - valor=0 u otros campos invalidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CUENTA_NOT_FOUND"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422", description = "SALDO_NO_DISPONIBLE - retiro sin fondos (F3)")
    })
    public ResponseEntity<ApiResponse<MovimientoResponse>> register(@Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse response = MovimientoApiMapper.toResponse(
                movimientoService.register(MovimientoApiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, CorrelationContext.get()));
    }

    @GetMapping
    @Operation(summary = "Listar movimientos")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado paginado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR")
    })
    public ApiResponse<PageResponse<MovimientoResponse>> list(
            @Parameter(example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(example = "20") @RequestParam(defaultValue = "20") int size) {
        movimientoService.validatePagination(page, size);
        return ApiResponse.success(
                MovimientoApiMapper.toPageResponse(movimientoService.list(page, size)),
                CorrelationContext.get());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MOVIMIENTO_NOT_FOUND")
    })
    public ApiResponse<MovimientoResponse> getById(
            @Parameter(example = "1") @PathVariable Long id) {
        return ApiResponse.success(
                MovimientoApiMapper.toResponse(movimientoService.getById(id)),
                CorrelationContext.get());
    }
}
