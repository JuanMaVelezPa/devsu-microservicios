package com.devsu.client.api.dto;

public record ClienteResponse(
        Long id,
        String nombre,
        String identificacion,
        String direccion,
        String telefono,
        String genero,
        Integer edad,
        boolean estado
) {
}
