package es.uji.ei1027.ovi.modelo.Solicitud;

import java.util.Arrays;
import java.util.List;

public enum EstadoSolicitud {
    Pendiente,
    Aprobada,
    Rechazada;
    public static EstadoSolicitud fromString(String valor) {
        if (valor == null) {
            return null;
        }

        switch (valor) {
            case "Pendiente":
                return Pendiente;
            case "Aprobada":
                return Aprobada;
            case "Rechazada":
                return Rechazada;
            default:
                throw new IllegalArgumentException("Valor de EstadoSolicitud no válido: " + valor);
        }

    }
    public String getTexto() {
        switch (this) {
            case Pendiente:
                return "Pendiente";
            case Aprobada:
                return "Aprobada";
            case Rechazada:
                return "Rechazada";
            default:
                throw new IllegalStateException("Valor no esperado: " + this);
        }
    }
    public List<String> getLista() {
        return List.of("Pendiente", "Aprobada", "Rechazada");
    }

}
