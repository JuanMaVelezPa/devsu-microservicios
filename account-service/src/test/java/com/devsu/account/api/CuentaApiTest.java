package com.devsu.account.api;

import com.devsu.account.domain.model.ClienteReferencia;
import com.devsu.account.infrastructure.persistence.ClienteReferenciaJpaRepository;
import com.devsu.account.infrastructure.persistence.CuentaJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CuentaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteReferenciaJpaRepository clienteReferenciaRepository;

    @Autowired
    private CuentaJpaRepository cuentaRepository;

    @BeforeEach
    void cleanDatabase() {
        cuentaRepository.deleteAll();
        clienteReferenciaRepository.deleteAll();
        seedAnexoAClientes();
    }

    @Test
    void shouldCreateListGetAndUpdateCuenta() throws Exception {
        String createBody = """
                {
                  "clienteId": 1,
                  "numeroCuenta": "478758",
                  "tipoCuenta": "AHORROS",
                  "saldoInicial": 2000,
                  "estado": "ACTIVA"
                }
                """;

        mockMvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.numeroCuenta").value("478758"))
                .andExpect(jsonPath("$.data.saldo").value(2000))
                .andExpect(jsonPath("$.data.clienteId").value(1));

        mockMvc.perform(get("/api/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));

        var saved = cuentaRepository.findAll().getFirst();

        mockMvc.perform(get("/api/cuentas/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tipoCuenta").value("AHORROS"));

        String updateBody = """
                {
                  "tipoCuenta": "CORRIENTE",
                  "estado": "INACTIVA"
                }
                """;

        mockMvc.perform(put("/api/cuentas/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tipoCuenta").value("CORRIENTE"))
                .andExpect(jsonPath("$.data.estado").value("INACTIVA"))
                .andExpect(jsonPath("$.data.saldo").value(2000));
    }

    @Test
    void shouldCreateAnexoACuentasInicialesYCuentaAdicional() throws Exception {
        createCuenta(1, "478758", "AHORROS", 2000);
        createCuenta(2, "225487", "CORRIENTE", 100);
        createCuenta(3, "495878", "AHORROS", 0);
        createCuenta(2, "496825", "AHORROS", 540);
        createCuenta(1, "585545", "CORRIENTE", 1000);

        mockMvc.perform(get("/api/cuentas?size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(5)))
                .andExpect(jsonPath("$.data.totalElements").value(5));
    }

    @Test
    void shouldReturn422WhenClienteReferenciaNotFound() throws Exception {
        String body = """
                {
                  "clienteId": 999,
                  "numeroCuenta": "111111",
                  "tipoCuenta": "AHORROS",
                  "saldoInicial": 100
                }
                """;

        mockMvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.code").value("CLIENTE_NOT_FOUND"));
    }

    @Test
    void shouldReturn422WhenClienteReferenciaInactiva() throws Exception {
        ClienteReferencia inactiva = clienteReferenciaRepository.findById(1L).orElseThrow();
        inactiva.setActivo(false);
        clienteReferenciaRepository.save(inactiva);

        String body = """
                {
                  "clienteId": 1,
                  "numeroCuenta": "111111",
                  "tipoCuenta": "AHORROS",
                  "saldoInicial": 100
                }
                """;

        mockMvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.code").value("CLIENTE_INACTIVO"));
    }

    @Test
    void shouldReturn409WhenNumeroCuentaDuplicado() throws Exception {
        String body = """
                {
                  "clienteId": 1,
                  "numeroCuenta": "478758",
                  "tipoCuenta": "AHORROS",
                  "saldoInicial": 2000
                }
                """;

        mockMvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CUENTA_DUPLICADA"));
    }

    private void createCuenta(long clienteId, String numeroCuenta, String tipoCuenta, int saldoInicial)
            throws Exception {
        String body = """
                {
                  "clienteId": %d,
                  "numeroCuenta": "%s",
                  "tipoCuenta": "%s",
                  "saldoInicial": %d
                }
                """.formatted(clienteId, numeroCuenta, tipoCuenta, saldoInicial);

        mockMvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    private void seedAnexoAClientes() {
        saveReferencia(1L, "Jose Lema", "1234567890", true);
        saveReferencia(2L, "Marianela Montalvo", "0987654321", true);
        saveReferencia(3L, "Juan Osorio", "1122334455", true);
    }

    private void saveReferencia(Long id, String nombre, String identificacion, boolean activo) {
        ClienteReferencia referencia = new ClienteReferencia();
        referencia.setId(id);
        referencia.setNombre(nombre);
        referencia.setIdentificacion(identificacion);
        referencia.setActivo(activo);
        clienteReferenciaRepository.save(referencia);
    }
}
