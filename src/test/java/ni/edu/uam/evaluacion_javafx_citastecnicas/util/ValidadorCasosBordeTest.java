package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Límites de longitud (coinciden con la BD) y casos borde de las validaciones. */
class ValidadorCasosBordeTest {

    private Cliente clienteBase() {
        return new Cliente(0, "Ana", "Lopez", "88881234", "ana@correo.com", true);
    }

    private CitaTecnica citaBase() {
        CitaTecnica cita = new CitaTecnica();
        cita.setClienteId(1);
        cita.setFecha(LocalDate.of(2026, 10, 15));
        cita.setHora(LocalTime.of(9, 0));
        cita.setTipoServicio("Reparacion");
        cita.setDescripcion("Equipo no enciende");
        cita.setEstado("En proceso");
        return cita;
    }

    @Test
    void nombresDe80CaracteresSeAceptanY81SeRechazan() {
        Cliente cliente = clienteBase();
        cliente.setNombres("a".repeat(80));
        assertTrue(Validador.validarCliente(cliente).isEmpty());
        cliente.setNombres("a".repeat(81));
        assertEquals(1, Validador.validarCliente(cliente).size());
    }

    @Test
    void correoMasLargoDe120CaracteresSeRechaza() {
        Cliente cliente = clienteBase();
        cliente.setCorreo("a".repeat(115) + "@x.com");
        assertEquals(1, Validador.validarCliente(cliente).size());
    }

    @Test
    void telefonoMuyCortoSeRechaza() {
        Cliente cliente = clienteBase();
        cliente.setTelefono("123");
        assertEquals(1, Validador.validarCliente(cliente).size());
    }

    @Test
    void telefonoConFormatoInternacionalSeAcepta() {
        Cliente cliente = clienteBase();
        cliente.setTelefono("+505 8888-1234");
        assertTrue(Validador.validarCliente(cliente).isEmpty());
    }

    @Test
    void descripcionDe255SeAceptaY256SeRechaza() {
        CitaTecnica cita = citaBase();
        cita.setDescripcion("d".repeat(255));
        assertTrue(Validador.validarCita(cita).isEmpty());
        cita.setDescripcion("d".repeat(256));
        assertEquals(1, Validador.validarCita(cita).size());
    }

    @Test
    void cadaEstadoPermitidoSeAcepta() {
        for (String estado : CitaTecnica.ESTADOS) {
            CitaTecnica cita = citaBase();
            cita.setEstado(estado);
            assertTrue(Validador.validarCita(cita).isEmpty(), "Debe aceptar: " + estado);
        }
    }

    @Test
    void estadoNuloSeRechaza() {
        CitaTecnica cita = citaBase();
        cita.setEstado(null);
        assertEquals(1, Validador.validarCita(cita).size());
    }

    @Test
    void horasLimiteDelDia() {
        assertEquals(LocalTime.of(0, 0), Validador.parsearHora("00:00"));
        assertEquals(LocalTime.of(23, 59), Validador.parsearHora("23:59"));
        assertNull(Validador.parsearHora("24:00"));
        assertNull(Validador.parsearHora("12:60"));
    }

    @Test
    void correoConEspaciosInternosSeRechaza() {
        assertFalse(Validador.esCorreoValido("ana lopez@correo.com"));
    }
}
