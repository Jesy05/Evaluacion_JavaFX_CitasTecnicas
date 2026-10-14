package ni.edu.uam.evaluacion_javafx_citastecnicas.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CitaTecnica {

    /** Estados permitidos por la guía; también hay un CHECK en la tabla. */
    public static final List<String> ESTADOS = List.of("Pendiente", "En proceso", "Finalizada", "Cancelada");

    private int id;
    private int clienteId;
    /** Solo de lectura: viene del JOIN con cliente y se muestra en el TableView (RF04). */
    private String clienteNombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String tipoServicio;
    private String descripcion;
    private String estado = "Pendiente";

    public CitaTecnica() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
