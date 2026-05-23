package es.uji.ei1027.ovi.modelo.PaRequest;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


    public class PaRequest {
        private int id;
        private StatusPaRequest status;

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
        private String personalidad;
        private String generoAsistente;
        private String disponibilidadHoraria;
        private String zonaGeografica;

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

        public String getPersonalidad() { return personalidad; }
        public void setPersonalidad(String personalidad) { this.personalidad = personalidad; }

        public String getGeneroAsistente() { return generoAsistente; }
        public void setGeneroAsistente(String generoAsistente) { this.generoAsistente = generoAsistente; }

        public String getDisponibilidadHoraria() { return disponibilidadHoraria; }
        public void setDisponibilidadHoraria(String disponibilidadHoraria) { this.disponibilidadHoraria = disponibilidadHoraria; }

        public String getZonaGeografica() { return zonaGeografica; }
        public void setZonaGeografica(String zonaGeografica) { this.zonaGeografica = zonaGeografica; }
    }

