---
title: Devsu Microservicios - Evaluacion Final
version: 3.7
encoding: ASCII
---

# Evaluacion final del proyecto

Checklist para validar cumplimiento del reto Devsu antes de entregar.

Especificacion del proyecto: [instructions.md](instructions.md)

Indice documentacion: [README.md](../README.md)

Marcar cada item al completarlo. **Obligatorio** = exigido por Devsu. **Proyecto** = decisiones propias del stack. **Bonus** = puntaje extra.

### Estado actual (2026-06-06)

| Area | Avance |
|---|---|
| Codigo + tests automaticos | F0-F11 implementadas; `./mvnw clean verify` verde |
| Validacion manual Casos 1-5 | Pendiente ejecutar Postman contra Docker |
| Entrega F12 | Pendiente ZIP/RAR, commits F11-F12 y marcar seccion B |

---

## A. Requisitos obligatorios del reto

### Arquitectura y calidad

- [x] 2 microservicios: (Cliente, Persona) y (Cuenta, Movimientos)
- [x] Comunicacion asincrona via Kafka demostrable
- [x] Transactional Outbox en client-service (outbox_event + publisher)
- [x] account-service consume topic `devsu.client.events`
- [x] Sin REST sincrono entre microservicios
- [x] Clean Architecture con capas separadas
- [x] JPA / Hibernate sobre PostgreSQL
- [x] Manejo global de excepciones
- [x] Clean Code y buenas practicas demostrables

### F1 - CRUD / CRU

- [x] `/clientes` - CRUD completo
- [x] `/cuentas` - CRU (sin delete)
- [x] `/movimientos` - POST y GET (sin PUT; F1 CRU sin update)

### F2 - Movimientos

- [x] Depositos con valor positivo
- [x] Retiros con valor negativo
- [x] Saldo actualizado tras cada movimiento
- [x] Historial de transacciones persistido

### F3 - Saldo insuficiente

- [x] Retiro sin saldo es rechazado
- [x] HTTP 422 con envelope ApiResponse (success: false)
- [x] error.code = SALDO_NO_DISPONIBLE
- [x] error.message = `"Saldo no disponible"` (texto exacto reto)
- [x] Saldo no cambia si falla

### F4 - Reporte

- [x] `GET /reportes?fechaDesde=&fechaHasta=&cliente=` funcional
- [x] Respuesta JSON con cuentas, saldos y movimientos

### F5 - Pruebas unitarias

- [x] Minimo 1 test unitario sobre entidad Cliente (`ClienteTest`)
- [x] Tests application con Mockito (`ClienteApplicationServiceTest`, `MovimientoApplicationServiceTest`)
- [x] Tests API MockMvc (Casos Anexo A, envelope, F3 saldo)
- [x] Integracion Kafka consumer (`ClienteEventConsumerIntegrationTest`)
- [x] Suite ejecutable: `./mvnw clean verify` (37 tests, 0 fallos)

### Docker y entregables

- [x] Solucion desplegada y funcional en Docker *(F10: `docker compose up -d --build`, health 200, targets Prometheus UP)*
- [x] `BaseDatos.sql` presente
- [x] Coleccion Postman JSON incluida *(Casos 1-5 + tests automatizados en Caso 5)*
- [x] OpenAPI / Swagger UI en ambos microservicios
- [x] Repositorio GitHub publico
- [x] README con instrucciones de despliegue *(Docker, local, observabilidad, Postman)*
- [x] Sin secretos en repo *(`.env` en `.gitignore`; usar `.env.example`)*
- [ ] Archivo ZIP o RAR generado *(F12)*
- [ ] Historial Git con commits F11 y F12 *(F0-F10 ya commiteados por fase: `docs(f0)` … `feat(f10)`)*

---

## B. Casos de uso (validacion funcional)

Referencia de datos: Anexo A en [instructions.md](instructions.md)

Validacion **manual end-to-end** con stack Docker + Postman. Los tests Maven cubren la misma logica en H2/Kafka embebido, pero esta seccion exige demostrar el flujo real (incluye sync Kafka y pausa outbox).

