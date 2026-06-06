package com.devsu.client.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Envelope estandar de respuesta API")
public record ApiResponse<T>(
        @Schema(description = "true en operaciones exitosas", example = "true")
        boolean success,
        @Schema(description = "Payload de negocio cuando success=true")
        T data,
        @Schema(description = "Detalle del error cuando success=false")
        ErrorInfo error,
        @Schema(description = "ID de trazabilidad (tambien en header X-Correlation-Id)", example = "550e8400-e29b-41d4-a716-446655440000")
        String correlationId
) {

    @Schema(description = "Codigo y mensaje de error de dominio")
    public record ErrorInfo(
            @Schema(description = "Codigo estable del error", example = "CLIENTE_DUPLICADO")
            String code,
            @Schema(description = "Mensaje legible", example = "Identificacion duplicada")
            String message
    ) {
    }

    public static <T> ApiResponse<T> success(T data, String correlationId) {
        return new ApiResponse<>(true, data, null, correlationId);
    }

    public static <T> ApiResponse<T> error(String code, String message, String correlationId) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message), correlationId);
    }
}
