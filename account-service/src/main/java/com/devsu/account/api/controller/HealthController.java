package com.devsu.account.api.controller;

import com.devsu.account.api.context.CorrelationContext;
import com.devsu.account.api.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Salud", description = "Verificacion basica del servicio")
public class HealthController {

    private final String serviceName;

    public HealthController(@Value("${spring.application.name}") String serviceName) {
        this.serviceName = serviceName;
    }

    @GetMapping
    @Operation(summary = "Health check", description = "Indica que la aplicacion responde. Para probes K8s/Docker usar /actuator/health.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio UP"))
    public ApiResponse<HealthData> health() {
        return ApiResponse.success(new HealthData("UP", serviceName), CorrelationContext.get());
    }

    @Schema(description = "Estado del microservicio")
    public record HealthData(
            @Schema(example = "UP") String status,
            @Schema(example = "account-service") String service
    ) {
    }
}
