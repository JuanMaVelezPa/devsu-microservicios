package com.devsu.account.api.controller;

import com.devsu.account.api.context.CorrelationContext;
import com.devsu.account.api.dto.ApiResponse;
import com.devsu.account.api.dto.MovimientoRequest;
import com.devsu.account.api.dto.MovimientoResponse;
import com.devsu.account.api.dto.PageResponse;
import com.devsu.account.api.mapper.MovimientoApiMapper;
import com.devsu.account.application.MovimientoApplicationService;
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
public class MovimientoController {

    private final MovimientoApplicationService movimientoService;

    public MovimientoController(MovimientoApplicationService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoResponse>> register(@Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse response = MovimientoApiMapper.toResponse(
                movimientoService.register(MovimientoApiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, CorrelationContext.get()));
    }

    @GetMapping
    public ApiResponse<PageResponse<MovimientoResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        movimientoService.validatePagination(page, size);
        return ApiResponse.success(
                MovimientoApiMapper.toPageResponse(movimientoService.list(page, size)),
                CorrelationContext.get());
    }

    @GetMapping("/{id}")
    public ApiResponse<MovimientoResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(
                MovimientoApiMapper.toResponse(movimientoService.getById(id)),
                CorrelationContext.get());
    }
}
