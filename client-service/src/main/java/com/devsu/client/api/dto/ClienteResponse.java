package com.devsu.client.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cliente persistido (sin contrasena)")
public record ClienteResponse(
        @Schema(description = "Identificador interno", example = "1")
        Long id,
        @Schema(example = "Jose Lema")
        String nombre,
        @Schema(example = "1234567890")
        String identificacion,
        @Schema(example = "Otavalo sn y principal")
        String direccion,
        @Schema(example = "098254785")
        String telefono,
        @Schema(example = "M")
        String genero,
        @Schema(example = "35")
        Integer edad,
        @Schema(description = "true = activo, false = baja logica", example = "true")
        boolean estado
) {
}
