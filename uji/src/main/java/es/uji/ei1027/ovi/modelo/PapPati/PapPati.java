package es.uji.ei1027.ovi.modelo.PapPati;

import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PapPati {
    private Disponibilidad disponibilidad;
    private int experiencia;
    private Boolean carnetConducir;
    private Boolean vehiculoPropio;
    private String urlCV;
    private String centroSocial;
    private String  descripcionPerfil;
    private EstadoRol estadoRol;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaInicioDisponibilidad;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaFinDisponibilidad;
    private List<Especialidad> especialidades;
    private int idPatPati;

    public int getIdPatPati() {
        return idPatPati;
    }

    public void setIdPatPati(int idPatPati) {
        this.idPatPati = idPatPati;
    }

    public Boolean getCarnetConducir() {
        return carnetConducir;
    }

    public void setCarnetConducir(Boolean carnetConducir) {
        this.carnetConducir = carnetConducir;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Disponibilidad disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Boolean getVehiculoPropio() {
        return vehiculoPropio;
    }

    public void setVehiculoPropio(Boolean vehiculoPropio) {
        this.vehiculoPropio = vehiculoPropio;
    }

    public String getUrlCV() {
        return urlCV;
    }

    public void setUrlCV(String urlCV) {
        this.urlCV = urlCV;
    }

    public String getDescripcionPerfil() {
        return descripcionPerfil;
    }

    public void setDescripcionPerfil(String descripcionPerfil) {
        this.descripcionPerfil = descripcionPerfil;
    }

    public EstadoRol getEstadoRol() {
        return estadoRol;
    }

    public void setEstadoRol(EstadoRol estadoRol) {
        this.estadoRol = estadoRol;
    }

    public String getCentroSocial() {
        return centroSocial;
    }

    public void setCentroSocial(String centroSocial) {
        this.centroSocial = centroSocial;
    }

    public LocalDate getFechaFinDisponibilidad() {
        return fechaFinDisponibilidad;
    }

    public void setFechaFinDisponibilidad(LocalDate fechaFinDisponibilidad) {
        this.fechaFinDisponibilidad = fechaFinDisponibilidad;
    }

    public LocalDate getFechaInicioDisponibilidad() {
        return fechaInicioDisponibilidad;
    }

    public void setFechaInicioDisponibilidad(LocalDate fechaInicioDisponibilidad) {
        this.fechaInicioDisponibilidad = fechaInicioDisponibilidad;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    public  List<String> getEspecialidadesNombre(){
        List<String> especialidadesNombre = new ArrayList<>();
        for (Especialidad especialidad : especialidades) {
            especialidadesNombre.add( especialidad.getDiversidadFuncional().getTexto());
        }
        return especialidadesNombre;
    }
}
