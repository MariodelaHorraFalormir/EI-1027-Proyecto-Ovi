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

        private int oviUser;

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
    }

