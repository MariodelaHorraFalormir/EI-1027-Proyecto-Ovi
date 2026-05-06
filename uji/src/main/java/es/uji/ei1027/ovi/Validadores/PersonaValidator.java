package es.uji.ei1027.ovi.Validadores;

import es.uji.ei1027.ovi.modelo.Persona.Persona;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PersonaValidator implements Validator {

    // Valores máximos por campo
    private static final int MAX_NOM = 100;
    private static final int MAX_NUM_DNI = 9;
    private static final int MAX_PAIS = 50;
    private static final int MAX_APELLIDOS = 100;
    private static final int MAX_MAIL = 255;
    private static final int MAX_TELEFONO = 9;
    private static final int MAX_CONTRASENA = 100;

    @Override
    public boolean supports(Class<?> cls) {
        return Persona.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Persona persona = (Persona) obj;

        String nombre = persona.getNombre();
        String dni = persona.getDni();
        String apellidos = persona.getApellidos();
        String mail = persona.getMail();
        String telefono = persona.getTelefono();
        String pais = persona.getPais();
        String contrasena = persona.getContrasena();
        int edad = persona.getEdad();

        // Nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "Debes ingresar un nombre");
        } else if (nombre.trim().length() > MAX_NOM) {
            errors.rejectValue("nombre", "longitud", "Longitud máxima superada");
        }

        // Apellidos
        if (apellidos == null || apellidos.trim().isEmpty()) {
            errors.rejectValue("apellidos", "obligatorio", "Debes ingresar tus apellidos");
        } else if (apellidos.trim().length() > MAX_APELLIDOS) {
            errors.rejectValue("apellidos", "longitud", "Longitud máxima superada");
        }

        // Edad / fechaNacimiento
        if (edad < 0) {
            errors.rejectValue("fechaNacimiento", "invalida", "La fecha debe ser pasada");
        } else if (edad > 140) {
            errors.rejectValue("fechaNacimiento", "invalida", "El usuario debe tener una fecha de nacimiento válida");
        }

        // DNI
        if (dni == null || dni.trim().isEmpty()) {
            errors.rejectValue("dni", "obligatorio", "Debes ingresar un DNI");
        } else if (dni.trim().length() > MAX_NUM_DNI) {
            errors.rejectValue("dni", "longitud", "Longitud máxima superada");
        } else if (!validarDni(dni)) {
            errors.rejectValue("dni", "incorrecto", "Introduce un DNI válido");
        }

        // Mail
        if (mail == null || mail.trim().isEmpty()) {
            errors.rejectValue("mail", "obligatorio", "Debes ingresar un mail");
        } else if (mail.trim().length() > MAX_MAIL) {
            errors.rejectValue("mail", "longitud", "Longitud máxima superada");
        } else if (!validarMail(mail)) {
            errors.rejectValue("mail", "incorrecto", "Debes ingresar un mail válido");
        }

        // Teléfono
        if (telefono == null || telefono.trim().isEmpty()) {
            errors.rejectValue("telefono", "obligatorio", "Debes ingresar un teléfono");
        } else if (telefono.trim().length() != MAX_TELEFONO) {
            errors.rejectValue("telefono", "incorrecto", "Introduce un teléfono válido de 9 dígitos");
        } else if (!telefono.matches("\\d{9}")) {
            errors.rejectValue("telefono", "incorrecto", "El teléfono solo debe contener números");
        }

        // Contraseña
        if (contrasena == null || contrasena.trim().isEmpty()) {
            errors.rejectValue("contrasena", "obligatorio", "Debes ingresar una contraseña");
        } else if (contrasena.length() > MAX_CONTRASENA) {
            errors.rejectValue("contrasena", "longitud", "Longitud máxima superada");
        } else if (contrasena.length() < 6) {
            errors.rejectValue("contrasena", "corta", "La contraseña debe tener al menos 6 caracteres");
        }

        // País
        if (pais == null || pais.trim().isEmpty()) {
            errors.rejectValue("pais", "obligatorio", "Debes ingresar un país");
        } else if (pais.trim().length() > MAX_PAIS) {
            errors.rejectValue("pais", "longitud", "Longitud máxima superada");
        }
    }

    private boolean validarMail(String mail) {
        if (mail == null) {
            return false;
        }

        mail = mail.trim();

        // Validación simple pero mejor que solo contains("@")
        return mail.contains("@");
    }

    private boolean validarDni(String dni) {
        if (dni == null) {
            return false;
        }

        dni = dni.trim().toUpperCase();

        // Formato correcto: 8 números y 1 letra
        if (!dni.matches("\\d{8}[A-Z]")) {
            return false;
        }

        String numeros = dni.substring(0, 8);
        char letraIntroducida = dni.charAt(8);

        final String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

        try {
            int numeroDni = Integer.parseInt(numeros);
            char letraCorrecta = letras.charAt(numeroDni % 23);
            return letraIntroducida == letraCorrecta;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}