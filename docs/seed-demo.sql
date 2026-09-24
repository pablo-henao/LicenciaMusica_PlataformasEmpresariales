-- Seed demo de Licencia+ (datos de prueba, NO es migración Flyway).
--
-- Uso (con el backend APAGADO o encendido, da igual: es SQL directo):
--   psql -h localhost -U postgres -d license_music -f docs/seed-demo.sql
--
-- Clave de TODOS los usuarios demo: demo1234
--   productor:  djkalu@demo.com      (DJ Kalu)
--   productora: yani@demo.com        (Yani Bloom, colaboradora)
--   comprador:  mcsueno@demo.com     (MC Sueño)
--   admin:      admin@demo.com       (rol ADMIN, ver /admin)
--
-- Qué deja listo:
--   - 3 beats PUBLICADOS con licencias (el catálogo no arranca vacío)
--   - 1 beat en BORRADOR con split pendiente (para probar Mis splits)
--   - 1 beat publicado con acuerdo CERRADO + historial (para ver timeline)
-- Es idempotente: se puede correr varias veces (limpia primero lo que inserta).

BEGIN;

DELETE FROM notificaciones;
DELETE FROM acuerdo_creditos_eventos;
DELETE FROM acuerdos_creditos;
DELETE FROM colaboradores_beat;
DELETE FROM compras;
DELETE FROM tipos_licencia;
DELETE FROM beats;
DELETE FROM usuarios;

-- password_hash = BCrypt("demo1234")
INSERT INTO usuarios (id, nombre, email, password_hash, rol) VALUES
  (1, 'DJ Kalu',    'djkalu@demo.com',  '$2b$10$19FuVEI0nvw7VNAr9JXJgeVeEq1N0SluO.Awn2fHhrimXE4Eit4Da', 'PRODUCTOR'),
  (2, 'Yani Bloom', 'yani@demo.com',    '$2b$10$19FuVEI0nvw7VNAr9JXJgeVeEq1N0SluO.Awn2fHhrimXE4Eit4Da', 'PRODUCTOR'),
  (3, 'MC Sueño',   'mcsueno@demo.com', '$2b$10$19FuVEI0nvw7VNAr9JXJgeVeEq1N0SluO.Awn2fHhrimXE4Eit4Da', 'COMPRADOR'),
  (4, 'Admin',      'admin@demo.com',   '$2b$10$19FuVEI0nvw7VNAr9JXJgeVeEq1N0SluO.Awn2fHhrimXE4Eit4Da', 'ADMIN');

INSERT INTO beats (id, titulo, genero, bpm, productor_id, url_preview, estado) VALUES
  (1, 'Perreo Galáctico', 'Dembow',    96, 1, 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 'PUBLICADO'),
  (2, 'Noche de Trap',    'Trap',     140, 1, 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3', 'PUBLICADO'),
  (3, 'Bachata Lunar',    'Bachata',  120, 2, 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3', 'PUBLICADO'),
  (4, 'Dembow del Barrio','Dembow',   100, 1, 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3', 'BORRADOR');

INSERT INTO tipos_licencia (id, beat_id, tipo, precio, condiciones) VALUES
  (1, 1, 'NO_EXCLUSIVA',      49900,  'Hasta 50.000 streams, 1 videoclip, crédito al productor.'),
  (2, 1, 'COMERCIAL_LIMITADA', 129900, 'Hasta 500.000 streams, shows en vivo incluidos.'),
  (3, 1, 'EXCLUSIVA',         499900, 'El beat sale del catálogo para otros compradores.'),
  (4, 2, 'NO_EXCLUSIVA',      39900,  'Hasta 50.000 streams, crédito al productor.'),
  (5, 2, 'EXCLUSIVA',         399900, 'El beat sale del catálogo para otros compradores.'),
  (6, 3, 'NO_EXCLUSIVA',      45900,  'Hasta 50.000 streams, crédito a la productora.'),
  (7, 3, 'COMERCIAL_LIMITADA', 119900, 'Hasta 500.000 streams, shows en vivo incluidos.');

-- Beat 1: split cerrado (demo del timeline + acuerdo CERRADO)
INSERT INTO colaboradores_beat (id, beat_id, usuario_id, rol, porcentaje_propuesto, estado) VALUES
  (1, 1, 2, 'CO_PRODUCTOR', 25, 'ACEPTADO');

INSERT INTO acuerdos_creditos (id, beat_id, estado, fecha_cierre) VALUES
  (1, 1, 'CERRADO', NOW()),
  (2, 4, 'ABIERTO', NULL);

INSERT INTO acuerdo_creditos_eventos (id, beat_id, usuario_id, tipo, detalle, fecha) VALUES
  (1, 1, 1, 'ABIERTO',    'DJ Kalu abrió el acuerdo de créditos',              NOW() - INTERVAL '3 days'),
  (2, 1, 1, 'PROPUESTA',  'DJ Kalu propuso a Yani Bloom como CO_PRODUCTOR (25%)', NOW() - INTERVAL '3 days'),
  (3, 1, 2, 'ACEPTACION', 'Yani Bloom aceptó su 25% como CO_PRODUCTOR',         NOW() - INTERVAL '2 days'),
  (4, 1, 1, 'CIERRE',     'Acuerdo cerrado: 25% + 75% del productor = 100%',   NOW() - INTERVAL '2 days'),
  (5, 4, 1, 'ABIERTO',    'DJ Kalu abrió el acuerdo de créditos',              NOW() - INTERVAL '1 day'),
  (6, 4, 1, 'PROPUESTA',  'DJ Kalu propuso a Yani Bloom como VOCALISTA (30%)', NOW() - INTERVAL '1 day');

-- Beat 4: split pendiente (demo de Mis splits para yani@demo.com)
INSERT INTO colaboradores_beat (id, beat_id, usuario_id, rol, porcentaje_propuesto, estado) VALUES
  (2, 4, 2, 'VOCALISTA', 30, 'PENDIENTE');

INSERT INTO notificaciones (id, usuario_id, tipo, mensaje, leida, fecha) VALUES
  (1, 2, 'INVITACION_COLABORACION', 'DJ Kalu te invitó como VOCALISTA (30%) en "Dembow del Barrio"', FALSE, NOW() - INTERVAL '1 day');

SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));
SELECT setval('beats_id_seq', (SELECT MAX(id) FROM beats));
SELECT setval('tipos_licencia_id_seq', (SELECT MAX(id) FROM tipos_licencia));
SELECT setval('colaboradores_beat_id_seq', (SELECT MAX(id) FROM colaboradores_beat));
SELECT setval('acuerdos_creditos_id_seq', (SELECT MAX(id) FROM acuerdos_creditos));
SELECT setval('acuerdo_creditos_eventos_id_seq', (SELECT MAX(id) FROM acuerdo_creditos_eventos));
SELECT setval('notificaciones_id_seq', (SELECT MAX(id) FROM notificaciones));

COMMIT;
