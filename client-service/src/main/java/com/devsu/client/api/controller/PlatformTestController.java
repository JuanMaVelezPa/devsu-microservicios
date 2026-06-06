package com.devsu.client.api.controller;

import com.devsu.client.domain.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/api/_platform")
public class PlatformTestController {

    @GetMapping("/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("CLIENTE_NOT_FOUND", "Cliente no encontrado");
    }
}
