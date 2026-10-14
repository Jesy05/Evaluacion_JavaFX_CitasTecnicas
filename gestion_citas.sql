-- =====================================================================
-- gestion_citas.sql - Evaluacion JavaFX + JDBC (TecnoAsistencia Nicaragua)
--
-- 1) Crear la base (una sola vez, conectado a la BD "postgres"):
--        CREATE DATABASE gestion_citas;
-- 2) Conectarse a gestion_citas (pgAdmin: Query Tool sobre esa BD) y ejecutar este script.
--    Es seguro ejecutarlo varias veces: no borra datos existentes.
-- =====================================================================

CREATE TABLE IF NOT EXISTS cliente (
    id        INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres   VARCHAR(80)  NOT NULL,
    apellidos VARCHAR(80)  NOT NULL,
    telefono  VARCHAR(20)  NOT NULL,
    correo    VARCHAR(120) NOT NULL,
    activo    BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_cliente_correo UNIQUE (correo)
);

CREATE TABLE IF NOT EXISTS cita_tecnica (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id    INT          NOT NULL,
    fecha         DATE         NOT NULL,
    hora          TIME         NOT NULL,
    tipo_servicio VARCHAR(80)  NOT NULL,
    descripcion   VARCHAR(255) NOT NULL,
    estado        VARCHAR(20)  NOT NULL DEFAULT 'Pendiente',
    -- RESTRICT: la base tambien impide borrar un cliente que tenga citas.
    CONSTRAINT fk_cita_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_cita_estado CHECK (estado IN ('Pendiente', 'En proceso', 'Finalizada', 'Cancelada'))
);

CREATE INDEX IF NOT EXISTS idx_cita_cliente ON cita_tecnica (cliente_id);
CREATE INDEX IF NOT EXISTS idx_cita_estado_fecha ON cita_tecnica (estado, fecha);
