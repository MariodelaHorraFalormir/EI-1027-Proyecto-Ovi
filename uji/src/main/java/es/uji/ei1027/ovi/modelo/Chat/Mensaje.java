package es.uji.ei1027.ovi.modelo.Chat;

import java.time.LocalDateTime;

public class Mensaje {
    private int id;
    private int idSolicitud;
    private int idEmisor;
    private int idReceptor;
    private String contenido;
    private LocalDateTime fechaEnvio;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public int getIdEmisor() { return idEmisor; }
    public void setIdEmisor(int idEmisor) { this.idEmisor = idEmisor; }

    public int getIdReceptor() { return idReceptor; }
    public void setIdReceptor(int idReceptor) { this.idReceptor = idReceptor; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}