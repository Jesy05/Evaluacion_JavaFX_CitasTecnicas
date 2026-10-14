package ni.edu.uam.evaluacion_javafx_citastecnicas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ni.edu.uam.evaluacion_javafx_citastecnicas.db.ConexionDB;

import java.io.IOException;
import java.sql.SQLException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        verificarConexion();

        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/view/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 680);
        scene.getStylesheets().add(MainApplication.class.getResource("/css/estilos.css").toExternalForm());

        stage.setTitle("TecnoAsistencia Nicaragua - Gestión de citas técnicas");
        stage.setScene(scene);
        stage.show();
    }

    /** RF01: informa al usuario si no hay conexión, en lugar de fallar en silencio. */
    private void verificarConexion() {
        try {
            ConexionDB.probarConexion();
        } catch (SQLException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de conexión");
            alerta.setHeaderText("No se pudo conectar con la base de datos");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
    }
}
