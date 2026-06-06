package com.devsu.client.api.controller;

import com.devsu.client.api.context.CorrelationContext;
import com.devsu.client.api.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final String serviceName;

    public HealthController(@Value("${spring.application.name}") String serviceName) {
        this.serviceName = serviceName;
    }

    @GetMapping
    public ApiResponse<HealthData> health() {
        return ApiResponse.success(new HealthData("UP", serviceName), CorrelationContext.get());
    }

    public record HealthData(String status, String service) {
    }
}
