package ni.edu.uam.evaluacion_javafx_citastecnicas.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Clase centralizada de conexión JDBC (RF01).
 * Las credenciales NO están en el código: se leen de db.properties (ignorado por Git)
 * o de las variables de entorno DB_URL, DB_USER y DB_PASSWORD.
 */
public final class ConexionDB {

    private static final String ARCHIVO_CONFIG = "db.properties";
    private static final String URL_POR_DEFECTO = "jdbc:postgresql://localhost:5432/gestion_citas";
    private static final String USUARIO_POR_DEFECTO = "postgres";

    private static final Properties CONFIG = cargarConfig();

    private ConexionDB() {
    }

    /** Abre una conexión nueva; quien la recibe debe cerrarla con try-with-resources. */
    public static Connection getConexion() throws SQLException {
        String url = valor("db.url", "DB_URL", URL_POR_DEFECTO);
        String usuario = valor("db.user", "DB_USER", USUARIO_POR_DEFECTO);
        String clave = valor("db.password", "DB_PASSWORD", "");
        try {
            return DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException e) {
            throw new SQLException("No se pudo conectar con PostgreSQL (" + url + "). "
                    + "Verifique que el servicio esté activo y que las credenciales de db.properties sean correctas. "
                    + "Detalle: " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
        }
    }

    /** Comprueba la conexión con una sentencia fija (sin datos del usuario), por eso usa Statement. */
    public static boolean probarConexion() throws SQLException {
        try (Connection conexion = getConexion();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT 1")) {
            return resultado.next() && resultado.getInt(1) == 1;
        }
    }

    private static String valor(String clavePropiedad, String variableEntorno, String porDefecto) {
        String desdeEntorno = System.getenv(variableEntorno);
        if (desdeEntorno != null && !desdeEntorno.isBlank()) {
            return desdeEntorno;
        }
        return CONFIG.getProperty(clavePropiedad, porDefecto);
    }

    private static Properties cargarConfig() {
        Properties propiedades = new Properties();
        Path archivo = Path.of(ARCHIVO_CONFIG);
        if (Files.exists(archivo)) {
            try (InputStream entrada = Files.newInputStream(archivo)) {
                propiedades.load(entrada);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + ARCHIVO_CONFIG + ": " + e.getMessage());
            }
        }
        return propiedades;
    }
}
