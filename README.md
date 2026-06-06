# Devsu � Microservicios bancarios

Prueba tecnica Devsu: dos microservicios con API REST, persistencia JPA, comunicacion **asincrona** (Kafka + Transactional Outbox) y despliegue en Docker. Enfoque **Clean Architecture** y patrones habituales en entornos financieros (consistencia eventual, trazabilidad, separacion de dominios).

| | |
|---|---|
| **Autor** | Juan Manuel Velez Parra |
| **Correo** | [juanmavelezpa@gmail.com](mailto:juanmavelezpa@gmail.com) |
| **LinkedIn** | [linkedin.com/in/juanmavelezdev](https://www.linkedin.com/in/juanmavelezdev/) |
| **Estado** | F2 completada � Maven multi-m�dulo, plataforma API. Implementaci�n en fases F3�F12. |

---

## Vision del proyecto

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| **client-service** | 8081 | CRUD de Persona/Cliente |
| **account-service** | 8082 | Cuentas, movimientos y reportes |

Los servicios **no se llaman por REST entre si**. Los cambios de cliente se propagan por **eventos Kafka**; account-service mantiene una proyeccion local (`cliente_referencia`) para validar cuentas y reportes.

Documentacion tecnica: [documentation/instructions.md](documentation/instructions.md) | Indice: [documentation/README.md](documentation/README.md).

---

## Stack tecnologico

| Area | Tecnologia |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4 |
| Build | Maven (multi-m�dulo) |
| Persistencia | PostgreSQL, JPA/Hibernate |
| Mensajeria | Apache Kafka + Transactional Outbox |
| API | REST, envelope `ApiResponse`, OpenAPI (springdoc) |
| Seguridad datos | BCrypt en contrasena (sin JWT; no exigido por el reto) |
| Observabilidad | Micrometer, Prometheus, Grafana, logs con `correlationId` |
| Pruebas | JUnit 5, Mockito, Postman; Testcontainers (bonus) |
| Runtime | Docker, Docker Compose |

---

## Estructura del repositorio

```
Devsu/
??? pom.xml
??? mvnw / mvnw.cmd
??? .mvn/wrapper/
??? README.md
??? .gitignore
??? documentation/
??? client-service/           # (F1+)
??? account-service/          # (F1+)
??? docker-compose.yml        # (F3+)
??? BaseDatos.sql             # (F3+)
??? .env.example              # (F3+)
??? infra/                    # (F3+)
??? postman/                  # (F12)
```

---

## Como compilar y probar

```bash
./mvnw clean verify          # compilar + tests (ambos servicios)
./mvnw test                  # solo tests
```

## Como ejecutar (cuando este implementado)

> Se completara en F3 (infra) y F10 (stack full).

```bash
copy .env.example .env
docker compose up -d
./mvnw -pl client-service spring-boot:run    # :8081
./mvnw -pl account-service spring-boot:run   # :8082
```

Flujo de prueba Casos 1�5: [documentation/instructions.md](documentation/instructions.md).

---

## Documentacion

| Documento | Contenido |
|---|---|
| [documentation/instructions.md](documentation/instructions.md) | ADR, arquitectura, contrato API, Anexo A |
| [documentation/data-model.md](documentation/data-model.md) | Modelo ER y SQL |
| [documentation/implementation-phases.md](documentation/implementation-phases.md) | Roadmap F0�F12 |
| [documentation/evaluation.md](documentation/evaluation.md) | Checklist pre-entrega |

---

## Desarrollo asistido por IA (AI Engineering)

Enfoque **dirigido por el desarrollador**: la IA asiste en estructuracion, redaccion e implementacion bajo spec; **las decisiones de arquitectura, contrato y trade-offs son del autor**.

| Etapa | Decision (humano) | Asistencia IA |
|---|---|---|
| Requisitos | Priorizar entregables del reto | Organizar y contrastar requisitos |
| Arquitectura | Stack, Kafka+Outbox, schemas | Proponer alternativas; consistencia en docs |
| Contrato API | Validar alineacion con el reto | Tablas, ejemplos, revision |
| Implementacion | Aprobar cada fase; revisar codigo | Codificar segun documentacion |
| Entrega | Checklist y defensa tecnica | Tareas repetitivas bajo supervision |

La carpeta `documentation/` documenta el proceso y las decisiones para la entrevista tecnica.

---

*Proyecto en construccion � sector financiero / Java backend.*
