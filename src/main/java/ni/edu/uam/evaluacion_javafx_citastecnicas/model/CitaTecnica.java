package ni.edu.uam.evaluacion_javafx_citastecnicas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CitaTecnica {

    public static final List<String> ESTADOS = List.of("Pendiente", "En proceso", "Finalizada", "Cancelada");

    private int id;
    private int clienteId;
    private String clienteNombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String tipoServicio;
    private String descripcion;
    private String estado = "Pendiente";

}
