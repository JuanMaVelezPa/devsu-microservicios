package com.devsu.account.api.controller;

import com.devsu.account.api.context.CorrelationContext;
import com.devsu.account.api.dto.ApiResponse;
import com.devsu.account.api.dto.ReporteResponse;
import com.devsu.account.api.mapper.ReporteApiMapper;
import com.devsu.account.application.ReporteApplicationService;
import com.devsu.account.application.dto.ReporteQuery;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteApplicationService reporteService;

    public ReporteController(ReporteApplicationService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public ApiResponse<ReporteResponse> generate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam String cliente) {
        ReporteResponse response = ReporteApiMapper.toResponse(
                reporteService.generate(new ReporteQuery(fechaDesde, fechaHasta, cliente)));
        return ApiResponse.success(response, CorrelationContext.get());
    }
}
