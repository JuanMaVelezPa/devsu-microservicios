package com.devsu.client.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteRequest(
        @NotBlank String nombre,
        @NotBlank String identificacion,
        @NotBlank String direccion,
        @NotBlank String telefono,
        String contrasena,
        @NotNull Boolean estado,
        String genero,
        @Min(0) @Max(150) Integer edad
) {
}
