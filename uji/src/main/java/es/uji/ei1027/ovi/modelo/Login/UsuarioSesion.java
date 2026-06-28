package es.uji.ei1027.ovi.modelo.Login;

import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Roles.RolUsuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSesion {

    private int idPersona;
    private String mail;
    private String nombre;

    private List<RolUsuario> rolesActivos = new ArrayList<>();
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
        if (rolesActivos == null) {
            this.rolesActivos = new ArrayList<>();
        } else {
            this.rolesActivos = new ArrayList<>(rolesActivos);
        }
    }

    public List<RolUsuario> getRolesExistentes() {
        return rolesExistentes;
    }

    public void setRolesExistentes(List<RolUsuario> rolesExistentes) {
        if (rolesExistentes == null) {
            this.rolesExistentes = new ArrayList<>();
        } else {
            this.rolesExistentes = new ArrayList<>(rolesExistentes);
        }
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

    public boolean tieneOviUserActivo() {
        return estadoOviUser == EstadoRol.Activo || esOviUser();
    }

    public boolean tieneOviUserPendiente() {
        return estadoOviUser == EstadoRol.Pendiente;
    }

    public boolean tieneOviUserRechazado() {
        return estadoOviUser == EstadoRol.Rechazado;
    }

    public boolean tienePapPatiActivo() {
        return estadoPapPati == EstadoRol.Activo || esPapPati();
    }

    public boolean tienePapPatiPendiente() {
        return estadoPapPati == EstadoRol.Pendiente;
    }

    public boolean tienePapPatiRechazado() {
        return estadoPapPati == EstadoRol.Rechazado;
    }

    public void activarRol(RolUsuario rol) {
        if (rol == null) {
            return;
        }

        if (!rolesExistentes.contains(rol)) {
            rolesExistentes.add(rol);
        }

        if (!rolesActivos.contains(rol)) {
            rolesActivos.add(rol);
        }

        if (rol == RolUsuario.Ovi_user) {
            estadoOviUser = EstadoRol.Activo;
        }

        if (rol == RolUsuario.Pap_pati) {
            estadoPapPati = EstadoRol.Activo;
        }
    }

    public void quitarRol(RolUsuario rol) {
        if (rol == null) {
            return;
        }

        rolesActivos.remove(rol);
        rolesExistentes.remove(rol);

        if (rol == RolUsuario.Ovi_user) {
            estadoOviUser = null;
        }

        if (rol == RolUsuario.Pap_pati) {
            estadoPapPati = null;
        }
    }

    public void setAdminOvi(boolean adminOvi) {
        if (adminOvi) {
            activarRol(RolUsuario.Admin_ovi);
        } else {
            quitarRol(RolUsuario.Admin_ovi);
        }
    }

    public void setOviUser(boolean oviUser) {
        if (oviUser) {
            activarRol(RolUsuario.Ovi_user);
        } else {
            quitarRol(RolUsuario.Ovi_user);
        }
    }

    public void setPapPati(boolean papPati) {
        if (papPati) {
            activarRol(RolUsuario.Pap_pati);
        } else {
            quitarRol(RolUsuario.Pap_pati);
        }
    }
}