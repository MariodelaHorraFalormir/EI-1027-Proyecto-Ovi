package es.uji.ei1027.ovi.modelo.Persona.Roles;

public class PatPati {
    public int getIdPatPati() {
        return idPatPati;
    }

    public void setIdPatPati(int idPatPati) {
        this.idPatPati = idPatPati;
    }

    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Disponibilidad disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    private int idPatPati;
    private Disponibilidad disponibilidad;
}
