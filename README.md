# Devsu - Microservicios bancarios

Prueba tecnica Devsu: dos microservicios con API REST, persistencia JPA, comunicacion **asincrona** (Kafka + Transactional Outbox) y despliegue en Docker. Enfoque **Clean Architecture** y patrones habituales en entornos financieros (consistencia eventual, trazabilidad, separacion de dominios).

| | |
|---|---|
| **Autor** | Juan Manuel Velez Parra |
| **Correo** | [juanmavelezpa@gmail.com](mailto:juanmavelezpa@gmail.com) |
| **LinkedIn** | [linkedin.com/in/juanmavelezdev](https://www.linkedin.com/in/juanmavelezdev/) |
| **Estado** | F5 completada - Outbox + Kafka publish. Siguiente: F6 Consumer. |

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

**account-service :8082** *(F7-F9)*

| Metodo | Path |
|---|---|
| GET | `/api/health` |
| POST/GET/PUT | `/api/cuentas` |
| POST/GET | `/api/movimientos` |
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

Importar `postman/Devsu.postman_collection.json` y `postman/Devsu-Local.postman_environment.json`. Carpeta **F4 - Caso 1** crea los 3 clientes del Anexo A.

Flujo completo Casos 1-5: [documentation/instructions.md](documentation/instructions.md).

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
