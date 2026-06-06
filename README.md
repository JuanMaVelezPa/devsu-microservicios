# Devsu - Microservicios bancarios

Prueba tecnica Devsu: dos microservicios con API REST, persistencia JPA, comunicacion **asincrona** (Kafka + Transactional Outbox) y despliegue en Docker. Enfoque **Clean Architecture** y patrones habituales en entornos financieros (consistencia eventual, trazabilidad, separacion de dominios).

| | |
|---|---|
| **Autor** | Juan Manuel Velez Parra |
| **Correo** | [juanmavelezpa@gmail.com](mailto:juanmavelezpa@gmail.com) |
| **LinkedIn** | [linkedin.com/in/juanmavelezdev](https://www.linkedin.com/in/juanmavelezdev/) |
| **Estado** | F10 completada - Docker full stack, Micrometer + dashboard Grafana de negocio. Siguiente: F11-F12 entrega. |

---

## Vision del proyecto

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| **client-service** | 8081 | CRUD de Persona/Cliente |
| **account-service** | 8082 | Cuentas, movimientos y reportes |

Los servicios **no se llaman por REST entre si**. Los cambios de cliente se propagan por **eventos Kafka**; account-service mantiene una proyeccion local (`cliente_referencia`) para validar cuentas y reportes.

Documentacion tecnica: [documentation/instructions.md](documentation/instructions.md) | Indice: [documentation/README.md](documentation/README.md).

---

## URLs de acceso (Docker Compose F10)

Requisito previo: `copy .env.example .env` y `docker compose up -d --build`.

| Que | URL |
|---|---|
| **client-service** - API REST | http://localhost:8081/api |
| **client-service** - Swagger UI | http://localhost:8081/swagger-ui.html |
| **client-service** - OpenAPI JSON | http://localhost:8081/v3/api-docs |
| **client-service** - Prometheus metrics | http://localhost:8081/actuator/prometheus |
| **account-service** - API REST | http://localhost:8082/api |
| **account-service** - Swagger UI | http://localhost:8082/swagger-ui.html |
| **account-service** - OpenAPI JSON | http://localhost:8082/v3/api-docs |
| **account-service** - Prometheus metrics | http://localhost:8082/actuator/prometheus |
| **Grafana** (admin/admin) | http://localhost:3000 |
| **Prometheus** | http://localhost:9090 |

### Endpoints API (contrato final)

**client-service :8081**

| Metodo | Path |
|---|---|
| GET | `/api/health` |
| POST | `/api/clientes` |
| GET | `/api/clientes` (page, size) |
| GET | `/api/clientes/{id}` |
| PUT | `/api/clientes/{id}` |
| DELETE | `/api/clientes/{id}` (baja logica) |

**account-service :8082** *(F7-F9 implementado)*

| Metodo | Path |
|---|---|
| GET | `/api/health` |
| POST/GET/PUT | `/api/cuentas`, `/api/cuentas/{id}` |
| POST/GET | `/api/movimientos`, `/api/movimientos/{id}` |
| GET | `/api/reportes?fechaDesde=&fechaHasta=&cliente=` |

Todas las respuestas usan envelope `ApiResponse` con header opcional `X-Correlation-Id`.

---

## Stack tecnologico

| Area | Tecnologia |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4 |
| Build | Maven (multi-modulo) |
| Persistencia | PostgreSQL 18 (Docker), JPA/Hibernate |
| Mensajeria | Apache Kafka 4.3 + Transactional Outbox |
| API | REST, envelope `ApiResponse`, OpenAPI (springdoc) |
| Seguridad datos | BCrypt en contrasena (sin JWT; no exigido por el reto) |
| Observabilidad | Micrometer + Actuator, Prometheus 3.12, Grafana 13, logs con `correlationId` |
| Pruebas | JUnit 5, Mockito, Postman; Testcontainers (bonus) |
| Runtime | Docker, Docker Compose |

---

## Observabilidad (Micrometer + Prometheus + Grafana)

Stack de metricas de punta a punta: **Micrometer** instrumenta la aplicacion, **Spring Boot Actuator** expone `/actuator/prometheus`, **Prometheus** hace scrape cada 15 s y **Grafana** visualiza un dashboard de negocio provisionado.

```
Microservicios (Micrometer)  -->  /actuator/prometheus  -->  Prometheus  -->  Grafana
     client-service :8081              scrape 15s              :9090          :3000
     account-service :8082
```

### Componentes

| Pieza | Rol |
|---|---|
| **Micrometer** | API de metricas; counters custom `devsu.*` + metricas JVM/HTTP/Hikari/Kafka |
| **Actuator** | Endpoints `health`, `info`, `prometheus` en ambos servicios |
| **Prometheus** | Almacena series temporales; config en `infra/prometheus/prometheus.yml` |
| **Grafana** | Dashboard **Devsu - Negocio bancario** (provisionado en `infra/grafana/`) |

### Metricas de negocio (`devsu.*`)

Counters custom ligados al caso Devsu (prefijo Prometheus: `devsu_*_total`):

| Metrica | Servicio | Descripcion |
|---|---|---|
| `devsu_cliente_operaciones{operacion}` | client | CRUD clientes: `create`, `update`, `delete` |
| `devsu_outbox_publicados{event_type}` | client | Eventos outbox publicados a Kafka |
| `devsu_cuenta_operaciones{operacion}` | account | Alta/actualizacion de cuentas |
| `devsu_movimiento_operaciones{tipo}` | account | `deposito` / `retiro` exitosos |
| `devsu_movimiento_rechazos{motivo}` | account | Rechazos F3 (`saldo_insuficiente`) |
| `devsu_kafka_eventos_procesados{event_type}` | account | Consumer sincroniza `cliente_referencia` |
| `devsu_kafka_eventos_duplicados` | account | Idempotencia por `eventId` |
| `devsu_reporte_generados` | account | Reportes de estado de cuenta generados |

### Metricas de plataforma (automaticas)

Micrometer/Spring Boot tambien exponen: latencia y throughput HTTP (`http_server_requests_*`), memoria JVM, pool HikariCP (PostgreSQL), metricas del producer/consumer Kafka y errores 4xx/5xx por endpoint.

### Como consultar

| Recurso | URL |
|---|---|
| Metricas raw client-service | http://localhost:8081/actuator/prometheus |
| Metricas raw account-service | http://localhost:8082/actuator/prometheus |
| Targets Prometheus (UP/DOWN) | http://localhost:9090/targets |
| Dashboard Grafana | http://localhost:3000 → carpeta **Devsu** → **Devsu - Negocio bancario** |

Tras levantar Docker y ejecutar el flujo Postman (Anexo A), las graficas muestran actividad en clientes, cuentas, movimientos, sync Kafka y reportes. Los counters `devsu_*` aparecen en `/actuator/prometheus` tras la primera operacion de cada tipo (Micrometer no expone series en cero).

---

## Estructura del repositorio

```
Devsu/
|-- pom.xml
|-- mvnw / mvnw.cmd
|-- README.md
|-- documentation/
|-- client-service/
|   `-- Dockerfile
|-- account-service/
|   `-- Dockerfile
|-- docker-compose.yml
|-- BaseDatos.sql
|-- .env.example
|-- .dockerignore
|-- infra/
|   |-- prometheus/prometheus.yml
|   `-- grafana/provisioning/
|-- postman/
```

---

## Como compilar y probar

```bash
./mvnw clean verify
./mvnw test
```

## Como ejecutar

### Modo Docker (recomendado - F10)

Todo el stack en contenedores: PostgreSQL, Kafka, microservicios, Prometheus y Grafana.

```bash
copy .env.example .env
docker compose up -d --build
```

Esperar ~1-2 min a que `client-service` y `account-service` pasen healthcheck. Ver estado:

```bash
docker compose ps
```

**Flujo Anexo A:** importar Postman (`postman/`) y ejecutar carpetas F4 -> F7 -> F8 -> F9 (esperar ~5 s tras crear clientes para sync Kafka).

**Observabilidad:** ver seccion [Observabilidad (Micrometer + Prometheus + Grafana)](#observabilidad-micrometer--prometheus--grafana). Resumen rapido:
- Prometheus targets: http://localhost:9090/targets (jobs `client-service`, `account-service`)
- Grafana: http://localhost:3000 (usuario/contrasena en `.env`)
- Dashboard provisionado: carpeta **Devsu** → **Devsu - Negocio bancario** (metricas de clientes, cuentas, movimientos, Kafka, reportes)

Detener:

```bash
docker compose down
```

### Modo local (desarrollo)

Infra en Docker, apps con Maven en el host:

```bash
docker compose up -d postgres kafka prometheus grafana
```

Variables en `.env`: `POSTGRES_HOST=localhost`, `POSTGRES_PORT=5433`, `KAFKA_BROKER=localhost:9092`. Spring Boot **no** carga `.env` solo; usa `envFile` en `.vscode/launch.json` o exporta variables.

```bash
./mvnw -pl client-service spring-boot:run    # :8081
./mvnw -pl account-service spring-boot:run   # :8082
```

**3. Postman**

Importar `postman/Devsu.postman_collection.json` y `postman/Devsu-Local.postman_environment.json`.

**Flujo rapido Anexo A (Casos 1-5):**

| Paso | Carpeta Postman | Notas |
|---|---|---|
| 1 | client-service > **F4 - Caso 1** | 3 POST clientes; scripts guardan `joseClienteId`, etc. |
| 2 | — | Esperar ~5 s (outbox + Kafka -> `cliente_referencia`) |
| 3 | account-service > **F7 - Caso 2** | 4 cuentas iniciales |
| 4 | account-service > **F7 - Caso 3** | Cuenta 585545 Jose Lema |
| 5 | account-service > **F8 - Caso 4** | 4 movimientos (fechas feb-2022) |
| 6 | account-service > **F9 - Caso 5** | GET reporte Marianela Montalvo |

Carpetas **utilidades** incluyen casos de error (422 F3, 404, 409). Detalle: [documentation/instructions.md](documentation/instructions.md).

---

## Documentacion

| Documento | Contenido |
|---|---|
| [documentation/instructions.md](documentation/instructions.md) | ADR, arquitectura, contrato API, Anexo A |
| [documentation/data-model.md](documentation/data-model.md) | Modelo ER y SQL |
| [documentation/implementation-phases.md](documentation/implementation-phases.md) | Roadmap F0-F12 |
| [documentation/evaluation.md](documentation/evaluation.md) | Checklist pre-entrega |

---

*Proyecto en construccion - sector financiero / Java backend.*
