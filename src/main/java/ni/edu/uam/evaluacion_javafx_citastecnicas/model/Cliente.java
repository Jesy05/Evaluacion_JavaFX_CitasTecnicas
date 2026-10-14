package ni.edu.uam.evaluacion_javafx_citastecnicas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cliente {

    private int id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private boolean activo = true;

    public String getNombreCompleto() {
        return (nombres + " " + apellidos).trim();
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
