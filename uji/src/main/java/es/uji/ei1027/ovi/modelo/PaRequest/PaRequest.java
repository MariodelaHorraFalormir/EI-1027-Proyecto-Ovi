package es.uji.ei1027.ovi.modelo.PaRequest;

import es.uji.ei1027.ovi.modelo.Persona.Genero;
import es.uji.ei1027.ovi.modelo.Personalidad;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


    public class PaRequest {
        private int id;
        private StatusPaRequest status;

        private Genero generoPreferido;
        private int experienciaMinima;
        private Personalidad personalidadDeseada; // La clase que acabas de crear
        private Integer idPapPatiAsignado;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate fechaCreacion;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate fechaResolucion;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate fechaInicio;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate fechaFin;

        private String tipoAsistencia;
        private String preferencias;

        private int oviUser;

        public LocalDate getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

        public LocalDate getFechaFin() { return fechaFin; }
        public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

        public String getTipoAsistencia() { return tipoAsistencia; }
        public void setTipoAsistencia(String tipoAsistencia) { this.tipoAsistencia = tipoAsistencia; }

        public String getPreferencias() { return preferencias; }
        public void setPreferencias(String preferencias) { this.preferencias = preferencias; }

        public PaRequest() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public StatusPaRequest getStatus() {
            return status;
        }

        public void setStatus(StatusPaRequest status) {
            this.status = status;
        }


        public LocalDate getFechaCreacion() {
            return fechaCreacion;
        }

        public void setFechaCreacion(LocalDate fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
        }


        public LocalDate getFechaResolucion() {
            return fechaResolucion;
        }

        public void setFechaResolucion(LocalDate fechaResolucion) {
            this.fechaResolucion = fechaResolucion;
        }


        public int getOviUser() {
            return oviUser;
        }

        public void setOviUser(int oviUser) {
            this.oviUser = oviUser;
        }

        public Genero getGeneroPreferido() {
            return generoPreferido;
        }

        public void setGeneroPreferido(Genero generoPreferido) {
            this.generoPreferido = generoPreferido;
        }

        public int getExperienciaMinima() {
            return experienciaMinima;
        }

        public void setExperienciaMinima(int experienciaMinima) {
            this.experienciaMinima = experienciaMinima;
        }

        public Personalidad getPersonalidadDeseada() {
            return personalidadDeseada;
        }

        public void setPersonalidadDeseada(Personalidad personalidadDeseada) {
            this.personalidadDeseada = personalidadDeseada;
        }

        public Integer getIdPapPatiAsignado() {
            return idPapPatiAsignado;
        }

        public void setIdPapPatiAsignado(Integer idPapPatiAsignado) {
            this.idPapPatiAsignado = idPapPatiAsignado;
        }
    }

