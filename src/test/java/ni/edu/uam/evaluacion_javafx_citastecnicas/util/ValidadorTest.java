package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;


public class ValidadorTest {
    private Cliente clienteValido() {
        return new Cliente(0, "Ana María", "López Pérez", "8888-1234", "ana@correo.com", true);
    }

    private CitaTecnica citaValida() {
        CitaTecnica cita = new CitaTecnica();
        cita.setClienteId(1);
        cita.setFecha(LocalDate.of(2026, 10, 15));
        cita.setHora(LocalTime.of(14, 30));
        cita.setTipoServicio("Mantenimiento");
        cita.setDescripcion("Limpieza de laptop");
        cita.setEstado("Pendiente");
        return cita;
    }

    @Test
    void clienteValidoNoTieneProblemas() {
        assertTrue(Validador.validarCliente(clienteValido()).isEmpty());
    }

    @Test
    void clienteConCamposVaciosReportaProblemas() {
        Cliente cliente = new Cliente(0, " ", "", null, "", true);
        assertEquals(4, Validador.validarCliente(cliente).size());
    }

    @Test
    void correoConFormatoInvalidoSeRechaza() {
        assertFalse(Validador.esCorreoValido("sin-arroba.com"));
        assertFalse(Validador.esCorreoValido("a@b"));
        assertFalse(Validador.esCorreoValido(null));
        assertTrue(Validador.esCorreoValido("tecnico@tecnoasistencia.com.ni"));
    }

    @Test
    void telefonoConLetrasSeRechaza() {
        Cliente cliente = clienteValido();
        cliente.setTelefono("abc12345");
        assertEquals(1, Validador.validarCliente(cliente).size());
    }

    @Test
    void citaValidaNoTieneProblemas() {
        assertTrue(Validador.validarCita(citaValida()).isEmpty());
    }

    @Test
    void citaSinClienteFechaNiHoraReportaTresProblemas() {
        CitaTecnica cita = citaValida();
        cita.setClienteId(0);
        cita.setFecha(null);
        cita.setHora(null);
        assertEquals(3, Validador.validarCita(cita).size());
    }

    @Test
    void estadoFueraDeLasOpcionesSeRechaza() {
        CitaTecnica cita = citaValida();
        cita.setEstado("Archivada");
        assertEquals(1, Validador.validarCita(cita).size());
    }

    @Test
    void parsearHoraAceptaFormatoHHmm() {
        assertEquals(LocalTime.of(14, 30), Validador.parsearHora("14:30"));
        assertEquals(LocalTime.of(9, 5), Validador.parsearHora(" 9:05 "));
    }

    @Test
    void parsearHoraDevuelveNullSiNoEsValida() {
        assertNull(Validador.parsearHora("25:00"));
        assertNull(Validador.parsearHora("abc"));
        assertNull(Validador.parsearHora(""));
    }


}
