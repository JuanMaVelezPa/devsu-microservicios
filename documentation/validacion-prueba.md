# Validacion de la prueba Devsu

Guia operativa para **demostrar** el reto al evaluador: flujo Postman (Casos 1-5), verificacion async en PostgreSQL y tests Maven.

**Documento principal del proyecto:** [README.md](../README.md) (vision, stack, despliegue, API). Este archivo es un complemento paso a paso.

---

## 1. Preparar entorno

```bash
copy .env.example .env
docker compose up -d --build
docker compose ps
```

Esperar a que `client-service` y `account-service` esten **healthy** (~1-2 min).

Opcional — tests automaticos:

```bash
./mvnw clean verify
```

---

## 2. Postman

Importar:

| Archivo | Uso |
|---|---|
| `postman/Devsu.postman_collection.json` | Coleccion |
| `postman/Devsu-Local.postman_environment.json` | Entorno (8081 / 8082) |

Seleccionar entorno **Devsu Local**.

---

## 3. Flujo principal (Casos 1-5)

Ejecutar **en orden**. Tras el paso 2, esperar **~5 segundos** (outbox publica cada 3 s + consumer Kafka).

| Paso | Carpeta Postman | Requests | Esperado |
|---|---|---|---|
| 1 | `8081 Client Service` → `02 Caso 1 - Crear clientes` | 3 POST (Jose, Marianela, Juan) | HTTP 201, `success: true` |
| 2 | *(pausa ~5 s)* | — | Sync Kafka → `cliente_referencia` |
| 3 | `8082 Account Service` → `02 Caso 2 - Cuentas iniciales` | 4 POST | HTTP 201 |
| 4 | `8082 Account Service` → `03 Caso 3 - Cuenta adicional` | 1 POST (585545) | HTTP 201 |
| 5 | `8082 Account Service` → `04 Caso 4 - Movimientos` | 4 POST | HTTP 201 |
| 6 | `8082 Account Service` → `05 Caso 5 - Reporte` | GET Marianela feb-2022 | HTTP 200; tests Postman en verde |

### Caso 5 — resultado esperado (Marianela Montalvo, feb-2022)

| Cuenta | Saldo actual | Movimiento |
|---|---|---|
| 225487 | 700 | +600 el 2022-02-10 |
| 496825 | 0 | -540 el 2022-02-08 |

---

## 4. Verificar comunicacion async (PostgreSQL)

Outbox y proyeccion **no tienen GET en la API**; se revisan en base de datos.

Conectar:

```bash
docker exec -it devsu-postgres psql -U devsu -d devsu_db
```

Consultas utiles (copiar/pegar):

```sql
-- Outbox: eventos pendientes o ya publicados a Kafka
SELECT id, event_type, aggregate_id, published_at IS NOT NULL AS publicado, created_at
FROM client.outbox_event
ORDER BY created_at;

-- Clientes sincronizados en account-service (proyeccion Kafka)
SELECT id, nombre, identificacion, activo, synced_at
FROM account.cliente_referencia
ORDER BY id;

-- Idempotencia del consumer
SELECT event_id, processed_at FROM account.processed_event ORDER BY processed_at;
```

Tras el **paso 1** del flujo: filas en `outbox_event` con `publicado = true` y 3 filas en `cliente_referencia` con los mismos IDs.

---

## 5. Casos opcionales (errores)

| Carpeta Postman | Que demuestra |
|---|---|
| `8081` → `03 CRUD extra y errores` | 409 CLIENTE_DUPLICADO |
| `8082` → `06 Utilidades y errores` | 409 cuenta duplicada, 422 sin referencia, 422 F3 saldo, 404 |

---

## 6. Observabilidad (opcional)

| Recurso | URL |
|---|---|
| Swagger client | http://localhost:8081/swagger-ui.html |
| Swagger account | http://localhost:8082/swagger-ui.html |
| Prometheus targets | http://localhost:9090/targets |
| Grafana | http://localhost:3000 (admin/admin) |

---

## Referencias

- Datos Anexo A: [instructions.md](instructions.md#anexo-a---datos-de-prueba-reto-devsu)
- Checklist entrega: [evaluation.md](evaluation.md)
