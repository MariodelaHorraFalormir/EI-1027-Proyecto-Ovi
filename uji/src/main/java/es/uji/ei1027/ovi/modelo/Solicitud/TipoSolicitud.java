package es.uji.ei1027.ovi.modelo.Solicitud;

import java.util.ArrayList;
import java.util.List;

public enum TipoSolicitud {
    Pap_pati,
    Ovi_user,
    Pa_request,
    Asistencia_tecnica,
    Otro;
    public static TipoSolicitud fromString(String valor) {
        if (valor == null) {
           throw new IllegalArgumentException("No ha llegado el tipo de solicitud o tipo de solicitud vacia");
        }

        switch (valor) {
            case "Pap_pati":
                return Pap_pati;
            case "Ovi_user":
                return Ovi_user;
            case "Pa_request":
                return Pa_request;
            case "Asistencia_tecnica":
                return Asistencia_tecnica;
            case "Otro":
                return Otro;
            default:
                throw new IllegalArgumentException("Valor de CategoriaDeSolicitud no válido: " + valor);
        }

    }
    public String getTexto() {
        switch (this) {
            case Pap_pati:
                return "Pap_pati";
            case Ovi_user:
                return "Ovi_user";
            case Pa_request:
                return "Pa_request";
            case Asistencia_tecnica:
                return "Asistencia_tecnica";
            case Otro:
                return "Otro";
            default:
                throw new IllegalStateException("Valor no esperado: " + this);
        }
    }
    public List<String> getLista() {
        return List.of("Pa_request", "Asistencia_tecnica","Otro","Pap_pati","Ovi_user");

    }
}