| Caso | Item | Cobertura automatica |
|---|---|---|
| 1 | [ ] 3 clientes creados *(Postman)* | `ClienteApiTest` |
| 2 | [ ] 4 cuentas iniciales *(Postman)* | `CuentaApiTest` |
| 3 | [ ] Cuenta 585545 para Jose Lema *(Postman)* | `CuentaApiTest` |
| 4 | [ ] 4 movimientos ejecutados correctamente *(Postman)* | `MovimientoApiTest` |
| 5 | [ ] Reporte por fechas coincide con resultado esperado *(Postman)* | `ReporteApiTest` + tests en coleccion Postman |

> Guia paso a paso: [validacion-prueba.md](validacion-prueba.md) | SQL async: [queries-verificacion-async.sql](queries-verificacion-async.sql)

---

## C. Stack del proyecto (decisiones propias)

Referencia: tabla ADR y secciones 1-6 en [instructions.md](instructions.md)

- [x] Java 25 + Spring Boot 4 + Maven
- [x] PostgreSQL: 1 instancia, schemas `client` + `account`
- [x] ADR-13: .env.example + .gitignore + Docker Compose con variables (implementacion)
- [x] Kafka en Docker Compose
- [x] Outbox + cliente_referencia sincronizados
- [x] Modelo ampliado: FKs, saldo actual, auditoria
- [x] Puertos de persistencia en application (Repository pattern)
- [x] Records en DTOs; Streams donde aplique
- [x] CorrelationIdFilter + MDC en ambos servicios (ADR-12)
- [x] Header X-Correlation-Id en request/response; ApiResponse usa mismo ID
- [x] Consola legible + archivo logs/*.json con correlationId (ADR-12)
- [x] correlationId en outbox_event y headers Kafka
- [x] OpenAPI / Swagger UI en ambos microservicios
- [x] ApiResponse envelope en todos los endpoints (ADR-11)
- [x] GET listados paginados con page/size opcionales (ADR-14)
- [x] Contrasena cliente: hash BCrypt en POST/PUT; nunca en GET/logs/Kafka (ADR-15)
- [x] GlobalExceptionHandler mapea excepciones dominio -> HTTP 4xx
- [x] client-service :8081, account-service :8082
- [x] Micrometer + Prometheus + Grafana operativos *(F10: apps en compose + scrape + dashboard)*
- [x] Pruebas con JUnit 5 + Mockito *(F11: dominio, application, API e integracion Kafka)*

---

## D. Bonus (puntaje extra)

- [x] F6: 1 prueba de integracion (`ClienteEventConsumerIntegrationTest`)
- [x] F7: Despliegue completo en contenedores *(F10: MS + infra en compose)*
- [ ] Diseno documenta rendimiento, escalabilidad, resiliencia *(parcial: Outbox + idempotencia en [instructions sec. 2](instructions.md#2-kafka--outbox); sin seccion dedicada rendimiento/escalabilidad)*
- [ ] Virtual threads u optimizaciones adicionales

---

## E. Entrega F12 (checklist operativo)

- [x] Guia de validacion [validacion-prueba.md](validacion-prueba.md)
- [x] Consultas SQL async [queries-verificacion-async.sql](queries-verificacion-async.sql)
- [x] Postman actualizado (Casos 1-5, carpetas por servicio, tests Caso 5)
- [ ] Ejecutar flujo Casos 1-5 en Docker y marcar seccion B
- [ ] Commit `test(f11): pruebas unitarias y de integracion`
- [ ] Commit `chore(f12): entrega - Postman, OpenAPI y documentacion final`
- [ ] Generar ZIP/RAR del proyecto (sin `target/`, `.env`, logs)
- [ ] Push final a GitHub

---

## Estado final

| Criterio | Estado | Notas |
|---|---|---|
| Obligatorios codigo (A, sin B) | [x] Completo | Falta ZIP e historial F11-F12 |
| Casos de uso (B) | [ ] Pendiente | Validacion manual Postman + Docker |
| Stack proyecto (C) | [x] Completo | |
| Bonus (D) | [ ] Parcial | Integracion + Docker full; doc rendimiento pendiente |

**Resultado:** [ ] APROBADO  |  [x] PENDIENTE *(cerrar B + F12 para aprobar)*
