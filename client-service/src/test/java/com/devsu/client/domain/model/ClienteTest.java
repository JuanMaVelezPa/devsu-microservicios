package com.devsu.client.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteTest {

    @Test
    void shouldBeActiveByDefault() {
        Cliente cliente = new Cliente();

        assertThat(cliente.isEstado()).isTrue();
    }

    @Test
    void shouldStorePersonaAndClienteFields() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Jose Lema");
        cliente.setIdentificacion("1234567890");
        cliente.setDireccion("Otavalo sn y principal");
        cliente.setTelefono("098254785");
        cliente.setContrasena("hash-bcrypt");
        cliente.setEstado(false);

        assertThat(cliente.getNombre()).isEqualTo("Jose Lema");
        assertThat(cliente.getIdentificacion()).isEqualTo("1234567890");
        assertThat(cliente.getDireccion()).isEqualTo("Otavalo sn y principal");
        assertThat(cliente.getTelefono()).isEqualTo("098254785");
        assertThat(cliente.getContrasena()).isEqualTo("hash-bcrypt");
        assertThat(cliente.isEstado()).isFalse();
    }
}
