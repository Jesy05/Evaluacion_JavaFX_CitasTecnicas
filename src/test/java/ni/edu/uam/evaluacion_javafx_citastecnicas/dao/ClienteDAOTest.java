package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;

import ni.edu.uam.evaluacion_javafx_citastecnicas.ConexionDB;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


public class ClienteDAOTest {
    private final ClienteDAO dao = new ClienteDAO();
    private Cliente creado;
    private String sufijo;

    @BeforeEach
    void prepararYCrearCliente() throws SQLException {
        boolean disponible;
        try {
            disponible = ConexionDB.probarConexion();
        } catch (SQLException e) {
            disponible = false;
        }
        assumeTrue(disponible, "PostgreSQL no disponible: prueba omitida");

        sufijo = String.valueOf(System.nanoTime());
        creado = new Cliente(0, "Prueba", "Apellido" + sufijo, "88881234", "prueba" + sufijo + "@test.com", true);
        dao.insertar(creado);
    }

    @AfterEach
    void limpiar() throws SQLException {
        if (creado != null && creado.getId() > 0) {
            dao.eliminar(creado.getId());
        }
    }

    @Test
    void insertarAsignaUnIdGenerado() {
        assertTrue(creado.getId() > 0);
    }

    @Test
    void existeCorreoIgnoraMayusculasYPuedeExcluirAlPropioCliente() throws SQLException {
        assertTrue(dao.existeCorreo(creado.getCorreo().toUpperCase(), 0));
        assertFalse(dao.existeCorreo(creado.getCorreo(), creado.getId()));
    }

    @Test
    void buscarPorParteDelApellidoSinDistinguirMayusculas() throws SQLException {
        List<Cliente> encontrados = dao.buscar(("APELLIDO" + sufijo).substring(3));
        assertTrue(encontrados.stream().anyMatch(c -> c.getId() == creado.getId()));
    }

    @Test
    void actualizarTelefonoPersiste() throws SQLException {
        creado.setTelefono("77770000");
        dao.actualizar(creado);
        List<Cliente> encontrados = dao.buscar(creado.getCorreo());
        assertEquals(1, encontrados.size());
        assertEquals("77770000", encontrados.get(0).getTelefono());
    }

    @Test
    void clienteSinCitasTieneConteoCero() throws SQLException {
        assertEquals(0, dao.contarCitas(creado.getId()));
    }


}
