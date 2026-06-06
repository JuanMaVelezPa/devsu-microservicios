-- Devsu microservicios bancarios - DDL (ADR-16, sin seed)
-- PostgreSQL: devsu_db | schemas: client, account

CREATE SCHEMA IF NOT EXISTS client;
CREATE SCHEMA IF NOT EXISTS account;

-- ---------------------------------------------------------------------------
-- Schema client (client-service)
-- ---------------------------------------------------------------------------

CREATE TABLE client.persona (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    genero          VARCHAR(20),
    edad            INTEGER CHECK (edad IS NULL OR (edad >= 0 AND edad <= 150)),
    identificacion  VARCHAR(20) NOT NULL,
    direccion       VARCHAR(255),
    telefono        VARCHAR(20),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_persona_identificacion UNIQUE (identificacion)
);

CREATE TABLE client.cliente (
    id          BIGINT PRIMARY KEY REFERENCES client.persona (id) ON DELETE CASCADE,
    contrasena  VARCHAR(255) NOT NULL,
    estado      BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE client.outbox_event (
    id              UUID PRIMARY KEY,
    aggregate_type  VARCHAR(50) NOT NULL,
    aggregate_id    BIGINT NOT NULL,
    event_type      VARCHAR(50) NOT NULL,
    payload         JSONB NOT NULL,
    correlation_id  UUID,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at    TIMESTAMP
);

CREATE INDEX idx_outbox_pending ON client.outbox_event (created_at)
    WHERE published_at IS NULL;

-- ---------------------------------------------------------------------------
-- Schema account (account-service)
-- ---------------------------------------------------------------------------

CREATE TABLE account.cliente_referencia (
    id              BIGINT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    identificacion  VARCHAR(20) NOT NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    synced_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_cliente_referencia_identificacion UNIQUE (identificacion)
);

CREATE TABLE account.cuenta (
    id              BIGSERIAL PRIMARY KEY,
    cliente_id      BIGINT NOT NULL REFERENCES account.cliente_referencia (id),
    numero_cuenta   VARCHAR(20) NOT NULL,
    tipo_cuenta     VARCHAR(20) NOT NULL,
    saldo           DECIMAL(12, 2) NOT NULL DEFAULT 0,
    estado          VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_cuenta_numero UNIQUE (numero_cuenta),
    CONSTRAINT chk_cuenta_tipo CHECK (tipo_cuenta IN ('AHORROS', 'CORRIENTE')),
    CONSTRAINT chk_cuenta_saldo CHECK (saldo >= 0),
    CONSTRAINT chk_cuenta_estado CHECK (estado IN ('ACTIVA', 'INACTIVA'))
);

CREATE TABLE account.movimiento (
    id                  BIGSERIAL PRIMARY KEY,
    cuenta_id           BIGINT NOT NULL REFERENCES account.cuenta (id),
    fecha               DATE NOT NULL DEFAULT CURRENT_DATE,
    tipo_movimiento     VARCHAR(20) NOT NULL,
    valor               DECIMAL(12, 2) NOT NULL,
    saldo_resultante    DECIMAL(12, 2) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_movimiento_tipo CHECK (tipo_movimiento IN ('DEPOSITO', 'RETIRO')),
    CONSTRAINT chk_movimiento_valor CHECK (valor <> 0),
    CONSTRAINT chk_movimiento_saldo_resultante CHECK (saldo_resultante >= 0)
);

CREATE INDEX idx_movimiento_cuenta_fecha ON account.movimiento (cuenta_id, fecha);

CREATE TABLE account.processed_event (
    event_id        UUID PRIMARY KEY,
    processed_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
