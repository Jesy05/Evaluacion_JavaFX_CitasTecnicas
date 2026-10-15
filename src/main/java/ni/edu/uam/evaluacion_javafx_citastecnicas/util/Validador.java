package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import lombok.NoArgsConstructor;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
public final class Validador {
    private static final Pattern CORREO = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9+\\-\\s()]{7,20}$");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("H:mm").withResolverStyle(ResolverStyle.STRICT);

    public static List<String> validarCliente(Cliente cliente) {
        List<String> problemas = new ArrayList<>();
        exigirTexto(problemas, cliente.getNombres(), "Nombres", 80);
        exigirTexto(problemas, cliente.getApellidos(), "Apellidos", 80);

        if (esVacio(cliente.getTelefono())) {
            problemas.add("El campo Teléfono es obligatorio.");
        } else if (!TELEFONO.matcher(cliente.getTelefono().trim()).matches()) {
            problemas.add("El teléfono debe tener entre 7 y 20 caracteres (números, +, -, espacios o paréntesis).");
        }

        if (esVacio(cliente.getCorreo())) {
            problemas.add("El campo Correo es obligatorio.");
        } else if (cliente.getCorreo().trim().length() > 120) {
            problemas.add("El correo no puede superar 120 caracteres.");
        } else if (!esCorreoValido(cliente.getCorreo())) {
            problemas.add("El correo no tiene un formato válido (ejemplo: nombre@dominio.com).");
        }
        return problemas;
    }

    public static List<String> validarCita(CitaTecnica cita) {
        List<String> problemas = new ArrayList<>();
        if (cita.getClienteId() <= 0) {
            problemas.add("Debe seleccionar un cliente.");
        }
        if (cita.getFecha() == null) {
            problemas.add("La fecha es obligatoria.");
        }
        if (cita.getHora() == null) {
            problemas.add("La hora es obligatoria (formato HH:mm, por ejemplo 14:30).");
        }
        exigirTexto(problemas, cita.getTipoServicio(), "Tipo de servicio", 80);
        exigirTexto(problemas, cita.getDescripcion(), "Descripción", 255);
        if (esVacio(cita.getEstado()) || !CitaTecnica.ESTADOS.contains(cita.getEstado())) {
            problemas.add("El estado debe ser uno de: " + String.join(", ", CitaTecnica.ESTADOS) + ".");
        }
        return problemas;
    }

    public static boolean esCorreoValido(String correo) {
        return correo != null && CORREO.matcher(correo.trim()).matches();
    }

    public static LocalTime parsearHora(String texto) {
        if (esVacio(texto)) {
            return null;
        }
        try {
            return LocalTime.parse(texto.trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static void exigirTexto(List<String> problemas, String valor, String campo, int maximo) {
        if (esVacio(valor)) {
            problemas.add("El campo " + campo + " es obligatorio.");
        } else if (valor.trim().length() > maximo) {
            problemas.add("El campo " + campo + " no puede superar " + maximo + " caracteres.");
        }
    }

    private static boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }





}
