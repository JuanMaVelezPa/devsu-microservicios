package com.devsu.account.application;

import com.devsu.account.application.dto.MovimientoCommand;
import com.devsu.account.application.port.CuentaRepositoryPort;
import com.devsu.account.application.port.MovimientoRepositoryPort;
import com.devsu.account.domain.exception.SaldoNoDisponibleException;
import com.devsu.account.domain.model.Cuenta;
import com.devsu.account.domain.model.Movimiento;
import com.devsu.account.domain.model.TipoMovimiento;
import com.devsu.account.infrastructure.observability.BusinessMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoApplicationServiceTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @Mock
    private BusinessMetrics businessMetrics;

    @InjectMocks
    private MovimientoApplicationService movimientoService;

    @Test
    void shouldRejectRetiroWhenSaldoInsuficiente() {
        Cuenta cuenta = cuentaWithSaldo("478758", new BigDecimal("100"));
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> movimientoService.register(
                new MovimientoCommand("478758", new BigDecimal("-575"), LocalDateTime.of(2022, 2, 1, 9, 0, 0))))
                .isInstanceOf(SaldoNoDisponibleException.class)
                .extracting(ex -> ((SaldoNoDisponibleException) ex).getCode())
                .isEqualTo("SALDO_NO_DISPONIBLE");

        assertThat(cuenta.getSaldo()).isEqualByComparingTo("100");
        verify(cuentaRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
        verify(businessMetrics).incrementMovimientoRechazo("saldo_insuficiente");
    }

    @Test
    void shouldRegisterDepositoAndUpdateSaldo() {
        Cuenta cuenta = cuentaWithSaldo("225487", new BigDecimal("100"));
        when(cuentaRepository.findByNumeroCuenta("225487")).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
            Movimiento saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        var view = movimientoService.register(
                new MovimientoCommand("225487", new BigDecimal("600"), LocalDateTime.of(2022, 2, 10, 14, 30, 0)));

        assertThat(view.valor()).isEqualByComparingTo("600");
        assertThat(view.tipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(view.saldoResultante()).isEqualByComparingTo("700");
        assertThat(cuenta.getSaldo()).isEqualByComparingTo("700");

        ArgumentCaptor<Movimiento> captor = ArgumentCaptor.forClass(Movimiento.class);
        verify(movimientoRepository).save(captor.capture());
        assertThat(captor.getValue().getTipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);
        verify(businessMetrics).incrementMovimiento("deposito");
    }

    private static Cuenta cuentaWithSaldo(String numeroCuenta, BigDecimal saldo) {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setSaldo(saldo);
        return cuenta;
    }
}
