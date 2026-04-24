package es.uji.ei1027.ovi.modelo;

public class PapPati extends Persona { // Hereda de persona para tener nombre, mail...
    private String disponibilidad;
    private int experiencia_meses;
    private String formacion_principal;
    private String url_cv;

    public int getExperiencia_meses() {
        return experiencia_meses;
    }

    public void setExperiencia_meses(int experiencia_meses) {
        this.experiencia_meses = experiencia_meses;
    }

    public String getFormacion_principal() {
        return formacion_principal;
    }

    public void setFormacion_principal(String formacion_principal) {
        this.formacion_principal = formacion_principal;
    }

    public String getUrl_cv() {
        return url_cv;
    }

    public void setUrl_cv(String url_cv) {
        this.url_cv = url_cv;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}