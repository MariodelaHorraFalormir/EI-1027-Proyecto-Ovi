package es.uji.ei1027.ovi.modelo.Contrato;

import java.time.LocalDate;

public class Contrato {
    private int id;
    private int idSolicitud;
    private int idUsuarioOvi;
    private int idPapPati;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado; // "Activo", "Finalizado", "Cancelado"

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public int getIdUsuarioOvi() { return idUsuarioOvi; }
    public void setIdUsuarioOvi(int idUsuarioOvi) { this.idUsuarioOvi = idUsuarioOvi; }

    public int getIdPapPati() { return idPapPati; }
    public void setIdPapPati(int idPapPati) { this.idPapPati = idPapPati; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}