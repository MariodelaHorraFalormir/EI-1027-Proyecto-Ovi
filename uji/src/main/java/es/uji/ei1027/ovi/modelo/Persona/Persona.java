package es.uji.ei1027.ovi.modelo.Persona;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class Persona {
    private int idPersona;
    private String nombre;
    private String apellidos;
    private String mail;
    private String telefono;
    private  String direccion;
    private Genero genero;
    private String pais;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaAlta;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaBaja;

    public Persona() {}
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {this.apellidos = apellidos;}

    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {this.pais = pais;}

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return java.time.Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }



    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }




}