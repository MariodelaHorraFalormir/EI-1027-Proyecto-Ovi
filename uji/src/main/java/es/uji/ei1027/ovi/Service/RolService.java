package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.dao.AdminOviDao;
import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.modelo.Persona.PersonaFormulario;
import es.uji.ei1027.ovi.modelo.Roles.RolUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolService {

    private PapPatiDao papPatiDao;
    private OviUserDao oviUserDao;
    private AdminOviDao adminOviDao;

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setAdminOviDao(AdminOviDao adminOviDao) {
        this.adminOviDao = adminOviDao;
    }

    public void borrarRol(int idPersona, String rolTexto) {
        RolUsuario rolUsuario = RolUsuario.fromString(rolTexto);
        borrarRol(idPersona, rolUsuario);
    }
    //esto se podria mejorar usando una interfaz para que asi los metodos fueran mejores y reutilizables
    public void borrarRol(int idPersona, RolUsuario rolUsuario) {
        try {
            switch (rolUsuario) {
                case Pap_pati:
                    borrarPapPati(idPersona);
                    break;

                case Ovi_user:
                    borrarOviUser(idPersona);
                    break;

                case Admin_ovi:
                    borrarAdminOvi(idPersona);
                    break;

                default:
                    throw new IllegalArgumentException("Rol no válido: " + rolUsuario);
            }
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede borrar el rol porque tiene datos asociados.",
                    e
            );
        }
    }

    private void borrarPapPati(int idPersona) {
        if (!papPatiDao.existePapPati(idPersona)) {
            throw new IllegalArgumentException("La persona no tiene rol PAP/PATI.");
        }

        papPatiDao.delete(idPersona);
    }

    private void borrarOviUser(int idPersona) {
        if (!oviUserDao.existeOviUser(idPersona)) {
            throw new IllegalArgumentException("La persona no tiene rol Usuario OVI.");
        }

        oviUserDao.delete(idPersona);
    }

    private void borrarAdminOvi(int idPersona) {
        if (!adminOviDao.existeAdminOvi(idPersona)) {
            throw new IllegalArgumentException("La persona no tiene rol Admin OVI.");
        }

        adminOviDao.delete(idPersona);
    }
    public void crearRolRapido(int idPersona, String rolTexto) {
        RolUsuario rolUsuario = RolUsuario.fromString(rolTexto);
        crearRolRapido(idPersona, rolUsuario);
    }

    public void crearRolRapido(int idPersona, RolUsuario rolUsuario) {
        switch (rolUsuario) {
            case Pap_pati:
                papPatiDao.crearRapidoActivo(idPersona);
                break;

            case Ovi_user:
                oviUserDao.crearRapidoActivo(idPersona);
                break;

            case Admin_ovi:
                adminOviDao.addByIdPersona(idPersona);
                break;

            default:
                throw new IllegalArgumentException("Rol no válido: " + rolUsuario);
        }
    }
    public List<RolUsuario> getRolesAsignados(PersonaFormulario formulario) {
        List<RolUsuario> roles = new ArrayList<>();

        if (formulario.getAdminOvi() != null) {
            roles.add(RolUsuario.Admin_ovi);
        }

        if (formulario.getOviUser() != null) {
            roles.add(RolUsuario.Ovi_user);
        }

        if (formulario.getPapPati() != null) {
            roles.add(RolUsuario.Pap_pati);
        }

        return roles;
    }

    public List<RolUsuario> getRolesNoAsignados(PersonaFormulario formulario) {
        List<RolUsuario> rolesAsignados = getRolesAsignados(formulario);
        List<RolUsuario> rolesNoAsignados = new ArrayList<>();

        for (RolUsuario rol : RolUsuario.getLista()) {
            if (!rolesAsignados.contains(rol)) {
                rolesNoAsignados.add(rol);
            }
        }

        return rolesNoAsignados;
    }

}