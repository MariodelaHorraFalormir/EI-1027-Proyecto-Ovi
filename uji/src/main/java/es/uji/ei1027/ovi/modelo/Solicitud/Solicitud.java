package es.uji.ei1027.ovi.modelo.Solicitud;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Solicitud {
    private int idSolicitud;
    private int personaSolicitante;
    private CategoriaSolicitud categoriaSolicitud;
    private  TipoSolicitud tipoSolicitud;
    private  EstadoSolicitud estadoSolicitud;
    private Integer tecnicoRevisor;
    private String mensajeSolicitud;
    private String motivoResolucion;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaResolucion;
    private String mensajeRevision;
    private LocalDateTime fechaUltimaRevision;
    private Integer numeroRevisiones;

    public CategoriaSolicitud getCategoriaSolicitud() {
        return categoriaSolicitud;
    }

    public void setCategoriaSolicitud(CategoriaSolicitud categoriaSolicitud) {
        this.categoriaSolicitud = categoriaSolicitud;
    }

    public LocalDate getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDate fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getMotivoResolucion() {
        return motivoResolucion;
    }

    public void setMotivoResolucion(String motivoResolucion) {
        this.motivoResolucion = motivoResolucion;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public int getPersonaSolicitante() {
        return personaSolicitante;
    }

    public void setPersonaSolicitante(int personaSolicitante) {
        this.personaSolicitante = personaSolicitante;
    }

    public Integer getTecnicoRevisor() {
        return tecnicoRevisor;
    }

    public void setTecnicoRevisor(Integer tecnicoRevisor) {
        this.tecnicoRevisor = tecnicoRevisor;
    }

    public String getMensajeSolicitud() {
        return mensajeSolicitud;
    }

    public void setMensajeSolicitud(String mensajeSolicitud) {
        this.mensajeSolicitud = mensajeSolicitud;
    }

    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }
    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }
    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }
    public EstadoSolicitud getEstadoSolicitud() {
        return estadoSolicitud;
    }
    public String getMensajeRevision() {
        return mensajeRevision;
    }

    public void setMensajeRevision(String mensajeRevision) {
        this.mensajeRevision = mensajeRevision;
    }

    public LocalDateTime getFechaUltimaRevision() {
        return fechaUltimaRevision;
    }

    public void setFechaUltimaRevision(LocalDateTime fechaUltimaRevision) {
        this.fechaUltimaRevision = fechaUltimaRevision;
    }

    public Integer getNumeroRevisiones() {
        return numeroRevisiones;
    }

    public void setNumeroRevisiones(Integer numeroRevisiones) {
        this.numeroRevisiones = numeroRevisiones;
    }
}
