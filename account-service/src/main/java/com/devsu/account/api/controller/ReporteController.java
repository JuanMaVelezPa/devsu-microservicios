package com.devsu.account.api.controller;

import com.devsu.account.api.context.CorrelationContext;
import com.devsu.account.api.dto.ApiResponse;
import com.devsu.account.api.dto.ReporteResponse;
import com.devsu.account.api.mapper.ReporteApiMapper;
import com.devsu.account.application.ReporteApplicationService;
import com.devsu.account.application.dto.ReporteQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reporte de estado de cuenta por cliente y rango de fechas (F4 del reto / Caso 5 Anexo A).")
public class ReporteController {

    private final ReporteApplicationService reporteService;

    public ReporteController(ReporteApplicationService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    @Operation(
            summary = "Generar reporte de cuentas",
            description = """
                    Devuelve las cuentas del cliente cuyo nombre coincide exactamente (case-sensitive; trim espacios),
                    con movimientos en el rango [fechaDesde, fechaHasta] ordenados cronologicamente,
                    y saldo actual.
                    Caso 5 Anexo A: cliente=Marianela Montalvo, feb-2022 -> cuentas 225487 (saldo 700) y 496825 (saldo 0).
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reporte generado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_ERROR - rango de fechas invalido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CLIENTE_NOT_FOUND - sin coincidencia en cliente_referencia")
    })
    public ApiResponse<ReporteResponse> generate(
            @Parameter(description = "Inicio del rango (inclusive)", example = "2022-02-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @Parameter(description = "Fin del rango (inclusive)", example = "2022-02-28", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @Parameter(description = "Nombre exacto del cliente (case-sensitive; espacios al inicio/fin se ignoran)", example = "Marianela Montalvo", required = true)
            @RequestParam String cliente) {
        ReporteResponse response = ReporteApiMapper.toResponse(
                reporteService.generate(new ReporteQuery(fechaDesde, fechaHasta, cliente)));
        return ApiResponse.success(response, CorrelationContext.get());
    }
}
