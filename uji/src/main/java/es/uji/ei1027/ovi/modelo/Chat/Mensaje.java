package es.uji.ei1027.ovi.modelo.Chat;

import java.time.LocalDateTime;

public class Mensaje {

    private int id;
    private Integer idSolicitud;
    private int idEmisor;
    private Integer idReceptor;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Integer conversacion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public int getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(int idEmisor) {
        this.idEmisor = idEmisor;
    }

    public Integer getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(Integer idReceptor) {
        this.idReceptor = idReceptor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Integer getConversacion() {
        return conversacion;
    }

    public void setConversacion(Integer conversacion) {
        this.conversacion = conversacion;
    }
}