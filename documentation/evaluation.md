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

- [ ] 2 microservicios: (Cliente, Persona) y (Cuenta, Movimientos)
- [ ] Comunicacion asincrona via Kafka demostrable
- [ ] Transactional Outbox en client-service (outbox_event + publisher)
- [ ] account-service consume topic `devsu.client.events`
- [ ] Sin REST sincrono entre microservicios
- [ ] Clean Architecture con capas separadas
- [ ] JPA / Hibernate sobre PostgreSQL
- [ ] Manejo global de excepciones
- [ ] Clean Code y buenas practicas demostrables

### F1 - CRUD / CRU

- [ ] `/clientes` - CRUD completo
- [ ] `/cuentas` - CRU (sin delete)
- [ ] `/movimientos` - POST y GET (sin PUT; F1 CRU sin update)

### F2 - Movimientos

- [ ] Depositos con valor positivo
- [ ] Retiros con valor negativo
- [ ] Saldo actualizado tras cada movimiento
- [ ] Historial de transacciones persistido

### F3 - Saldo insuficiente

- [ ] Retiro sin saldo es rechazado
- [ ] HTTP 422 con envelope ApiResponse (success: false)
- [ ] error.code = SALDO_NO_DISPONIBLE
- [ ] error.message = `"Saldo no disponible"` (texto exacto reto)
- [ ] Saldo no cambia si falla

### F4 - Reporte

- [ ] `GET /reportes?fechaDesde=&fechaHasta=&cliente=` funcional
- [ ] Respuesta JSON con cuentas, saldos y movimientos

### F5 - Pruebas unitarias

- [ ] Minimo 1 test unitario sobre entidad Cliente
- [ ] Ejecutable con la suite de tests

### Docker y entregables

- [ ] Solucion desplegada y funcional en Docker
- [ ] `BaseDatos.sql` presente
- [ ] Coleccion Postman JSON incluida
- [ ] Repositorio GitHub publico
- [ ] Archivo ZIP o RAR generado
- [ ] README con instrucciones de despliegue
- [ ] Repositorio GitHub con historial de commits claro

---

## B. Casos de uso (validacion funcional)

Referencia de datos: Anexo A en [instructions.md](instructions.md)

- [ ] Caso 1: 3 clientes creados
- [ ] Caso 2: 4 cuentas iniciales
- [ ] Caso 3: Cuenta 585545 para Jose Lema
- [ ] Caso 4: 4 movimientos ejecutados correctamente
- [ ] Caso 5: Reporte por fechas coincide con resultado esperado

---

## C. Stack del proyecto (decisiones propias)

Referencia: tabla ADR y secciones 1-6 en [instructions.md](instructions.md)

- [ ] Java 25 + Spring Boot 4 + Maven
- [ ] PostgreSQL: 1 instancia, schemas `client` + `account`
- [ ] ADR-13: .env.example + .gitignore + Docker Compose con variables (implementacion)
- [ ] Kafka en Docker Compose
- [ ] Outbox + cliente_referencia sincronizados
- [ ] Modelo ampliado: FKs, saldo actual, auditoria
- [ ] Puertos de persistencia en application (Repository pattern)
- [ ] Records en DTOs; Streams donde aplique
- [ ] CorrelationIdFilter + MDC en ambos servicios (ADR-12)
- [ ] Header X-Correlation-Id en request/response; ApiResponse usa mismo ID
- [ ] Consola legible + archivo logs/*.json con correlationId (ADR-12)
- [ ] correlationId en outbox_event y headers Kafka
- [ ] OpenAPI / Swagger UI en ambos microservicios
- [ ] ApiResponse envelope en todos los endpoints (ADR-11)
- [ ] GET listados paginados con page/size opcionales (ADR-14)
- [ ] Contrasena cliente: hash BCrypt en POST/PUT; nunca en GET/logs/Kafka (ADR-15)
- [ ] GlobalExceptionHandler mapea excepciones dominio -> HTTP 4xx
- [ ] client-service :8081, account-service :8082
- [ ] Micrometer + Prometheus + Grafana operativos
- [ ] Pruebas con JUnit 5 + Mockito

---

## D. Bonus (puntaje extra)

- [ ] F6: 1 prueba de integracion
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
