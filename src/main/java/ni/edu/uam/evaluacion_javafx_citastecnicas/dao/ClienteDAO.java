package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;

import ni.edu.uam.evaluacion_javafx_citastecnicas.db.ConexionDB;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla cliente. Todo con PreparedStatement y try-with-resources. */
public class ClienteDAO {

    private static final String COLUMNAS = "id, nombres, apellidos, telefono, correo, activo";

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM cliente ORDER BY apellidos, nombres";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            return leer(resultado);
        }
    }

    /** RF05: coincidencia parcial en nombres, apellidos o correo, sin distinguir mayúsculas. */
    public List<Cliente> buscar(String texto) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM cliente"
                + " WHERE LOWER(nombres) LIKE ? ESCAPE '\\'"
                + " OR LOWER(apellidos) LIKE ? ESCAPE '\\'"
                + " OR LOWER(correo) LIKE ? ESCAPE '\\'"
                + " ORDER BY apellidos, nombres";
        String patron = "%" + escaparComodines(texto.trim().toLowerCase()) + "%";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, patron);
            sentencia.setString(2, patron);
            sentencia.setString(3, patron);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return leer(resultado);
            }
        }
    }

    public void insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarCampos(sentencia, cliente);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    cliente.setId(claves.getInt("id"));
                }
            }
        }
    }

    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, activo = ? WHERE id = ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarCampos(sentencia, cliente);
            sentencia.setInt(6, cliente.getId());
            sentencia.executeUpdate();
        }
    }

    /** Eliminación física. El controlador debe comprobar antes contarCitas(). */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        }
    }

    /** RF07: ¿el correo ya lo usa otro cliente? Para un cliente nuevo pase excluirId = 0. */
    public boolean existeCorreo(String correo, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE LOWER(correo) = LOWER(?) AND id <> ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, correo.trim());
            sentencia.setInt(2, excluirId);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        }
    }

    public int contarCitas(int clienteId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cita_tecnica WHERE cliente_id = ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, clienteId);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getInt(1) : 0;
            }
        }
    }

    private void asignarCampos(PreparedStatement sentencia, Cliente cliente) throws SQLException {
        sentencia.setString(1, cliente.getNombres().trim());
        sentencia.setString(2, cliente.getApellidos().trim());
        sentencia.setString(3, cliente.getTelefono().trim());
        sentencia.setString(4, cliente.getCorreo().trim().toLowerCase());
        sentencia.setBoolean(5, cliente.isActivo());
    }

    private List<Cliente> leer(ResultSet resultado) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        while (resultado.next()) {
            clientes.add(new Cliente(
                    resultado.getInt("id"),
                    resultado.getString("nombres"),
                    resultado.getString("apellidos"),
                    resultado.getString("telefono"),
                    resultado.getString("correo"),
                    resultado.getBoolean("activo")));
        }
        return clientes;
    }

    private String escaparComodines(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
