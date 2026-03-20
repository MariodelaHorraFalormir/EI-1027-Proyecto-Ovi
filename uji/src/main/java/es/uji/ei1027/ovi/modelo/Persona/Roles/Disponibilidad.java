package es.uji.ei1027.ovi.modelo.Persona.Roles;

public enum Disponibilidad {
    Total,
    Parcial,
    No_disponible;

    //llamo al metodo desde la clase para facilitar la conversion
    public static Disponibilidad fromString(String valor) {
        if (valor == null) {
            return null;
        }

        switch (valor) {
            case "Total":
                return Total;
            case "Parcial":
                return Parcial;
            case "No disponible":
            case "No_disponible":
                return No_disponible;
            default:
                throw new IllegalArgumentException("Valor de disponibilidad no válido: " + valor);
        }

    }
    public String getTexto() {
        switch (this) {
            case Total:
                return "Total";
            case Parcial:
                return "Parcial";
            case No_disponible:
                return "No disponible";
            default:
                throw new IllegalStateException("Valor no esperado: " + this);
        }
    }

}
