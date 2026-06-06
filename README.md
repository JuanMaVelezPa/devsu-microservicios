# Devsu - Microservicios bancarios

Prueba tecnica Devsu: dos microservicios con API REST, persistencia JPA, comunicacion **asincrona** (Kafka + Transactional Outbox) y despliegue en Docker. Enfoque **Clean Architecture** y patrones habituales en entornos financieros (consistencia eventual, trazabilidad, separacion de dominios).

| | |
|---|---|
| **Autor** | Juan Manuel Velez Parra |
| **Correo** | [juanmavelezpa@gmail.com](mailto:juanmavelezpa@gmail.com) |
| **LinkedIn** | [linkedin.com/in/juanmavelezdev](https://www.linkedin.com/in/juanmavelezdev/) |
| **Estado** | F9 completada - reportes por cliente y rango de fechas. Siguiente: F10 Observabilidad Docker. |

---

## Vision del proyecto

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| **client-service** | 8081 | CRUD de Persona/Cliente |
| **account-service** | 8082 | Cuentas, movimientos y reportes |

Los servicios **no se llaman por REST entre si**. Los cambios de cliente se propagan por **eventos Kafka**; account-service mantiene una proyeccion local (`cliente_referencia`) para validar cuentas y reportes.

Documentacion tecnica: [documentation/instructions.md](documentation/instructions.md) | Indice: [documentation/README.md](documentation/README.md).

---

## URLs de acceso (con Docker + apps arriba)

Requisito previo: `docker compose up -d` y microservicios en local (`spring-boot:run` hasta F10).

| Que | URL |
|---|---|
| **client-service** - API REST | http://localhost:8081/api |
| **client-service** - Swagger UI | http://localhost:8081/swagger-ui.html |
| **account-service** - API REST | http://localhost:8082/api |
| **account-service** - Swagger UI | http://localhost:8082/swagger-ui.html |
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
| Observabilidad | Micrometer, Prometheus 3.12, Grafana 13, logs con `correlationId` |
| Pruebas | JUnit 5, Mockito, Postman; Testcontainers (bonus) |
| Runtime | Docker, Docker Compose |

---

## Estructura del repositorio

```
Devsu/
|-- pom.xml
|-- mvnw / mvnw.cmd
|-- README.md
|-- documentation/
|-- client-service/
|-- account-service/
|-- docker-compose.yml
|-- BaseDatos.sql
|-- .env.example
|-- infra/
|-- postman/
```

---

## Como compilar y probar

```bash
./mvnw clean verify
./mvnw test
```

## Como ejecutar

**1. Infra (PostgreSQL, Kafka, Prometheus, Grafana)**

```bash
copy .env.example .env
docker compose up -d
```

**2. Microservicios** (local, hasta F10 en contenedor)

Variables de BD: el `application.yml` usa por defecto `localhost:5433` y credenciales de `.env.example`. Spring Boot **no** carga el archivo `.env`; usa `envFile` en `.vscode/launch.json` o exporta vars (`POSTGRES_HOST=localhost`, `POSTGRES_PORT=5433`).

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
