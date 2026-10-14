package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.NoArgsConstructor;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public final class AlertUtil {
    public static void exito(String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, "Operación exitosa", mensaje);
    }

    public static void informacion(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void advertencia(String titulo, String mensaje) {
        mostrar(Alert.AlertType.WARNING, titulo, mensaje);
    }

    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void errores(String titulo, List<String> mensajes) {
        mostrar(Alert.AlertType.WARNING, titulo, "- " + String.join("\n- ", mensajes));
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.OK, ButtonType.CANCEL);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    public static void errorSql(String operacion, SQLException e) {
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        String explicacion;
        if (estado.equals("23505")) {
            explicacion = "Ya existe un registro con ese valor único (por ejemplo, un correo repetido).";
        } else if (estado.equals("23503")) {
            explicacion = "El registro está relacionado con otros datos (cliente con citas, o cliente inexistente).";
        } else if (estado.equals("23514")) {
            explicacion = "Un valor no cumple las reglas permitidas por la base de datos (por ejemplo, el estado).";
        } else if (estado.equals("23502")) {
            explicacion = "Falta un dato obligatorio.";
        } else if (estado.equals("22001")) {
            explicacion = "Un texto es más largo de lo permitido.";
        } else if (estado.startsWith("08")) {
            explicacion = "No hay conexión con PostgreSQL. Verifique que el servicio esté activo y db.properties.";
        } else {
            explicacion = e.getMessage();
        }
        mostrar(Alert.AlertType.ERROR, "Error al " + operacion, explicacion);
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


}
