package ni.edu.uam.evaluacion_javafx_citastecnicas.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeloTest {

    @Test
    void losEstadosPermitidosSonLosDeLaGuia() {
        assertEquals(4, CitaTecnica.ESTADOS.size());
        assertTrue(CitaTecnica.ESTADOS.containsAll(
                java.util.List.of("Pendiente", "En proceso", "Finalizada", "Cancelada")));
    }

    @Test
    void unaCitaNuevaEmpiezaPendiente() {
        assertEquals("Pendiente", new CitaTecnica().getEstado());
    }

    @Test
    void nombreCompletoUneNombresYApellidos() {
        Cliente cliente = new Cliente(1, "Ana Lucía", "Rivas Castillo", "88881234", "ana@correo.com", true);
        assertEquals("Ana Lucía Rivas Castillo", cliente.getNombreCompleto());
        assertEquals("Ana Lucía Rivas Castillo", cliente.toString());
    }

    @Test
    void unClienteNuevoEmpiezaActivo() {
        assertTrue(new Cliente().isActivo());
    }
}
