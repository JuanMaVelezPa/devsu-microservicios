package com.devsu.account.api;

import com.devsu.account.domain.model.ClienteReferencia;
import com.devsu.account.domain.model.Cuenta;
import com.devsu.account.domain.model.EstadoCuenta;
import com.devsu.account.domain.model.Movimiento;
import com.devsu.account.domain.model.TipoCuenta;
import com.devsu.account.domain.model.TipoMovimiento;
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
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReporteApiTest {

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
        seedAnexoAMovimientos();
    }

    @Test
    void shouldGenerateAnexoACaso5ReportForMarianelaMontalvo() throws Exception {
        mockMvc.perform(get("/api/reportes")
                        .param("fechaDesde", "2022-02-01")
                        .param("fechaHasta", "2022-02-28")
                        .param("cliente", "Marianela Montalvo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cliente").value("Marianela Montalvo"))
                .andExpect(jsonPath("$.data.fechaDesde").value("2022-02-01"))
                .andExpect(jsonPath("$.data.fechaHasta").value("2022-02-28"))
                .andExpect(jsonPath("$.data.cuentas", hasSize(2)))
                .andExpect(jsonPath("$.data.cuentas[0].numeroCuenta").value("225487"))
                .andExpect(jsonPath("$.data.cuentas[0].saldoActual").value(700))
                .andExpect(jsonPath("$.data.cuentas[0].movimientos", hasSize(1)))
                .andExpect(jsonPath("$.data.cuentas[0].movimientos[0].fecha").value("2022-02-10"))
                .andExpect(jsonPath("$.data.cuentas[0].movimientos[0].valor").value(600))
                .andExpect(jsonPath("$.data.cuentas[0].movimientos[0].saldoResultante").value(700))
                .andExpect(jsonPath("$.data.cuentas[1].numeroCuenta").value("496825"))
                .andExpect(jsonPath("$.data.cuentas[1].saldoActual").value(0))
                .andExpect(jsonPath("$.data.cuentas[1].movimientos", hasSize(1)))
                .andExpect(jsonPath("$.data.cuentas[1].movimientos[0].fecha").value("2022-02-08"))
                .andExpect(jsonPath("$.data.cuentas[1].movimientos[0].valor").value(-540))
                .andExpect(jsonPath("$.data.cuentas[1].movimientos[0].saldoResultante").value(0));
    }

    @Test
    void shouldFindClienteByNameCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/reportes")
                        .param("fechaDesde", "2022-02-01")
                        .param("fechaHasta", "2022-02-28")
                        .param("cliente", "marianela montalvo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cliente").value("Marianela Montalvo"))
                .andExpect(jsonPath("$.data.cuentas", hasSize(2)));
    }

    @Test
    void shouldIncludeCuentasWithoutMovimientosInRange() throws Exception {
        mockMvc.perform(get("/api/reportes")
                        .param("fechaDesde", "2022-02-01")
                        .param("fechaHasta", "2022-02-28")
                        .param("cliente", "Jose Lema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cuentas", hasSize(2)))
                .andExpect(jsonPath("$.data.cuentas[0].numeroCuenta").value("478758"))
                .andExpect(jsonPath("$.data.cuentas[0].movimientos", hasSize(1)))
                .andExpect(jsonPath("$.data.cuentas[1].numeroCuenta").value("585545"))
                .andExpect(jsonPath("$.data.cuentas[1].movimientos", hasSize(0)));
    }

    @Test
    void shouldReturn404WhenClienteNotFound() throws Exception {
        mockMvc.perform(get("/api/reportes")
                        .param("fechaDesde", "2022-02-01")
                        .param("fechaHasta", "2022-02-28")
                        .param("cliente", "Cliente Inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("CLIENTE_NOT_FOUND"));
    }

    @Test
    void shouldExecuteFullAnexoAFlowViaApi() throws Exception {
        movimientoRepository.deleteAll();
        cuentaRepository.deleteAll();
        clienteReferenciaRepository.deleteAll();

        createClienteViaApi();
        createCuentasViaApi();
        createMovimientosViaApi();

        mockMvc.perform(get("/api/reportes")
                        .param("fechaDesde", "2022-02-01")
                        .param("fechaHasta", "2022-02-28")
                        .param("cliente", "Marianela Montalvo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cuentas[0].numeroCuenta").value("225487"))
                .andExpect(jsonPath("$.data.cuentas[0].saldoActual").value(700))
                .andExpect(jsonPath("$.data.cuentas[1].numeroCuenta").value("496825"))
                .andExpect(jsonPath("$.data.cuentas[1].saldoActual").value(0));
    }

    private void seedAnexoAData() {
        saveReferencia(1L, "Jose Lema", "1234567890");
        saveReferencia(2L, "Marianela Montalvo", "0987654321");
        saveReferencia(3L, "Juan Osorio", "1122334455");

        saveCuenta(1L, "478758", TipoCuenta.AHORROS, new BigDecimal("1425"));
        saveCuenta(2L, "225487", TipoCuenta.CORRIENTE, new BigDecimal("700"));
        saveCuenta(3L, "495878", TipoCuenta.AHORROS, new BigDecimal("150"));
        saveCuenta(2L, "496825", TipoCuenta.AHORROS, BigDecimal.ZERO);
        saveCuenta(1L, "585545", TipoCuenta.CORRIENTE, new BigDecimal("1000"));
    }

    private void seedAnexoAMovimientos() {
        saveMovimiento("478758", LocalDate.of(2022, 2, 1), TipoMovimiento.RETIRO, new BigDecimal("-575"), new BigDecimal("1425"));
        saveMovimiento("225487", LocalDate.of(2022, 2, 10), TipoMovimiento.DEPOSITO, new BigDecimal("600"), new BigDecimal("700"));
        saveMovimiento("495878", LocalDate.of(2022, 2, 5), TipoMovimiento.DEPOSITO, new BigDecimal("150"), new BigDecimal("150"));
        saveMovimiento("496825", LocalDate.of(2022, 2, 8), TipoMovimiento.RETIRO, new BigDecimal("-540"), BigDecimal.ZERO);
    }

    private void createClienteViaApi() throws Exception {
        saveReferencia(1L, "Jose Lema", "1234567890");
        saveReferencia(2L, "Marianela Montalvo", "0987654321");
        saveReferencia(3L, "Juan Osorio", "1122334455");
    }

    private void createCuentasViaApi() throws Exception {
        createCuenta(1, "478758", "AHORROS", 2000);
        createCuenta(2, "225487", "CORRIENTE", 100);
        createCuenta(3, "495878", "AHORROS", 0);
        createCuenta(2, "496825", "AHORROS", 540);
        createCuenta(1, "585545", "CORRIENTE", 1000);
    }

    private void createMovimientosViaApi() throws Exception {
        registerMovimiento("478758", -575, "2022-02-01");
        registerMovimiento("225487", 600, "2022-02-10");
        registerMovimiento("495878", 150, "2022-02-05");
        registerMovimiento("496825", -540, "2022-02-08");
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

    private void registerMovimiento(String numeroCuenta, int valor, String fecha) throws Exception {
        String body = """
                {
                  "numeroCuenta": "%s",
                  "valor": %d,
                  "fecha": "%s"
                }
                """.formatted(numeroCuenta, valor, fecha);

        mockMvc.perform(post("/api/movimientos").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
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

    private void saveMovimiento(
            String numeroCuenta,
            LocalDate fecha,
            TipoMovimiento tipoMovimiento,
            BigDecimal valor,
            BigDecimal saldoResultante) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta).orElseThrow();
        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaId(cuenta.getId());
        movimiento.setFecha(fecha);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setValor(valor);
        movimiento.setSaldoResultante(saldoResultante);
        movimientoRepository.save(movimiento);
    }
}
