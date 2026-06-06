package com.devsu.client.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para crear o actualizar un cliente")
public record ClienteRequest(
        @Schema(description = "Nombre completo del cliente", example = "Jose Lema")
        @NotBlank String nombre,
        @Schema(description = "Identificacion unica (UK)", example = "1234567890")
        @NotBlank String identificacion,
        @Schema(description = "Direccion de residencia", example = "Otavalo sn y principal")
        @NotBlank String direccion,
        @Schema(description = "Telefono de contacto", example = "098254785")
        @NotBlank String telefono,
        @Schema(description = "Contrasena en texto plano; se persiste hasheada con BCrypt. No se devuelve en GET.", example = "1234")
        String contrasena,
        @Schema(description = "Estado activo/inactivo del cliente", example = "true")
        @NotNull Boolean estado,
        @Schema(description = "Genero (opcional)", example = "M")
        String genero,
        @Schema(description = "Edad en anos (opcional, 0-150)", example = "35")
        @Min(0) @Max(150) Integer edad
) {
}
