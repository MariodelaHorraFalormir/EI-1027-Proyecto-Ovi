package es.uji.ei1027.ovi.modelo.Roles;

import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;

import java.util.List;

public enum EstadoRol {
    Pendiente,Activo,Rechazado;

    public static EstadoRol fromString(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("No ha llegado el tipo de solicitud o tipo de solicitud vacia");
        }

        switch (valor) {
            case "Pendiente":
                return Pendiente;
            case "Activo":
                return Activo;
            case "Rechazado":
                return Rechazado;
            default:
                throw new IllegalArgumentException("Valor de CategoriaDeSolicitud no válido: " + valor);
        }

    }
    public String getTexto() {
        switch (this) {
            case Pendiente:
                return "Pendiente";
            case Activo:
                return "Aprobada";
            case Rechazado:
                return "Rechazada";
            default:
                throw new IllegalStateException("Valor no esperado: " + this);
        }
    }
    public List<String> getLista() {
        return List.of("Pendiente","Activo","Rechazado");

    }
}

