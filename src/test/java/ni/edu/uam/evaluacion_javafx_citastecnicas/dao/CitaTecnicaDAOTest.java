package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;
import ni.edu.uam.evaluacion_javafx_citastecnicas.ConexionDB;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CitaTecnicaDAOTest {

    private static final LocalDate FECHA_PRUEBA = LocalDate.of(2099, 1, 1);

    private final CitaTecnicaDao dao = new CitaTecnicaDao();
    private int clienteId;

    @BeforeEach
    void crearClienteDePrueba() throws SQLException {
        boolean disponible;
        try {
            disponible = ConexionDB.probarConexion();
        } catch (SQLException e) {
            disponible = false;
        }
        assumeTrue(disponible, "PostgreSQL no disponible: prueba omitida");

        String sufijo = String.valueOf(System.nanoTime());
        String sql = "INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) "
                + "VALUES (?, ?, ?, ?, TRUE) RETURNING id";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, "Cliente");
            sentencia.setString(2, "Prueba" + sufijo);
            sentencia.setString(3, "88881234");
            sentencia.setString(4, "cita" + sufijo + "@test.com");
            try (ResultSet resultado = sentencia.executeQuery()) {
                resultado.next();
                clienteId = resultado.getInt(1);
            }
        }
    }

    @AfterEach
    void limpiar() throws SQLException {
        if (clienteId > 0) {
            try (Connection conexion = ConexionDB.getConexion()) {
                try (PreparedStatement citas = conexion.prepareStatement("DELETE FROM cita_tecnica WHERE cliente_id = ?")) {
                    citas.setInt(1, clienteId);
                    citas.executeUpdate();
                }
                try (PreparedStatement cliente = conexion.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
                    cliente.setInt(1, clienteId);
                    cliente.executeUpdate();
                }
            }
        }
    }

    private CitaTecnica nuevaCita(String estado) {
        CitaTecnica cita = new CitaTecnica();
        cita.setClienteId(clienteId);
        cita.setFecha(FECHA_PRUEBA);
        cita.setHora(LocalTime.of(10, 0));
        cita.setTipoServicio("Mantenimiento");
        cita.setDescripcion("Cita creada por una prueba automatica");
        cita.setEstado(estado);
        return cita;
    }

    private boolean contiene(List<CitaTecnica> citas, int id) {
        return citas.stream().anyMatch(c -> c.getId() == id);
    }

    @Test
    void insertarYListarMuestraElNombreCompletoDelCliente() throws SQLException {
        CitaTecnica cita = nuevaCita("Pendiente");
        dao.insertar(cita);
        assertTrue(cita.getId() > 0);

        CitaTecnica leida = dao.filtrar(null, FECHA_PRUEBA).stream()
                .filter(c -> c.getId() == cita.getId()).findFirst().orElseThrow();
        assertTrue(leida.getClienteNombre().startsWith("Cliente Prueba"));
    }

    @Test
    void filtrarPorEstadoYFecha() throws SQLException {
        CitaTecnica cita = nuevaCita("En proceso");
        dao.insertar(cita);

        assertTrue(contiene(dao.filtrar("En proceso", FECHA_PRUEBA), cita.getId()));
        assertFalse(contiene(dao.filtrar("Cancelada", FECHA_PRUEBA), cita.getId()));
        assertFalse(contiene(dao.filtrar("En proceso", FECHA_PRUEBA.plusDays(1)), cita.getId()));
    }

    @Test
    void actualizarEstadoAFinalizadaPersiste() throws SQLException {
        CitaTecnica cita = nuevaCita("Pendiente");
        dao.insertar(cita);

        dao.actualizarEstado(cita.getId(), "Finalizada");

        assertTrue(contiene(dao.filtrar("Finalizada", FECHA_PRUEBA), cita.getId()));
    }

    @Test
    void eliminarQuitaLaCita() throws SQLException {
        CitaTecnica cita = nuevaCita("Pendiente");
        dao.insertar(cita);

        dao.eliminar(cita.getId());

        assertFalse(contiene(dao.filtrar(null, FECHA_PRUEBA), cita.getId()));
    }

    @Test
    void existeClienteDistingueEntreRealEInexistente() throws SQLException {
        assertTrue(dao.existeCliente(clienteId));
        assertFalse(dao.existeCliente(Integer.MAX_VALUE));
    }
}


