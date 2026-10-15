-- =====================================================================
-- datos_prueba.sql - Datos de ejemplo para demostrar la aplicacion
--
-- Ejecutar DESPUES de gestion_citas.sql, conectado a la BD gestion_citas.
-- Es seguro ejecutarlo varias veces: no duplica clientes ni citas.
-- Para quitar estos datos antes de una prueba limpia:
--     DELETE FROM cita_tecnica; DELETE FROM cliente;
-- =====================================================================

INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) VALUES
                                                                       ('Maria Jose',  'Gonzalez Ruiz',  '8888-1001', 'maria.gonzalez@ejemplo.com', TRUE),
                                                                       ('Carlos Luis', 'Mendoza Pineda', '8888-1002', 'carlos.mendoza@ejemplo.com', TRUE),
                                                                       ('Ana Lucia',   'Rivas Castillo', '8888-1003', 'ana.rivas@ejemplo.com',      TRUE),
                                                                       ('Pedro',       'Lopez Altamirano','8888-1004', 'pedro.lopez@ejemplo.com',   FALSE)
    ON CONFLICT (correo) DO NOTHING;

INSERT INTO cita_tecnica (cliente_id, fecha, hora, tipo_servicio, descripcion, estado)
SELECT c.id, v.fecha, v.hora, v.servicio, v.descripcion, v.estado
FROM (VALUES
          ('maria.gonzalez@ejemplo.com', DATE '2026-10-15', TIME '09:00', 'Mantenimiento', 'Limpieza interna y cambio de pasta termica', 'Pendiente'),
          ('maria.gonzalez@ejemplo.com', DATE '2026-10-16', TIME '14:30', 'Reparacion',    'La laptop no enciende',                    'En proceso'),
          ('carlos.mendoza@ejemplo.com', DATE '2026-10-15', TIME '11:00', 'Instalacion',   'Instalar Windows y programas basicos',      'Finalizada'),
          ('ana.rivas@ejemplo.com',      DATE '2026-10-17', TIME '10:15', 'Diagnostico',   'Equipo muy lento al iniciar',               'Pendiente'),
          ('ana.rivas@ejemplo.com',      DATE '2026-10-14', TIME '16:00', 'Mantenimiento', 'Revision general de impresora',             'Cancelada')
     ) AS v(correo, fecha, hora, servicio, descripcion, estado)
         JOIN cliente c ON c.correo = v.correo
WHERE NOT EXISTS (
    SELECT 1 FROM cita_tecnica t
    WHERE t.cliente_id = c.id AND t.fecha = v.fecha AND t.hora = v.hora
);
