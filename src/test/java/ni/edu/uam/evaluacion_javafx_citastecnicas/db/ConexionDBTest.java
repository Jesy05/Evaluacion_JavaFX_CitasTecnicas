package ni.edu.uam.evaluacion_javafx_citastecnicas.db;

import ni.edu.uam.evaluacion_javafx_citastecnicas.ConexionDB;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Pruebas de integración: se omiten solas si PostgreSQL no está disponible. */
class ConexionDBTest {

    private boolean baseDisponible() {
        try {
            return ConexionDB.probarConexion();
        } catch (SQLException e) {
            return false;
        }
    }

    @Test
    void probarConexionDevuelveTrueConLaBaseActiva() throws SQLException {
        assumeTrue(baseDisponible(), "PostgreSQL no disponible: prueba omitida");
        assertTrue(ConexionDB.probarConexion());
    }

    @Test
    void lasTablasObligatoriasExisten() throws SQLException {
        assumeTrue(baseDisponible(), "PostgreSQL no disponible: prueba omitida");
        String sql = "SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_schema = 'public' AND table_name = ?";
        for (String tabla : new String[]{"cliente", "cita_tecnica"}) {
            try (Connection conexion = ConexionDB.getConexion();
                 PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, tabla);
                try (ResultSet resultado = sentencia.executeQuery()) {
                    assertTrue(resultado.next() && resultado.getInt(1) == 1,
                            "Falta la tabla " + tabla + ": ejecute gestion_citas.sql");
                }
            }
        }
    }
}
