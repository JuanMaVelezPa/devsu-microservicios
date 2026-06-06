package com.devsu.client.api;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlatformApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthShouldReturnEnvelopeWithGeneratedCorrelationId() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-Id"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("client-service"))
                .andExpect(jsonPath("$.error").value(nullValue()))
                .andExpect(jsonPath("$.correlationId").isNotEmpty());

        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void healthShouldPropagateProvidedCorrelationId() throws Exception {
        String correlationId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/health").header("X-Correlation-Id", correlationId))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Correlation-Id", correlationId))
                .andExpect(jsonPath("$.correlationId").value(correlationId));
    }

    @Test
    void notFoundShouldReturnEnvelopeWithHttp404() throws Exception {
        mockMvc.perform(get("/api/_platform/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value("CLIENTE_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("Cliente no encontrado"))
                .andExpect(jsonPath("$.correlationId").isNotEmpty());
    }
}
