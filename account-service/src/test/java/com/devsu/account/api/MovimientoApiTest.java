package com.devsu.account.api;

import com.devsu.account.domain.model.ClienteReferencia;
import com.devsu.account.domain.model.Cuenta;
import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.TipoCuenta;
import com.devsu.account.infrastructure.persistence.ClienteReferenciaJpaRepository;
import com.devsu.account.infrastructure.persistence.CuentaJpaRepository;
import com.devsu.account.infrastructure.persistence.MovimientoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MovimientoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteReferenciaJpaRepository clienteReferenciaRepository;

    @Autowired
    private CuentaJpaRepository cuentaRepository;

    @Autowired
    private MovimientoJpaRepository movimientoRepository;

    @BeforeEach
    void cleanDatabase() {
        movimientoRepository.deleteAll();
        cuentaRepository.deleteAll();
        clienteReferenciaRepository.deleteAll();
        seedAnexoAData();
    }

    @Test
    void shouldRegisterDepositoAndRetiroUpdatingSaldo() throws Exception {
        registerMovimiento("478758", -575, "2022-02-01")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tipoMovimiento").value("RETIRO"))
                .andExpect(jsonPath("$.data.valor").value(-575))
                .andExpect(jsonPath("$.data.saldoResultante").value(1425));

        assertThat(cuentaRepository.findByNumeroCuenta("478758").orElseThrow().getSaldo())
                .isEqualByComparingTo(new BigDecimal("1425"));

        registerMovimiento("225487", 600, "2022-02-10")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.data.saldoResultante").value(700));

        mockMvc.perform(get("/api/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void shouldExecuteAnexoACaso4Movimientos() throws Exception {
        registerMovimiento("478758", -575, "2022-02-01").andExpect(status().isCreated());
        registerMovimiento("225487", 600, "2022-02-10").andExpect(status().isCreated());
        registerMovimiento("495878", 150, "2022-02-05").andExpect(status().isCreated());
        registerMovimiento("496825", -540, "2022-02-08").andExpect(status().isCreated());

        assertThat(cuentaRepository.findByNumeroCuenta("478758").orElseThrow().getSaldo())
                .isEqualByComparingTo(new BigDecimal("1425"));
        assertThat(cuentaRepository.findByNumeroCuenta("225487").orElseThrow().getSaldo())
                .isEqualByComparingTo(new BigDecimal("700"));
        assertThat(cuentaRepository.findByNumeroCuenta("495878").orElseThrow().getSaldo())
                .isEqualByComparingTo(new BigDecimal("150"));
        assertThat(cuentaRepository.findByNumeroCuenta("496825").orElseThrow().getSaldo())
                .isEqualByComparingTo(BigDecimal.ZERO);

        mockMvc.perform(get("/api/movimientos?size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(4));
    }

    @Test
    void shouldReturn422WhenSaldoInsuficienteWithoutPersistingMovimiento() throws Exception {
        BigDecimal saldoInicial = cuentaRepository.findByNumeroCuenta("496825").orElseThrow().getSaldo();

        registerMovimiento("496825", -541, "2022-02-08")
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.code").value("SALDO_NO_DISPONIBLE"))
                .andExpect(jsonPath("$.error.message").value("Saldo no disponible"));

        assertThat(movimientoRepository.count()).isZero();
        assertThat(cuentaRepository.findByNumeroCuenta("496825").orElseThrow().getSaldo())
                .isEqualByComparingTo(saldoInicial);
    }

    @Test
    void shouldReturn404WhenCuentaNotFound() throws Exception {
        registerMovimiento("000000", 100, "2022-02-01")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("CUENTA_NOT_FOUND"));
    }

    @Test
    void shouldReturn400WhenValorIsZero() throws Exception {
        String body = """
                {
                  "numeroCuenta": "478758",
                  "valor": 0,
                  "fecha": "2022-02-01"
                }
                """;

        mockMvc.perform(post("/api/movimientos").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    private org.springframework.test.web.servlet.ResultActions registerMovimiento(
            String numeroCuenta, int valor, String fecha) throws Exception {
        String body = """
                {
                  "numeroCuenta": "%s",
                  "valor": %d,
                  "fecha": "%s"
                }
                """.formatted(numeroCuenta, valor, fecha);

        return mockMvc.perform(post("/api/movimientos").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private void seedAnexoAData() {
        saveReferencia(1L, "Jose Lema", "1234567890");
        saveReferencia(2L, "Marianela Montalvo", "0987654321");
        saveReferencia(3L, "Juan Osorio", "1122334455");

        saveCuenta(1L, "478758", TipoCuenta.AHORROS, new BigDecimal("2000"));
        saveCuenta(2L, "225487", TipoCuenta.CORRIENTE, new BigDecimal("100"));
        saveCuenta(3L, "495878", TipoCuenta.AHORROS, BigDecimal.ZERO);
        saveCuenta(2L, "496825", TipoCuenta.AHORROS, new BigDecimal("540"));
    }

    private void saveReferencia(Long id, String nombre, String identificacion) {
        ClienteReferencia referencia = new ClienteReferencia();
        referencia.setId(id);
        referencia.setNombre(nombre);
        referencia.setIdentificacion(identificacion);
        referencia.setActivo(true);
        clienteReferenciaRepository.save(referencia);
    }

    private void saveCuenta(Long clienteId, String numeroCuenta, TipoCuenta tipoCuenta, BigDecimal saldo) {
        Cuenta cuenta = new Cuenta();
        cuenta.setClienteId(clienteId);
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setSaldo(saldo);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuentaRepository.save(cuenta);
    }
}
