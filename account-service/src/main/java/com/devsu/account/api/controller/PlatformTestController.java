package com.devsu.account.api.controller;

import com.devsu.account.domain.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/_platform")
public class PlatformTestController {

    @GetMapping("/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("CUENTA_NOT_FOUND", "Cuenta no encontrada");
    }
}
