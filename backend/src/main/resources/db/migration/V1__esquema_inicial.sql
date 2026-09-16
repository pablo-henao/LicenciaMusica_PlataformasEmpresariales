-- Esquema inicial de Licencia+. Refleja el estado de las entidades JPA al momento
-- de introducir Flyway (bloques 1 a 6): Hibernate pasa de ddl-auto=update a validate,
-- Flyway pasa a ser el unico dueño del esquema.

CREATE TABLE usuarios (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(255),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    rol           VARCHAR(255)
);

CREATE TABLE beats (
    id           BIGSERIAL PRIMARY KEY,
    titulo       VARCHAR(255),
    genero       VARCHAR(255),
    bpm          INTEGER,
    productor_id BIGINT NOT NULL REFERENCES usuarios (id),
    url_preview  VARCHAR(255),
    estado       VARCHAR(255)
);

CREATE INDEX idx_beats_productor_id ON beats (productor_id);
CREATE INDEX idx_beats_estado_genero_bpm ON beats (estado, genero, bpm);

CREATE TABLE tipos_licencia (
    id          BIGSERIAL PRIMARY KEY,
    beat_id     BIGINT NOT NULL REFERENCES beats (id),
    tipo        VARCHAR(255),
    precio      NUMERIC,
    condiciones TEXT
);

CREATE INDEX idx_tipos_licencia_beat_id ON tipos_licencia (beat_id);

CREATE TABLE compras (
    id                BIGSERIAL PRIMARY KEY,
    comprador_id      BIGINT NOT NULL REFERENCES usuarios (id),
    tipo_licencia_id  BIGINT NOT NULL REFERENCES tipos_licencia (id),
    fecha             TIMESTAMP,
    estado            VARCHAR(255),
    contrato_pdf      BYTEA
);

CREATE INDEX idx_compras_comprador_id ON compras (comprador_id);
CREATE INDEX idx_compras_tipo_licencia_id ON compras (tipo_licencia_id);

CREATE TABLE colaboradores_beat (
    id                    BIGSERIAL PRIMARY KEY,
    beat_id               BIGINT NOT NULL REFERENCES beats (id),
    usuario_id            BIGINT NOT NULL REFERENCES usuarios (id),
    rol                   VARCHAR(255),
    porcentaje_propuesto  NUMERIC,
    estado                VARCHAR(255)
);

CREATE INDEX idx_colaboradores_beat_beat_id ON colaboradores_beat (beat_id);
CREATE INDEX idx_colaboradores_beat_usuario_id ON colaboradores_beat (usuario_id);

CREATE TABLE acuerdos_creditos (
    id            BIGSERIAL PRIMARY KEY,
    beat_id       BIGINT NOT NULL UNIQUE REFERENCES beats (id),
    estado        VARCHAR(255),
    fecha_cierre  TIMESTAMP
);

CREATE TABLE acuerdo_creditos_eventos (
    id          BIGSERIAL PRIMARY KEY,
    beat_id     BIGINT NOT NULL REFERENCES beats (id),
    usuario_id  BIGINT NOT NULL REFERENCES usuarios (id),
    tipo        VARCHAR(255),
    detalle     VARCHAR(255),
    fecha       TIMESTAMP
);

CREATE INDEX idx_acuerdo_creditos_eventos_beat_id ON acuerdo_creditos_eventos (beat_id);
