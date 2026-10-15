# Guía para la defensa

Preguntas probables del docente y dónde está la respuesta en el código. Cada integrante debe poder
explicar el flujo completo y, en detalle, su parte.

## Flujo general (todas las operaciones)

1. El usuario pulsa un botón del formulario FXML (`onAction="#onGuardar"`).
2. El controlador (`ClienteController` / `CitaController`) lee los campos y arma un objeto del modelo.
3. `Validador` devuelve la lista de problemas; si hay alguno, `AlertUtil` los muestra y no se toca la BD.
4. El DAO abre una `Connection` con `ConexionDB.getConexion()`, crea un `PreparedStatement` con parámetros
   `?`, ejecuta y, si es una lectura, recorre el `ResultSet`.
5. El controlador muestra la alerta de éxito o de error SQL y recarga el `TableView`.

## Preguntas frecuentes

| Pregunta | Respuesta corta | Dónde verlo |
|----------|-----------------|-------------|
| ¿Dónde se crea la conexión? | En una sola clase, que lee credenciales de `db.properties` o de variables de entorno | `ConexionDB.getConexion` |
| ¿Dónde usan `Statement`? | En la comprobación de conexión: `SELECT 1`, sin datos del usuario | `ConexionDB.probarConexion` |
| ¿Por qué `PreparedStatement`? | Evita la inyección SQL y deja que el driver convierta tipos (fecha, hora) | Todos los DAO |
| ¿Cómo cierran los recursos? | `try-with-resources` cierra `Connection`, `PreparedStatement` y `ResultSet` | Todos los DAO |
| ¿Cómo se busca sin distinguir mayúsculas? | `LOWER(columna) LIKE ?` con el texto en minúsculas y `%` alrededor | `ClienteDAO.buscar` |
| ¿Cómo se evita el correo duplicado? | Se consulta antes (`existeCorreo`) y además hay `UNIQUE` en la tabla | `ClienteDAO`, `gestion_citas.sql` |
| ¿Cómo se impide borrar un cliente con citas? | El controlador cuenta sus citas y explica el motivo; la FK `ON DELETE RESTRICT` es la segunda barrera | `ClienteController.onEliminar` |
| ¿Cómo se filtran las citas por estado y fecha? | El SQL se arma con fragmentos fijos y los valores van como `?` | `CitaTecnicaDao.filtrar` |
| ¿Cómo se muestra el nombre del cliente en la tabla de citas? | `JOIN` entre `cita_tecnica` y `cliente` | `CitaTecnicaDao.SELECT_BASE` |
| ¿Cómo se actualiza el TableView? | Tras cada operación se vuelve a consultar y se hace `setAll` en la lista observable | `cargarTodos`, `aplicarFiltros` |
| ¿Dónde se valida el estado de la cita? | En `Validador` contra `CitaTecnica.ESTADOS` y en la BD con un `CHECK` | `Validador.validarCita` |
| ¿Qué pasa si PostgreSQL está apagado? | `MainApplication` muestra un aviso de conexión y las operaciones reportan el error SQL | `MainApplication`, `AlertUtil.errorSql` |

## Reparto de explicación

Corresponde a quién hizo cada parte según el historial de Git (`git log --format="%an | %s"`).

| Integrante | Debe poder explicar y ejecutar |
|------------|--------------------------------|
| Jesy | Script SQL, `ConexionDB`, modelos, `MainApplication` y cómo se carga cada pestaña |
| Gabriela | `Validador`, `AlertUtil` y las pruebas unitarias |
| Fabiola | `CitaTecnicaDao`, ComboBox de clientes, filtros y JOIN |
| José | `ClienteDAO`, búsqueda y eliminación restringida |

## Antes de la defensa

- [ ] La BD `gestion_citas` está creada y `gestion_citas.sql` ejecutado.
- [ ] `db.properties` existe en la máquina donde se defiende.
- [ ] `git pull` en `master` y la app abre sin errores.
- [ ] Se recorrió `docs/PRUEBAS_ACEPTACION.md` completo.
- [ ] `./mvnw test` pasa.
