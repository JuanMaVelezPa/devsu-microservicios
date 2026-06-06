package com.devsu.account.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClienteEventPayload(
        Long id,
        String nombre,
        String identificacion,
        Boolean activo
) {
}
