package es.uji.ei1027.ovi.modelo.Persona;

public class Personalidad {
    private int idPersona;
    private int ritmo;
    private int comunicacion;
    private int expresividad;
    private int caracter;
    private int naturaleza;

    public Personalidad() {}

    public Personalidad(int idPersona, int ritmo, int comunicacion,
                        int expresividad, int caracter, int naturaleza) {
        this.idPersona = idPersona;
        this.ritmo = ritmo;
        this.comunicacion = comunicacion;
        this.expresividad = expresividad;
        this.caracter = caracter;
        this.naturaleza = naturaleza;
    }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public int getRitmo() { return ritmo; }
    public void setRitmo(int ritmo) { this.ritmo = ritmo; }

    public int getComunicacion() { return comunicacion; }
    public void setComunicacion(int comunicacion) { this.comunicacion = comunicacion; }

    public int getExpresividad() { return expresividad; }
    public void setExpresividad(int expresividad) { this.expresividad = expresividad; }

    public int getCaracter() { return caracter; }
    public void setCaracter(int caracter) { this.caracter = caracter; }

    public int getNaturaleza() { return naturaleza; }
    public void setNaturaleza(int naturaleza) { this.naturaleza = naturaleza; }

    public String toTexto() {
        return "Ritmo:" + ritmo +
                ",Comunicacion:" + comunicacion +
                ",Expresividad:" + expresividad +
                ",Caracter:" + caracter +
                ",Naturaleza:" + naturaleza;
    }
}