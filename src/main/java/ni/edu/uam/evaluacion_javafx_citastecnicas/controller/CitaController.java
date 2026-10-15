package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.CitaTecnicaDao;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.AlertUtil;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.Validador;
import javafx.collections.ListChangeListener;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public class CitaController {

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtTipoServicio;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private DatePicker dpFiltroFecha;
    @FXML private Label lblTotal;


    @FXML private TableView<CitaTecnica> tblCitas;
    @FXML private TableColumn<CitaTecnica, Integer> colId;
    @FXML private TableColumn<CitaTecnica, String> colCliente;
    @FXML private TableColumn<CitaTecnica, LocalDate> colFecha;
    @FXML private TableColumn<CitaTecnica, LocalTime> colHora;
    @FXML private TableColumn<CitaTecnica, String> colServicio;
    @FXML private TableColumn<CitaTecnica, String> colEstado;

    private final CitaTecnicaDao citaDAO = new CitaTecnicaDao();
    private final ObservableList<CitaTecnica> citas = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("tipoServicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tblCitas.setItems(citas);

        // Muestra cuántas citas hay en la tabla con los filtros actuales.
        lblTotal.setText("Total: 0 cita(s)");
        citas.addListener((ListChangeListener<CitaTecnica>) cambio ->
                lblTotal.setText("Total: " + citas.size() + " cita(s)"));



        cmbEstado.getItems().setAll(CitaTecnica.ESTADOS);
        cmbEstado.setValue("Pendiente");
        cmbFiltroEstado.getItems().setAll(CitaTecnica.ESTADOS);

        // Si se crearon clientes en la otra pestaña, la lista se refresca al abrir el desplegable.
        cmbCliente.setOnShowing(evento -> cargarClientes());

        tblCitas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                mostrarEnFormulario(seleccionada);
            }
        });

        cargarClientes();
        aplicarFiltros();
    }

    @FXML
    private void onGuardar() {
        CitaTecnica cita = leerFormulario(0);
        if (!esValida(cita)) {
            return;
        }
        try {
            if (!citaDAO.existeCliente(cita.getClienteId())) {
                AlertUtil.advertencia("Cliente inexistente", "El cliente seleccionado ya no existe en la base de datos.");
                return;
            }
            citaDAO.insertar(cita);
            AlertUtil.exito("Cita registrada correctamente.");
            limpiarFormulario();
            aplicarFiltros();
        } catch (SQLException e) {
            AlertUtil.errorSql("registrar la cita", e);
        }
    }

    @FXML
    private void onActualizar() {
        CitaTecnica seleccionada = tblCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertUtil.advertencia("Sin selección", "Seleccione una cita de la tabla para modificarla.");
            return;
        }
        CitaTecnica cita = leerFormulario(seleccionada.getId());
        if (!esValida(cita)) {
            return;
        }
        try {
            if (!citaDAO.existeCliente(cita.getClienteId())) {
                AlertUtil.advertencia("Cliente inexistente", "El cliente seleccionado ya no existe en la base de datos.");
                return;
            }
            citaDAO.actualizar(cita);
            AlertUtil.exito("Cita actualizada correctamente.");
            limpiarFormulario();
            aplicarFiltros();
        } catch (SQLException e) {
            AlertUtil.errorSql("actualizar la cita", e);
        }
    }


    @FXML
    private void onEliminar() {
        CitaTecnica seleccionada = tblCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertUtil.advertencia("Sin selección", "Seleccione una cita de la tabla para eliminarla.");
            return;
        }
        if (!AlertUtil.confirmar("Eliminar cita",
                "¿Seguro que desea eliminar la cita #" + seleccionada.getId() + " de "
                        + seleccionada.getClienteNombre() + "? Esta acción no se puede deshacer.")) {
            return;
        }
        try {
            citaDAO.eliminar(seleccionada.getId());
            AlertUtil.exito("Cita eliminada correctamente.");
            limpiarFormulario();
            aplicarFiltros();
        } catch (SQLException e) {
            AlertUtil.errorSql("eliminar la cita", e);
        }
    }
    /** Cambia el estado de la cita seleccionada a Finalizada sin editar todo el formulario. */
    @FXML
    private void onMarcarFinalizada() {
        CitaTecnica seleccionada = tblCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertUtil.advertencia("Sin selección", "Seleccione una cita de la tabla para marcarla como finalizada.");
            return;
        }
        if ("Finalizada".equals(seleccionada.getEstado())) {
            AlertUtil.informacion("Sin cambios", "La cita #" + seleccionada.getId() + " ya está finalizada.");
            return;
        }
        try {
            citaDAO.actualizarEstado(seleccionada.getId(), "Finalizada");
            AlertUtil.exito("La cita #" + seleccionada.getId() + " fue marcada como finalizada.");
            limpiarFormulario();
            aplicarFiltros();
        } catch (SQLException e) {
            AlertUtil.errorSql("actualizar el estado de la cita", e);
        }
    }

    @FXML
    private void onFiltrar() {
        aplicarFiltros();
    }

    /** RF06: limpia los filtros y recupera todos los registros. */
    @FXML
    private void onLimpiarFiltros() {
        cmbFiltroEstado.setValue(null);
        dpFiltroFecha.setValue(null);
        aplicarFiltros();
    }

    @FXML
    private void onLimpiar() {
        limpiarFormulario();
    }

    @FXML
    private void onRecargar() {
        cargarClientes();
        aplicarFiltros();
    }

    /** Recarga el TableView respetando los filtros que estén puestos (RF09). */
    private void aplicarFiltros() {
        try {
            citas.setAll(citaDAO.filtrar(cmbFiltroEstado.getValue(), dpFiltroFecha.getValue()));
        } catch (SQLException e) {
            AlertUtil.errorSql("cargar las citas", e);
        }
    }

    private void cargarClientes() {
        Cliente actual = cmbCliente.getValue();
        try {
            List<Cliente> clientes = citaDAO.listarClientes();
            cmbCliente.getItems().setAll(clientes);
            if (actual != null) {
                seleccionarCliente(actual.getId());
            }
        } catch (SQLException e) {
            AlertUtil.errorSql("cargar los clientes", e);
        }
    }

    private void seleccionarCliente(int clienteId) {
        for (Cliente cliente : cmbCliente.getItems()) {
            if (cliente.getId() == clienteId) {
                cmbCliente.setValue(cliente);
                return;
            }
        }
    }

    private CitaTecnica leerFormulario(int id) {
        CitaTecnica cita = new CitaTecnica();
        cita.setId(id);
        Cliente cliente = cmbCliente.getValue();
        cita.setClienteId(cliente == null ? 0 : cliente.getId());
        cita.setFecha(dpFecha.getValue());
        cita.setHora(Validador.parsearHora(txtHora.getText()));
        cita.setTipoServicio(txtTipoServicio.getText());
        cita.setDescripcion(txtDescripcion.getText());
        cita.setEstado(cmbEstado.getValue());
        return cita;
    }

    private boolean esValida(CitaTecnica cita) {
        List<String> problemas = Validador.validarCita(cita);
        if (!problemas.isEmpty()) {
            AlertUtil.errores("Revise los datos de la cita", problemas);
            return false;
        }
        return true;
    }

    private void mostrarEnFormulario(CitaTecnica cita) {
        seleccionarCliente(cita.getClienteId());
        dpFecha.setValue(cita.getFecha());
        txtHora.setText(cita.getHora().toString());
        txtTipoServicio.setText(cita.getTipoServicio());
        txtDescripcion.setText(cita.getDescripcion());
        cmbEstado.setValue(cita.getEstado());
    }

    private void limpiarFormulario() {
        tblCitas.getSelectionModel().clearSelection();
        cmbCliente.setValue(null);
        dpFecha.setValue(null);
        txtHora.clear();
        txtTipoServicio.clear();
        txtDescripcion.clear();
        cmbEstado.setValue("Pendiente");
    }
}



