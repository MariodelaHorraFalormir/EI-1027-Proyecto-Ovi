package es.uji.ei1027.ovi.Validadores;

import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PersonaValidator implements Validator {

    public enum ModoValidacion {
        REGISTRO,
        UPDATE
    }

    private PersonaDao personaDao;
    private ModoValidacion modo;

    // Constructor vacío
    public PersonaValidator() {
        this.modo = ModoValidacion.REGISTRO;
    }

    // Constructor antiguo: por defecto registro
    public PersonaValidator(PersonaDao personaDao) {
        this.personaDao = personaDao;
        this.modo = ModoValidacion.REGISTRO;
    }

    // Constructor nuevo: permite elegir REGISTRO o UPDATE
    public PersonaValidator(PersonaDao personaDao, ModoValidacion modo) {
        this.personaDao = personaDao;
        this.modo = modo;
    }

    private static final int MAX_NOMBRE = 100;
    private static final int MAX_DNI = 9;
    private static final int MAX_PAIS = 50;
    private static final int MAX_APELLIDOS = 100;
    private static final int MAX_MAIL = 255;
    private static final int MAX_TELEFONO = 9;
    private static final int MAX_CONTRASENA = 100;
    private static final int MAX_DIRECCION = 255;

    @Override
    public boolean supports(Class<?> cls) {
        return Persona.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Persona persona = (Persona) obj;

        validarNombre(persona, errors);
        validarApellidos(persona, errors);
        validarFechaNacimiento(persona, errors);
        validarDni(persona, errors);
        validarMail(persona, errors);
        validarTelefono(persona, errors);
        validarDireccion(persona, errors);
        validarPais(persona, errors);
        validarGenero(persona, errors);
        validarPersonalidad(persona, errors);

        if (modo == ModoValidacion.REGISTRO) {
            validarContrasenaRegistro(persona, errors);
        }
    }

    private void validarNombre(Persona persona, Errors errors) {
        String nombre = persona.getNombre();

        if (nombre == null || nombre.trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "Debes ingresar un nombre");
        } else if (nombre.trim().length() > MAX_NOMBRE) {
            errors.rejectValue("nombre", "longitud", "Longitud máxima superada");
        }
    }

    private void validarApellidos(Persona persona, Errors errors) {
        String apellidos = persona.getApellidos();

        if (apellidos == null || apellidos.trim().isEmpty()) {
            errors.rejectValue("apellidos", "obligatorio", "Debes ingresar tus apellidos");
        } else if (apellidos.trim().length() > MAX_APELLIDOS) {
            errors.rejectValue("apellidos", "longitud", "Longitud máxima superada");
        }
    }

    private void validarFechaNacimiento(Persona persona, Errors errors) {
        if (persona.getFechaNacimiento() == null) {
            errors.rejectValue("fechaNacimiento", "obligatorio", "Debes ingresar una fecha de nacimiento");
            return;
        }

        int edad = persona.getEdad();

        if (edad < 0) {
            errors.rejectValue("fechaNacimiento", "invalida", "La fecha debe ser pasada");
        } else if (edad > 140) {
            errors.rejectValue("fechaNacimiento", "invalida", "El usuario debe tener una fecha de nacimiento válida");
        }
    }

    private void validarDni(Persona persona, Errors errors) {
        String dni = persona.getDni();

        if (dni == null || dni.trim().isEmpty()) {
            errors.rejectValue("dni", "obligatorio", "Debes ingresar un DNI");
            return;
        }

        dni = dni.trim().toUpperCase();

        if (dni.length() > MAX_DNI) {
            errors.rejectValue("dni", "longitud", "Longitud máxima superada");
            return;
        }

        if (!validarDniFormato(dni)) {
            errors.rejectValue("dni", "incorrecto", "Introduce un DNI válido");
            return;
        }

        if (personaDao == null) {
            return;
        }

        if (modo == ModoValidacion.REGISTRO) {
            if (personaDao.existeDni(dni)) {
                errors.rejectValue("dni", "duplicado", "Este DNI ya está registrado en el sistema");
            }
        } else if (modo == ModoValidacion.UPDATE) {
            int idPersona = persona.getIdPersona();

            if (personaDao.existeDniEnOtraPersona(dni, idPersona)) {
                errors.rejectValue("dni", "duplicado", "Este DNI ya está registrado en otra persona");
            }
        }
    }

    private void validarMail(Persona persona, Errors errors) {
        String mail = persona.getMail();

        if (mail == null || mail.trim().isEmpty()) {
            errors.rejectValue("mail", "obligatorio", "Debes ingresar un mail");
            return;
        }

        mail = mail.trim();

        if (mail.length() > MAX_MAIL) {
            errors.rejectValue("mail", "longitud", "Longitud máxima superada");
            return;
        }

        if (!validarMailFormato(mail)) {
            errors.rejectValue("mail", "incorrecto", "Debes ingresar un mail válido");
            return;
        }

        if (personaDao == null) {
            return;
        }

        if (modo == ModoValidacion.REGISTRO) {
            if (personaDao.existeMail(mail)) {
                errors.rejectValue("mail", "duplicado", "Este mail ya está registrado en el sistema");
            }
        } else if (modo == ModoValidacion.UPDATE) {
            int idPersona = persona.getIdPersona();

            if (personaDao.existeMailEnOtraPersona(mail, idPersona)) {
                errors.rejectValue("mail", "duplicado", "Este mail ya está registrado en otra persona");
            }
        }
    }

    private void validarTelefono(Persona persona, Errors errors) {
        String telefono = persona.getTelefono();

        if (telefono == null || telefono.trim().isEmpty()) {
            errors.rejectValue("telefono", "obligatorio", "Debes ingresar un teléfono");
        } else if (telefono.trim().length() != MAX_TELEFONO) {
            errors.rejectValue("telefono", "incorrecto", "Introduce un teléfono válido de 9 dígitos");
        } else if (!telefono.matches("\\d{9}")) {
            errors.rejectValue("telefono", "incorrecto", "El teléfono solo debe contener números");
        }
    }

    private void validarDireccion(Persona persona, Errors errors) {
        String direccion = persona.getDireccion();

        if (direccion == null || direccion.trim().isEmpty()) {
            errors.rejectValue("direccion", "obligatorio", "Debes ingresar una dirección");
        } else if (direccion.trim().length() > MAX_DIRECCION) {
            errors.rejectValue("direccion", "longitud", "Longitud máxima superada");
        }
    }

    private void validarPais(Persona persona, Errors errors) {
        String pais = persona.getPais();

        if (pais == null || pais.trim().isEmpty()) {
            errors.rejectValue("pais", "obligatorio", "Debes ingresar un país");
        } else if (pais.trim().length() > MAX_PAIS) {
            errors.rejectValue("pais", "longitud", "Longitud máxima superada");
        }
    }

    private void validarGenero(Persona persona, Errors errors) {
        if (persona.getGenero() == null) {
            errors.rejectValue("genero", "obligatorio", "Debes seleccionar un género");
        }
    }

    private void validarPersonalidad(Persona persona, Errors errors) {
        if (persona.getPersonalidad() == null) {
            errors.rejectValue("personalidad", "obligatorio", "Debes rellenar los datos de personalidad");
            return;
        }

        validarRangoPersonalidad(persona.getPersonalidad().getRitmo(), "personalidad.ritmo", errors);
        validarRangoPersonalidad(persona.getPersonalidad().getComunicacion(), "personalidad.comunicacion", errors);
        validarRangoPersonalidad(persona.getPersonalidad().getExpresividad(), "personalidad.expresividad", errors);
        validarRangoPersonalidad(persona.getPersonalidad().getCaracter(), "personalidad.caracter", errors);
        validarRangoPersonalidad(persona.getPersonalidad().getNaturaleza(), "personalidad.naturaleza", errors);
    }

    private void validarRangoPersonalidad(int valor, String campo, Errors errors) {
        if (valor < 1 || valor > 10) {
            errors.rejectValue(campo, "incorrecto", "El valor debe estar entre 1 y 10");
        }
    }

    private void validarContrasenaRegistro(Persona persona, Errors errors) {
        String contrasena = persona.getContrasena();

        if (contrasena == null || contrasena.trim().isEmpty()) {
            errors.rejectValue("contrasena", "obligatorio", "Debes ingresar una contraseña");
        } else if (contrasena.length() > MAX_CONTRASENA) {
            errors.rejectValue("contrasena", "longitud", "Longitud máxima superada");
        } else if (contrasena.length() < 6) {
            errors.rejectValue("contrasena", "corta", "La contraseña debe tener al menos 6 caracteres");
        }
    }

    private boolean validarMailFormato(String mail) {
        if (mail == null) {
            return false;
        }

        return mail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean validarDniFormato(String dni) {
        if (dni == null) {
            return false;
        }

        dni = dni.trim().toUpperCase();

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