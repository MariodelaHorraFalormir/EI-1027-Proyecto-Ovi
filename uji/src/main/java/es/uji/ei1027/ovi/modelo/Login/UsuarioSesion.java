package es.uji.ei1027.ovi.modelo.Login;

import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Roles.RolUsuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSesion {

    private int idPersona;
    private String mail;
    private String nombre;

    // Roles activos = permisos reales
    private List<RolUsuario> rolesActivos = new ArrayList<>();

    // Roles existentes = existen en tabla, aunque estén Pendiente/Rechazado
    private List<RolUsuario> rolesExistentes = new ArrayList<>();

    private EstadoRol estadoOviUser;
    private EstadoRol estadoPapPati;

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
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

    public List<RolUsuario> getRolesActivos() {
        return rolesActivos;
    }

    public void setRolesActivos(List<RolUsuario> rolesActivos) {
        this.rolesActivos = rolesActivos;
    }

    public List<RolUsuario> getRolesExistentes() {
        return rolesExistentes;
    }

    public void setRolesExistentes(List<RolUsuario> rolesExistentes) {
        this.rolesExistentes = rolesExistentes;
    }

    public EstadoRol getEstadoOviUser() {
        return estadoOviUser;
    }

    public void setEstadoOviUser(EstadoRol estadoOviUser) {
        this.estadoOviUser = estadoOviUser;
    }

    public EstadoRol getEstadoPapPati() {
        return estadoPapPati;
    }

    public void setEstadoPapPati(EstadoRol estadoPapPati) {
        this.estadoPapPati = estadoPapPati;
    }

    // =========================
    // ROLES ACTIVOS / PERMISOS
    // =========================

    public boolean tieneRol(RolUsuario rol) {
        return rolesActivos != null && rolesActivos.contains(rol);
    }

    public boolean tieneRolActivo(RolUsuario rol) {
        return tieneRol(rol);
    }

    public boolean esAdminOvi() {
        return tieneRolActivo(RolUsuario.Admin_ovi);
    }

    public boolean esOviUser() {
        return tieneRolActivo(RolUsuario.Ovi_user);
    }

    public boolean esPapPati() {
        return tieneRolActivo(RolUsuario.Pap_pati);
    }

    // =========================
    // ROLES EXISTENTES
    // =========================

    public boolean tieneRolExistente(RolUsuario rol) {
        return rolesExistentes != null && rolesExistentes.contains(rol);
    }

    public boolean tieneOviUserExistente() {
        return tieneRolExistente(RolUsuario.Ovi_user);
    }

    public boolean tienePapPatiExistente() {
        return tieneRolExistente(RolUsuario.Pap_pati);
    }

    public boolean tieneAdminOviExistente() {
        return tieneRolExistente(RolUsuario.Admin_ovi);
    }

    // =========================
    // ESTADOS OVI USER
    // =========================

    public boolean tieneOviUserActivo() {
        return estadoOviUser == EstadoRol.Activo;
    }

    public boolean tieneOviUserPendiente() {
        return estadoOviUser == EstadoRol.Pendiente;
    }

    public boolean tieneOviUserRechazado() {
        return estadoOviUser == EstadoRol.Rechazado;
    }

    // =========================
    // ESTADOS PAP PATI
    // =========================

    public boolean tienePapPatiActivo() {
        return estadoPapPati == EstadoRol.Activo;
    }

    public boolean tienePapPatiPendiente() {
        return estadoPapPati == EstadoRol.Pendiente;
    }

    public boolean tienePapPatiRechazado() {
        return estadoPapPati == EstadoRol.Rechazado;
    }
}