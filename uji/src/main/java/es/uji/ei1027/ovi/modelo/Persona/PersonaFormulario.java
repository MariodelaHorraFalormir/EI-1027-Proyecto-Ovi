package es.uji.ei1027.ovi.modelo.Persona;

import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Roles.RolUsuario;
import es.uji.ei1027.ovi.modelo.AdminOvi.AdminOvi;

import java.util.ArrayList;
import java.util.List;

public class PersonaFormulario {
    private PapPati papPati;
    private OviUser oviUser;
    private Persona persona;
    private AdminOvi adminOvi;
    public AdminOvi getAdminOvi() {
        return adminOvi;
    }

    public void setAdminOvi(AdminOvi adminOvi) {
        this.adminOvi = adminOvi;
    }

    public boolean tieneAdminOvi() {
        return adminOvi != null;
    }

    public PapPati getPapPati() {
        return papPati;
    }

    public void setPapPati(PapPati papPati) {
        this.papPati = papPati;
    }

    public OviUser getOviUser() {
        return oviUser;
    }

    public void setOviUser(OviUser oviUser) {
        this.oviUser = oviUser;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
    public int getNumeroRoles() {
        int numeroRoles = 0;

        if (papPati != null) {
            numeroRoles++;
        }

        if (oviUser != null) {
            numeroRoles++;
        }
        if (adminOvi != null) {
            numeroRoles++;
        }

        return numeroRoles;
    }
    public boolean tienePapPati() {
        return papPati != null;
    }

    public boolean tieneOviUser() {
        return oviUser != null;
    }

    public boolean tieneRoles() {
        return getNumeroRoles() > 0;
    }

    public List<RolUsuario> getRolesExistentes() {
        List<RolUsuario> roles = new ArrayList<>();

        if (adminOvi != null) {
            roles.add(RolUsuario.Admin_ovi);
        }

        if (oviUser != null) {
            roles.add(RolUsuario.Ovi_user);
        }

        if (papPati != null) {
            roles.add(RolUsuario.Pap_pati);
        }

        return roles;
    }

    public List<RolUsuario> getRolesActivos() {
        List<RolUsuario> roles = new ArrayList<>();

        if (adminOvi != null) {
            roles.add(RolUsuario.Admin_ovi);
        }

        if (tieneOviUserActivo()) {
            roles.add(RolUsuario.Ovi_user);
        }

        if (tienePapPatiActivo()) {
            roles.add(RolUsuario.Pap_pati);
        }

        return roles;
    }
    public int getNumeroRolesActivos() {
        return getRolesActivos().size();
    }

    public boolean tieneRolesActivos() {
        return getNumeroRolesActivos() > 0;
    }

    public boolean tieneOviUserActivo() {
        return oviUser != null
                && oviUser.getEstado() != null
                && oviUser.getEstado() == EstadoRol.Activo;
    }

    public boolean tieneOviUserPendiente() {
        return oviUser != null
                && oviUser.getEstado() != null
                && oviUser.getEstado() == EstadoRol.Pendiente;
    }

    public boolean tieneOviUserRechazado() {
        return oviUser != null
                && oviUser.getEstado() != null
                && oviUser.getEstado() == EstadoRol.Rechazado;
    }

    public boolean tienePapPatiActivo() {
        return papPati != null
                && papPati.getEstadoRol() != null
                && papPati.getEstadoRol() == EstadoRol.Activo;
    }

    public boolean tienePapPatiPendiente() {
        return papPati != null
                && papPati.getEstadoRol() != null
                && papPati.getEstadoRol() == EstadoRol.Pendiente;
    }

    public boolean tienePapPatiRechazado() {
        return papPati != null
                && papPati.getEstadoRol()!= null
                && papPati.getEstadoRol() == EstadoRol.Rechazado;
    }

}
