package ni.edu.uam.evaluacion_javafx_citastecnicas;

import javafx.application.Application;

/** Punto de entrada que evita el error "JavaFX runtime components are missing" al ejecutar desde el IDE. */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }
}
