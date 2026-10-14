package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import java.net.URL;

/** Ventana principal: carga cada módulo (clientes, citas) en su pestaña. */
public class MainController {

    @FXML private Tab tabClientes;
    @FXML private Tab tabCitas;

    @FXML
    private void initialize() {
        cargarVista(tabClientes, "/view/cliente-view.fxml");
        cargarVista(tabCitas, "/view/cita-view.fxml");
    }

    private void cargarVista(Tab pestana, String ruta) {
        URL url = MainController.class.getResource(ruta);
        if (url == null) {
            pestana.setContent(new Label("Módulo aún no disponible: " + ruta));
            return;
        }
        try {
            pestana.setContent(new FXMLLoader(url).load());
        } catch (Exception e) {
            e.printStackTrace();
            pestana.setContent(new Label("No se pudo cargar " + ruta + ": " + e.getMessage()));
        }
    }
}
