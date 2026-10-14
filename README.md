# Evaluacion_JavaFX_CitasTecnicas  (JavaFX)

Aplicación de escritorio desarrollada para la asignatura **Programación de Aplicaciones de Escritorio** (Semana 8).  
Sistema de gestión de citas de atención técnica para **TecnoAsistencia Nicaragua S.A.**
Aplicación de escritorio JavaFX conectada a PostgreSQL mediante JDBC (sin ORM).

[![Repo GitHub](https://img.shields.io/badge/GitHub-AplicacionRegistro-blue?style=flat&logo=github)](https://github.com/Jesy05/AplicacionRegistro)
[![JavaFX](https://img.shields.io/badge/JavaFX-17%2B-orange?style=flat&logo=java)](https://openjfx.io/)
[![IDE](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-purple?style=flat&logo=intellijidea)](https://www.jetbrains.com/idea/)

---

 
Asignatura: Programación de Aplicaciones de Escritorio, Unidad II (UAM). Docente: José A. Durán García.
 
## Equipo
 
| Integrante | Usuario GitHub | Responsabilidad |
|------------|----------------|-----------------|
| Jesy González | [@Jesy05](https://github.com/Jesy05) | Estructura del proyecto, script SQL, conexión JDBC centralizada, modelos, ventana principal |
| Gabriela _APELLIDOS_ | @USUARIO_GABRIELA | CRUD de clientes: `ClienteDAO`, formulario, TableView y búsqueda |
| Fabiola _APELLIDOS_ | @USUARIO_FABIOLA | CRUD de citas: `CitaTecnicaDAO`, formulario, TableView, ComboBox de clientes y filtros |
| José Cristo _APELLIDOS_ | @USUARIO_JOSE | Validaciones, alertas, pruebas unitarias, README y documentación de pruebas |
 
## Requisitos
 
- JDK 21
- PostgreSQL 14 o superior (con pgAdmin 4 opcional)
- Maven (incluido como `mvnw`) o IntelliJ IDEA
 
## Instalación y ejecución
 
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Jesy05/Evaluacion_JavaFX_CitasTecnicas.git
   ```
2. En PostgreSQL crear la base de datos:
   ```sql
   CREATE DATABASE gestion_citas;
   ```
3. Conectado a `gestion_citas`, ejecutar el script `gestion_citas.sql` (crea las tablas `cliente` y `cita_tecnica`).
4. Copiar `db.properties.example` como `db.properties` en la raíz y escribir las credenciales locales:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/gestion_citas
   db.user=postgres
   db.password=su_password
   ```
   `db.properties` está en `.gitignore`: las contraseñas **no** se suben al repositorio.
   Alternativa: variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD`.
5. Ejecutar:
   - **IntelliJ IDEA:** abrir el proyecto, esperar la importación de Maven y ejecutar la clase `Launcher`.
   - **Terminal:** `./mvnw clean javafx:run`
6. Pruebas unitarias: `./mvnw test`
 
## Modelo de datos
 
`cliente (1) ── (N) cita_tecnica`, con clave foránea `cita_tecnica.cliente_id → cliente.id` (`ON DELETE RESTRICT`).
Estados permitidos de la cita: Pendiente, En proceso, Finalizada, Cancelada.
 
## Arquitectura
 
```
ni.edu.uam.evaluacion_javafx_citastecnicas
├── db          ConexionDB (conexión JDBC centralizada)
├── model       Cliente, CitaTecnica
├── dao         ClienteDAO, CitaTecnicaDAO (SQL con PreparedStatement)
├── controller  MainController, ClienteController, CitaController
└── util        Validador, AlertUtil
resources/view  main-view.fxml, cliente-view.fxml, cita-view.fxml
```
 
### Flujo JDBC
 
1. El controlador FXML recibe la acción del usuario y arma el objeto del modelo.
2. `Validador` revisa los datos; si hay problemas se muestran con `AlertUtil`.
3. El DAO obtiene una `Connection` de `ConexionDB`, prepara un `PreparedStatement` con parámetros `?`
   y lo ejecuta; las lecturas recorren un `ResultSet`.
4. Todo se cierra con `try-with-resources`. Las `SQLException` se traducen a mensajes claros.
5. El controlador recarga el `TableView` para reflejar el cambio sin reiniciar.
 
`ConexionDB.probarConexion()` usa un `Statement` con la sentencia fija `SELECT 1`.
Todas las sentencias con valores del usuario usan `PreparedStatement`.
 
## Requerimientos
 
| Código | Dónde se cumple |
|--------|-----------------|
| RF01 | `ConexionDB`, aviso de error en `MainApplication` |
| RF02 | `ClienteController` + `cliente-view.fxml` + `ClienteDAO` |
| RF03, RF04 | `CitaController` + `cita-view.fxml` + `CitaTecnicaDAO` |
| RF05 | `ClienteDAO.buscar` |
| RF06 | `CitaTecnicaDAO.filtrar` |
| RF07 | `Validador` + comprobaciones en los DAO |
| RF08 | `AlertUtil` y confirmaciones en los controladores |
| RF09 | Recarga del TableView tras cada operación |
| RF10 | PostgreSQL |
 
## Pruebas de aceptación
 
Ver [docs/PRUEBAS_ACEPTACION.md](docs/PRUEBAS_ACEPTACION.md).
 
## Capturas
 
Agregar las capturas en `docs/capturas/` y enlazarlas aquí:
 
- Clientes: _pendiente_
- Citas: _pendiente_
- Eliminación restringida: _pendiente_
