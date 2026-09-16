CREATE TABLE notificaciones (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT NOT NULL REFERENCES usuarios (id),
    tipo        VARCHAR(255),
    mensaje     VARCHAR(255),
    leida       BOOLEAN NOT NULL DEFAULT FALSE,
    fecha       TIMESTAMP
);

CREATE INDEX idx_notificaciones_usuario_id ON notificaciones (usuario_id);
CREATE INDEX idx_notificaciones_usuario_id_leida ON notificaciones (usuario_id, leida);
