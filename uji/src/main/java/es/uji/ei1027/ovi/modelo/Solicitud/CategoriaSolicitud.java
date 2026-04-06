package es.uji.ei1027.ovi.modelo.Solicitud;

import es.uji.ei1027.ovi.modelo.Persona.Genero;

import java.util.List;

public enum CategoriaSolicitud {
    Rol,
    Proceso;
    public static CategoriaSolicitud fromString(String valor) {
        if (valor == null) {
            return null;
        }

        switch (valor) {
            case "Proceso":
                return Proceso;
            case "Rol":
                return Rol;
            default:
                throw new IllegalArgumentException("Valor de CategoriaDeSolicitud no válido: " + valor);
        }

    }
    public String getTexto() {
        switch (this) {
            case Rol:
                return "Rol";
            case Proceso:
                return "Proceso";
            default:
                throw new IllegalStateException("Valor no esperado: " + this);
        }
    }
    public List<String> getLista() {
        return List.of("Rol", "Proceso");
    }
}


