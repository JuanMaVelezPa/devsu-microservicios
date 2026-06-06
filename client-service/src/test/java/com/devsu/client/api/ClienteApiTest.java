package com.devsu.client.api;

import com.devsu.client.infrastructure.persistence.ClienteJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteJpaRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateListUpdateAndDeleteCliente() throws Exception {
        String createBody = """
                {
                  "nombre": "Jose Lema",
                  "identificacion": "1234567890",
                  "direccion": "Otavalo sn y principal",
                  "telefono": "098254785",
                  "contrasena": "1234",
                  "estado": true
                }
                """;

        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.data.estado").value(true));

        var saved = clienteRepository.findByIdentificacion("1234567890").orElseThrow();
        assertThat(passwordEncoder.matches("1234", saved.getContrasena())).isTrue();

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));

        String updateBody = """
                {
                  "nombre": "Jose Lema Actualizado",
                  "identificacion": "1234567890",
                  "direccion": "Otavalo sn y principal",
                  "telefono": "098254785",
                  "contrasena": "",
                  "estado": true
                }
                """;

        mockMvc.perform(put("/api/clientes/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Jose Lema Actualizado"));

        assertThat(passwordEncoder.matches("1234", clienteRepository.findById(saved.getId()).orElseThrow().getContrasena()))
                .isTrue();

        mockMvc.perform(delete("/api/clientes/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value(false));
    }

    @Test
    void shouldReturn409WhenIdentificacionDuplicada() throws Exception {
        String body = """
                {
                  "nombre": "Cliente A",
                  "identificacion": "9999999999",
                  "direccion": "Calle 1",
                  "telefono": "099999999",
                  "contrasena": "1234",
                  "estado": true
                }
                """;

        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CLIENTE_DUPLICADO"));
    }
}
