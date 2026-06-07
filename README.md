# Devsu - Microservicios bancarios

Prueba tecnica Devsu: dos microservicios con API REST, persistencia JPA, comunicacion **asincrona** (Kafka + Transactional Outbox) y despliegue en Docker. Enfoque **Clean Architecture** y patrones habituales en entornos financieros (consistencia eventual, trazabilidad, separacion de dominios).

| | |
|---|---|
| **Autor** | Juan Manuel Velez Parra |
| **Correo** | juanmavelezpa@gmail.com |
| **LinkedIn** | [linkedin.com/in/juanmavelezdev](https://www.linkedin.com/in/juanmavelezdev/) |
| **Estado** | F12 completada. Entrega: commit final, ZIP y push. |

---

## Vision del proyecto

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| **client-service** | 8081 | CRUD de Persona/Cliente |
| **account-service** | 8082 | Cuentas, movimientos y reportes |

Los servicios **no se llaman por REST entre si**. Los cambios de cliente se propagan por **eventos Kafka**; account-service mantiene una proyeccion local (`cliente_referencia`) para validar cuentas y reportes.

```
Postman / Cliente  -->  client-service :8081  -->  schema client (outbox)
                              |
                              v Kafka (devsu.client.events)
                              |
Postman / Cuentas  -->  account-service :8082  -->  schema account (cliente_referencia, cuenta, movimiento)
```

Documentacion tecnica: [documentation/instructions.md](documentation/instructions.md) | Indice: [documentation/README.md](documentation/README.md).

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
| Observabilidad | Micrometer + Actuator, Prometheus, Grafana, logs con `correlationId` |
| Pruebas | JUnit 5, Mockito, Postman |
| Runtime | Docker, Docker Compose |

---

## URLs de acceso (Docker Compose)

Requisito previo: `copy .env.example .env` y `docker compose up -d --build`.

| Recurso | URL |
|---|---|
| client-service - API | http://localhost:8081/api |
| client-service - Swagger | http://localhost:8081/swagger-ui.html |
| client-service - OpenAPI | http://localhost:8081/v3/api-docs |
| client-service - Prometheus | http://localhost:8081/actuator/prometheus |
| account-service - API | http://localhost:8082/api |
| account-service - Swagger | http://localhost:8082/swagger-ui.html |
| account-service - OpenAPI | http://localhost:8082/v3/api-docs |
| account-service - Prometheus | http://localhost:8082/actuator/prometheus |
| Grafana (admin/admin) | http://localhost:3000 |
| Prometheus | http://localhost:9090 |

### Endpoints API

**client-service :8081**

| Metodo | Path |
|---|---|
| GET | `/api/health` |
| POST/GET/PUT/DELETE | `/api/clientes`, `/api/clientes/{id}` |

**account-service :8082**

| Metodo | Path |
|---|---|
| GET | `/api/health` |
| POST/GET/PUT | `/api/cuentas`, `/api/cuentas/{id}` |
| POST/GET | `/api/movimientos`, `/api/movimientos/{id}` |
| GET | `/api/reportes?fechaDesde=&fechaHasta=&cliente=` |

Todas las respuestas usan envelope `ApiResponse` con header opcional `X-Correlation-Id`.

---

## Como ejecutar

### Modo Docker (recomendado)

Todo el stack en contenedores: PostgreSQL, Kafka, microservicios, Prometheus y Grafana.

```bash
copy .env.example .env
docker compose up -d --build
docker compose ps
```

Esperar ~1-2 min a que `client-service` y `account-service` esten **healthy**.

Detener: `docker compose down`

### Modo local (desarrollo)

Infra en Docker, aplicaciones con Maven en el host:

```bash
docker compose up -d postgres kafka
```

Variables en `.env`: `POSTGRES_HOST=localhost`, `POSTGRES_PORT=5433`, `KAFKA_BROKER=localhost:9092`.

```bash
./mvnw -pl client-service spring-boot:run    # :8081
./mvnw -pl account-service spring-boot:run   # :8082
```

---

## Compilar y probar

```bash
./mvnw clean verify
./mvnw test
```

Suite: unitarios de dominio/application (Mockito), integracion API (MockMvc + H2) e integracion Kafka consumer. Perfil `test` en `application-test.yml`.

---

## Postman

Importar:

- `postman/Devsu.postman_collection.json`
- `postman/Devsu-Local.postman_environment.json`

Coleccion organizada por servicio (`8081 Client Service`, `8082 Account Service`) con carpetas numeradas para el flujo del Anexo A (Casos 1-5) y utilidades de error.

Para el **paso a paso detallado** de validacion (incluye pausa Kafka y consultas SQL de outbox/sync): [documentation/validacion-prueba.md](documentation/validacion-prueba.md).

---

## Observabilidad

Micrometer instrumenta la aplicacion; Actuator expone `/actuator/prometheus`; Prometheus hace scrape cada 15 s; Grafana muestra el dashboard **Devsu - Negocio bancario** (provisionado en `infra/grafana/`).

Metricas de negocio `devsu_*` (clientes, outbox, cuentas, movimientos, rechazos F3, sync Kafka, reportes). Aparecen tras la primera operacion de cada tipo.

| Consulta | URL |
|---|---|
| Targets Prometheus | http://localhost:9090/targets |
| Dashboard Grafana | http://localhost:3000 → carpeta **Devsu** |

Config: `infra/prometheus/prometheus.yml`

**Resiliencia Docker (F12):** todos los servicios usan `restart: unless-stopped`. Los microservicios exponen healthcheck de readiness en Compose (`/actuator/health/readiness`); si un contenedor falla, Docker lo reinicia automaticamente. Las alertas a operadores (p. ej. Discord) quedan como evolucion futura — ver abajo.

---

## Evolucion futura (fuera de alcance del reto)

Decisiones documentadas para produccion; no implementadas en codigo para mantener el alcance de la prueba tecnica. Detalle en [instructions.md — seccion 7](documentation/instructions.md#7-produccion-y-evolucion).

| Area | Mejora posible |
|---|---|
| **Alertas** | Grafana Alerting o Prometheus Alertmanager → webhook Discord/Slack cuando un target cae o sube la tasa de errores |
| **Kafka** | Tuning consumer (`max.poll`, concurrencia), DLQ, retry con backoff; cluster multi-broker en produccion |
| **Escalabilidad** | Replicas horizontales de MS detras de balanceador; particiones Kafka alineadas al numero de consumidores |
| **Carga HTTP** | Virtual threads (`spring.threads.virtual.enabled=true`) si el perfil es I/O bound y hay metricas que lo justifiquen |
| **Pruebas E2E** | Testcontainers con stack Docker completo (Postgres + Kafka real) ademas de Postman manual |
| **Outbox** | Leader election o job dedicado si hay multiples instancias de client-service publicando |

El **Transactional Outbox**, la **idempotencia** del consumer y la **trazabilidad con correlationId** ya cubren resiliencia basica del flujo async.

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
|   |-- prometheus/
|   `-- grafana/
|-- postman/
```

---

## Documentacion

| Documento | Contenido |
|---|---|
| [documentation/instructions.md](documentation/instructions.md) | ADR, arquitectura, contrato API, Anexo A |
| [documentation/data-model.md](documentation/data-model.md) | Modelo ER y SQL |
| [documentation/implementation-phases.md](documentation/implementation-phases.md) | Roadmap F0-F12 |
| [documentation/evaluation.md](documentation/evaluation.md) | Checklist pre-entrega |
| [documentation/validacion-prueba.md](documentation/validacion-prueba.md) | Guia operativa Casos 1-5 (Postman + BD) |
