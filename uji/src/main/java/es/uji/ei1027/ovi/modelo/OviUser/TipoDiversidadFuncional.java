package es.uji.ei1027.ovi.modelo.OviUser;

import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;

import java.util.List;

public enum TipoDiversidadFuncional {
    Visual,
    Auditiva,
    Motora,
    Cognitiva,
    Psicosocial ,
    Organica,
    Otro;
    public static TipoDiversidadFuncional fromString(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("No ha llegado el tipo de solicitud o tipo de solicitud vacia");
        }

        switch (valor) {
            case "Visual":
                return Visual;
            case "Auditiva":
                return Auditiva;
            case "Motora":
                return Motora;
            case "Cognitiva":
                return Cognitiva;
            case "Psicosocial":
                return Psicosocial;
            case "Organica":
                return Organica;
            case "Otro":
                return Otro;
            default:
                throw new IllegalArgumentException("Valor de CategoriaDeSolicitud no válido: " + valor);
        }

    }
    public String getTexto() {

        switch (this) {
            case Visual:
                return "Visual";
            case Auditiva:
                return "Auditiva";
            case Motora:
                return "Motora";
            case Cognitiva:
                return "Cognitiva";
            case Psicosocial:
                return "Psicosocial";
            case Organica:
                return "Organica";
            case Otro:
                return "Otro";
            default:
                throw new IllegalArgumentException("Valor de CategoriaDeSolicitud no válido: " + this);
        }
    }
    public static List<String> getLista() {
      return List.of("Visual","Auditiva","Motora","Cognitiva","Psicosocial","Organica","Otro");

    }
}
