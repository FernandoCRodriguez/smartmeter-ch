-- Una lectura se identifica por su contador y su instante.
-- Si Kafka la entrega dos veces, la segunda se descarta (consumidor idempotente).
DROP INDEX idx_lectura_medidor_momento;

ALTER TABLE lectura
    ADD CONSTRAINT uk_lectura_medidor_momento UNIQUE (medidor_id, momento);