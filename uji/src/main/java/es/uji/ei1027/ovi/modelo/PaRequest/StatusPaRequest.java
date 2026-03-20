package es.uji.ei1027.ovi.modelo.PaRequest;


    public enum StatusPaRequest {
        En_espera,
        En_activo,
        Caducada,
        Finalizada;

        public static StatusPaRequest fromString(String valor) {
            if (valor == null) {
                return null;
            }

            switch (valor) {
                case "En espera":
                    return En_espera;
                case "En activo":
                    return En_activo;
                case "Caducada":
                    return Caducada;
                case "Finalizada":
                    return Finalizada;
                default:
                    throw new IllegalArgumentException("Estado no válido: " + valor);
            }
        }

        public String getTexto() {
            switch (this) {
                case En_espera:
                    return "En espera";
                case En_activo:
                    return "En activo";
                case Caducada:
                    return "Caducada";
                case Finalizada:
                    return "Finalizada";
                default:
                    throw new IllegalStateException("Valor no esperado: " + this);
            }
        }
    }

