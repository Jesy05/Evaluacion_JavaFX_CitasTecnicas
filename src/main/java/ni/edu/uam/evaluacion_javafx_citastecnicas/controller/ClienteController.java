package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.AlertUtil;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.Validador;

import java.sql.SQLException;
import java.util.List;

/** RF02, RF05, RF07, RF08, RF09 para el módulo de clientes. */
public class ClienteController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, Boolean> colActivo;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tblClientes.setItems(clientes);

        // Al seleccionar una fila, sus datos pasan al formulario.
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                mostrarEnFormulario(seleccionado);
            }
        });

        chkActivo.setSelected(true);
        cargarTodos();
    }

    @FXML
    private void onGuardar() {
        Cliente cliente = leerFormulario(0);
        if (!esValido(cliente)) {
            return;
        }
        try {
            if (clienteDAO.existeCorreo(cliente.getCorreo(), 0)) {
                AlertUtil.advertencia("Correo duplicado", "Ya existe un cliente con el correo " + cliente.getCorreo() + ".");
                return;
            }
            clienteDAO.insertar(cliente);
            AlertUtil.exito("Cliente registrado correctamente.");
            limpiarFormulario();
            cargarTodos();
        } catch (SQLException e) {
            AlertUtil.errorSql("registrar el cliente", e);
        }
    }

    @FXML
    private void onActualizar() {
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.advertencia("Sin selección", "Seleccione un cliente de la tabla para modificarlo.");
            return;
        }
        Cliente cliente = leerFormulario(seleccionado.getId());
        if (!esValido(cliente)) {
            return;
        }
        try {
            if (clienteDAO.existeCorreo(cliente.getCorreo(), cliente.getId())) {
                AlertUtil.advertencia("Correo duplicado", "Otro cliente ya usa el correo " + cliente.getCorreo() + ".");
                return;
            }
            clienteDAO.actualizar(cliente);
            AlertUtil.exito("Cliente actualizado correctamente.");
            limpiarFormulario();
            cargarTodos();
        } catch (SQLException e) {
            AlertUtil.errorSql("actualizar el cliente", e);
        }
    }

    @FXML
    private void onEliminar() {
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.advertencia("Sin selección", "Seleccione un cliente de la tabla para eliminarlo.");
            return;
        }
        try {
            int citas = clienteDAO.contarCitas(seleccionado.getId());
            if (citas > 0) {
                AlertUtil.advertencia("Eliminación no permitida",
                        "No se puede eliminar a " + seleccionado.getNombreCompleto() + " porque tiene "
                                + citas + " cita(s) técnica(s) asociada(s). Elimine primero sus citas.");
                return;
            }
            if (!AlertUtil.confirmar("Eliminar cliente",
                    "¿Seguro que desea eliminar a " + seleccionado.getNombreCompleto() + "? Esta acción no se puede deshacer.")) {
                return;
            }
            clienteDAO.eliminar(seleccionado.getId());
            AlertUtil.exito("Cliente eliminado correctamente.");
            limpiarFormulario();
            cargarTodos();
        } catch (SQLException e) {
            AlertUtil.errorSql("eliminar el cliente", e);
        }
    }

    @FXML
    private void onBuscar() {
        String texto = txtBuscar.getText();
        if (texto == null || texto.isBlank()) {
            cargarTodos();
            return;
        }
        try {
            List<Cliente> encontrados = clienteDAO.buscar(texto);
            clientes.setAll(encontrados);
            if (encontrados.isEmpty()) {
                AlertUtil.informacion("Sin resultados", "No hay clientes que coincidan con \"" + texto.trim() + "\".");
            }
        } catch (SQLException e) {
            AlertUtil.errorSql("buscar clientes", e);
        }
    }

    @FXML
    private void onVerTodos() {
        txtBuscar.clear();
        cargarTodos();
    }

    @FXML
    private void onLimpiar() {
        limpiarFormulario();
    }

    private void cargarTodos() {
        try {
            clientes.setAll(clienteDAO.listar());
        } catch (SQLException e) {
            AlertUtil.errorSql("cargar los clientes", e);
        }
    }

    private Cliente leerFormulario(int id) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombres(txtNombres.getText());
        cliente.setApellidos(txtApellidos.getText());
        cliente.setTelefono(txtTelefono.getText());
        cliente.setCorreo(txtCorreo.getText());
        cliente.setActivo(chkActivo.isSelected());
        return cliente;
    }

    private boolean esValido(Cliente cliente) {
        List<String> problemas = Validador.validarCliente(cliente);
        if (!problemas.isEmpty()) {
            AlertUtil.errores("Revise los datos del cliente", problemas);
            return false;
        }
        return true;
    }

    private void mostrarEnFormulario(Cliente cliente) {
        txtNombres.setText(cliente.getNombres());
        txtApellidos.setText(cliente.getApellidos());
        txtTelefono.setText(cliente.getTelefono());
        txtCorreo.setText(cliente.getCorreo());
        chkActivo.setSelected(cliente.isActivo());
    }

    private void limpiarFormulario() {
        tblClientes.getSelectionModel().clearSelection();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        chkActivo.setSelected(true);
    }
}
