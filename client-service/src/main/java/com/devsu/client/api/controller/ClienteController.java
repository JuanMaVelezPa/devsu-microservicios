package com.devsu.client.api.controller;

import com.devsu.client.api.context.CorrelationContext;
import com.devsu.client.api.dto.ApiResponse;
import com.devsu.client.api.dto.ClienteRequest;
import com.devsu.client.api.dto.ClienteResponse;
import com.devsu.client.api.dto.PageResponse;
import com.devsu.client.api.mapper.ClienteApiMapper;
import com.devsu.client.application.ClienteApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Clientes", description = "CRUD de Persona/Cliente. DELETE = baja logica (estado=false).")
public class ClienteController {

    private final ClienteApplicationService clienteService;

    public ClienteController(ClienteApplicationService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @Operation(summary = "Crear cliente", description = "Persiste el cliente, hashea la contrasena y encola evento ClienteCreado en outbox.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente creado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "CLIENTE_DUPLICADO - identificacion ya existe")
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> create(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = ClienteApiMapper.toResponse(
                clienteService.create(ClienteApiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, CorrelationContext.get()));
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Paginacion: page (base 0), size (default 20, max 100).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado paginado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR - page/size invalidos")
    })
    public ApiResponse<PageResponse<ClienteResponse>> list(
            @Parameter(description = "Numero de pagina (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Registros por pagina (max 100)", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        clienteService.validatePagination(page, size);
        return ApiResponse.success(
                ClienteApiMapper.toPageResponse(clienteService.list(page, size)),
                CorrelationContext.get());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CLIENTE_NOT_FOUND")
    })
    public ApiResponse<ClienteResponse> getById(
            @Parameter(description = "ID del cliente", example = "1")
            @PathVariable Long id) {
        return ApiResponse.success(
                ClienteApiMapper.toResponse(clienteService.getById(id)),
                CorrelationContext.get());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza datos y publica evento ClienteActualizado en outbox.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CLIENTE_NOT_FOUND"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "CLIENTE_DUPLICADO")
    })
    public ApiResponse<ClienteResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return ApiResponse.success(
                ClienteApiMapper.toResponse(clienteService.update(id, ClienteApiMapper.toCommand(request))),
                CorrelationContext.get());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Baja logica", description = "Marca estado=false y publica ClienteEliminado en outbox.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente desactivado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CLIENTE_NOT_FOUND")
    })
    public ApiResponse<ClienteResponse> delete(@PathVariable Long id) {
        return ApiResponse.success(
                ClienteApiMapper.toResponse(clienteService.deleteLogical(id)),
                CorrelationContext.get());
    }
}
