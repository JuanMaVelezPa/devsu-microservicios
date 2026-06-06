---
title: Devsu Microservicios - Evaluacion Final
version: 3.6
encoding: ASCII
---

# Evaluacion final del proyecto

Checklist para validar cumplimiento del reto Devsu antes de entregar.

Especificacion del proyecto: [instructions.md](instructions.md)

Indice documentacion: [README.md](README.md)

Marcar cada item al completarlo. **Obligatorio** = exigido por Devsu. **Proyecto** = decisiones propias del stack. **Bonus** = puntaje extra.

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

- [x] Minimo 1 test unitario sobre entidad Cliente
- [x] Ejecutable con la suite de tests

### Docker y entregables

- [ ] Solucion desplegada y funcional en Docker
- [x] `BaseDatos.sql` presente
- [x] Coleccion Postman JSON incluida
- [ ] Repositorio GitHub publico
- [ ] Archivo ZIP o RAR generado
- [ ] README con instrucciones de despliegue
- [ ] Repositorio GitHub con historial de commits claro

---

## B. Casos de uso (validacion funcional)

Referencia de datos: Anexo A en [instructions.md](instructions.md)

- [ ] Caso 1: 3 clientes creados *(validar con Postman)*
- [ ] Caso 2: 4 cuentas iniciales *(validar con Postman)*
- [ ] Caso 3: Cuenta 585545 para Jose Lema *(validar con Postman)*
- [ ] Caso 4: 4 movimientos ejecutados correctamente *(validar con Postman)*
- [ ] Caso 5: Reporte por fechas coincide con resultado esperado *(validar con Postman)*

> Coleccion Postman actualizada (F4-F9). Ejecutar flujo en [README.md](../README.md#como-ejecutar).

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
- [ ] Micrometer + Prometheus + Grafana operativos *(infra F3; apps en Docker F10)*
- [x] Pruebas con JUnit 5 + Mockito

---

## D. Bonus (puntaje extra)

- [x] F6: 1 prueba de integracion
- [ ] F7: Despliegue completo en contenedores (si no cubierto en A)
- [ ] Diseno documenta rendimiento, escalabilidad, resiliencia (outbox aporta resiliencia)
- [ ] Virtual threads u optimizaciones adicionales

---

## Estado final

| Criterio | Estado |
|---|---|
| Obligatorios (A + B) | [ ] Completo |
| Stack proyecto (C) | [ ] Completo |
| Bonus (D) | [ ] Completo |

**Resultado:** [ ] APROBADO  |  [ ] PENDIENTE
