package es.uji.ei1027.ovi.modelo;

public class Personalidad {
    private int movimiento;    // Lento (1) - Rápido (8)
    private int habla;         // Reservada (1) - Directa (8)
    private int expresividad;  // Baja (1) - Alta (8)
    private int caracter;      // Serio (1) - Alegre (8)
    private int naturaleza;    // Normal (1) - Peculiar (8)

    public Personalidad() {}

    public int getMovimiento() { return movimiento; }
    public void setMovimiento(int movimiento) { this.movimiento = movimiento; }
    public int getHabla() { return habla; }
    public void setHabla(int habla) { this.habla = habla; }
    public int getExpresividad() { return expresividad; }
    public void setExpresividad(int expresividad) { this.expresividad = expresividad; }
    public int getCaracter() { return caracter; }
    public void setCaracter(int caracter) { this.caracter = caracter; }
    public int getNaturaleza() { return naturaleza; }
    public void setNaturaleza(int naturaleza) { this.naturaleza = naturaleza; }
}