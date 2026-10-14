package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;
import ni.edu.uam.evaluacion_javafx_citastecnicas.db.ConexionDB;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CitaTecnicaDao {

    private static final String SELECT_BASE =
            "SELECT c.id, c.cliente_id, cl.nombres || ' ' || cl.apellidos AS cliente_nombre, "
                    + "c.fecha, c.hora, c.tipo_servicio, c.descripcion, c.estado "
                    + "FROM cita_tecnica c JOIN cliente cl ON cl.id = c.cliente_id";

    public List<CitaTecnica> listar() throws SQLException {
        return filtrar(null, null);
    }

    /**
     * RF06: filtra por estado y/o fecha. Un parámetro null significa "sin ese filtro".
     * Solo se concatenan fragmentos fijos del programa; los valores siempre van como parámetros (?).
     */
    public List<CitaTecnica> filtrar(String estado, LocalDate fecha) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append(" WHERE 1 = 1");
        if (estado != null) {
            sql.append(" AND c.estado = ?");
        }
        if (fecha != null) {
            sql.append(" AND c.fecha = ?");
        }
        sql.append(" ORDER BY c.fecha, c.hora");

        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {
            int posicion = 1;
            if (estado != null) {
                sentencia.setString(posicion++, estado);
            }
            if (fecha != null) {
                sentencia.setDate(posicion, Date.valueOf(fecha));
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                List<CitaTecnica> citas = new ArrayList<>();
                while (resultado.next()) {
                    citas.add(mapear(resultado));
                }
                return citas;
            }
        }
    }

    public void insertar(CitaTecnica cita) throws SQLException {
        String sql = "INSERT INTO cita_tecnica (cliente_id, fecha, hora, tipo_servicio, descripcion, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarCampos(sentencia, cita);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    cita.setId(claves.getInt("id"));
                }
            }
        }
    }

    public void actualizar(CitaTecnica cita) throws SQLException {
        String sql = "UPDATE cita_tecnica SET cliente_id = ?, fecha = ?, hora = ?, tipo_servicio = ?, "
                + "descripcion = ?, estado = ? WHERE id = ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarCampos(sentencia, cita);
            sentencia.setInt(7, cita.getId());
            sentencia.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cita_tecnica WHERE id = ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        }
    }

    /** RF07: el cliente debe existir. */
    public boolean existeCliente(int clienteId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE id = ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, clienteId);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        }
    }

    /** Clientes para llenar el ComboBox del formulario de citas (RF03). */
    public List<Cliente> listarClientes() throws SQLException {
        String sql = "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente ORDER BY apellidos, nombres";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
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
    }

    private void asignarCampos(PreparedStatement sentencia, CitaTecnica cita) throws SQLException {
        sentencia.setInt(1, cita.getClienteId());
        sentencia.setDate(2, Date.valueOf(cita.getFecha()));
        sentencia.setTime(3, Time.valueOf(cita.getHora()));
        sentencia.setString(4, cita.getTipoServicio().trim());
        sentencia.setString(5, cita.getDescripcion().trim());
        sentencia.setString(6, cita.getEstado());
    }

    private CitaTecnica mapear(ResultSet resultado) throws SQLException {
        CitaTecnica cita = new CitaTecnica();
        cita.setId(resultado.getInt("id"));
        cita.setClienteId(resultado.getInt("cliente_id"));
        cita.setClienteNombre(resultado.getString("cliente_nombre"));
        cita.setFecha(resultado.getDate("fecha").toLocalDate());
        cita.setHora(resultado.getTime("hora").toLocalTime());
        cita.setTipoServicio(resultado.getString("tipo_servicio"));
        cita.setDescripcion(resultado.getString("descripcion"));
        cita.setEstado(resultado.getString("estado"));
        return cita;
    }
}


