package com.devsu.client.application.dto;

public record ClienteView(
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
