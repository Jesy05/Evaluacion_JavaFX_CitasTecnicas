# Pruebas de aceptación (guía, sección 9)

Marcar cada prueba al ejecutarla ante el docente.
Requisito: base `gestion_citas` creada y script `gestion_citas.sql` ejecutado (ver README).

| # | Prueba | Resultado esperado | OK |
|---|--------|--------------------|----|
| 1 | Registrar un cliente | Aparece en el TableView de clientes | [ ] |
| 2 | Registrar otro cliente con el mismo correo | Se rechaza con mensaje de correo duplicado | [ ] |
| 3 | Modificar el teléfono del cliente | El cambio se ve en la tabla y persiste al recargar | [ ] |
| 4 | Registrar una cita asociada a ese cliente | La tabla de citas muestra el nombre completo del cliente | [ ] |
| 5 | Buscar por una parte del apellido (en minúsculas) | Solo aparecen los clientes que coinciden | [ ] |
| 6 | Filtrar citas por estado y por fecha, luego limpiar filtros | Se filtra y al limpiar vuelven todos los registros | [ ] |
| 7 | Cambiar el estado de una cita a Finalizada | El estado se actualiza y persiste | [ ] |
| 8 | Intentar eliminar un cliente con citas | Se rechaza explicando el motivo | [ ] |
| 9 | Eliminar la cita y luego el cliente | Cada eliminación pide confirmación y se completa | [ ] |
| 10 | Cerrar y volver a abrir la aplicación | Los cambios válidos siguen en la base | [ ] |
| 11 | Mostrar commits y autores del 12, 13 y 14 de octubre | Salida de `git log` (ver abajo) | [ ] |

Comando para la prueba 11:

```bash
git log --format="%h | %an | %ad | %s" --date=short
```

## Validaciones adicionales (RF07)

- Campos vacíos en cliente o cita: un mensaje por cada campo.
- Correo sin formato `usuario@dominio.ext`: rechazado.
- Fecha u hora vacía, u hora mal escrita (por ejemplo `25:00`): rechazado.
- Cita sin cliente seleccionado: rechazado.
- Estado fuera de Pendiente, En proceso, Finalizada, Cancelada: rechazado.

## Pruebas automáticas

```bash
./mvnw test
```

- `ValidadorTest` y `ValidadorCasosBordeTest`: no necesitan base de datos.
- `ConexionDBTest`: necesita PostgreSQL con las tablas creadas; si no hay conexión se omite sola.
