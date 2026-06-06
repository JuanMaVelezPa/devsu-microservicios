-- Verificacion async Devsu (ejecutar en psql contra devsu_db)
-- docker exec -it devsu-postgres psql -U devsu -d devsu_db

-- Outbox client-service (Transactional Outbox)
SELECT id,
       event_type,
       aggregate_id,
       payload->>'nombre' AS cliente,
       published_at IS NOT NULL AS publicado_a_kafka,
       created_at,
       published_at
FROM client.outbox_event
ORDER BY created_at;

-- Proyeccion local en account-service (consumer Kafka)
SELECT id, nombre, identificacion, activo, synced_at
FROM account.cliente_referencia
ORDER BY id;

-- Eventos Kafka ya procesados (idempotencia)
SELECT event_id, processed_at
FROM account.processed_event
ORDER BY processed_at;
