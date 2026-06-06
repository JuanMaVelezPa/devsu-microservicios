package com.devsu.client.application.dto;

public record ClienteCommand(
        String nombre,
        String identificacion,
        String direccion,
        String telefono,
        String contrasena,
        Boolean estado,
        String genero,
        Integer edad
) {
}
