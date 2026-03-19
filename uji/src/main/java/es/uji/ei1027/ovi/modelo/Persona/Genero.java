package es.uji.ei1027.ovi.modelo.Persona;

import es.uji.ei1027.ovi.modelo.Persona.Roles.Disponibilidad;

public enum Genero {
    Hombre,
    Mujer,
    Otro;
    public static Genero fromString(String valor) {
        if (valor == null) {
            return null;
        }

        switch (valor) {
            case "Hombre":
                return Hombre;
            case "Mujer":
                return Mujer;
            case "Otro":
                return Otro;
            default:
                throw new IllegalArgumentException("Valor de disponibilidad no válido: " + valor);
        }

    }
    public String getTexto() {
        switch (this) {
            case Mujer:
                return "Mujer";
            case Hombre:
                return "Hombre";
            case Otro:
                return "Otro";
            default:
                throw new IllegalStateException("Valor no esperado: " + this);
        }
    }
}
