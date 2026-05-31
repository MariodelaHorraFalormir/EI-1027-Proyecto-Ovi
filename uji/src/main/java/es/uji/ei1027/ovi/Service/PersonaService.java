package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.dao.AdminOviDao;
import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Persona.PersonaFormulario;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PersonaService {

    private PersonaDao personaDao;
    private PapPatiDao patPatiDao;
    private OviUserDao oviUserDao;
    private AdminOviDao adminOviDao;


    @Autowired
    public void setPersonaDao(PersonaDao personaDao) {
        this.personaDao = personaDao;
    }

    @Autowired
    public void setPatPatiDao(PapPatiDao patPatiDao) {
        this.patPatiDao = patPatiDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setAdminOviDao(AdminOviDao adminOviDao) {
        this.adminOviDao = adminOviDao;
    }

    public PersonaFormulario getPersonaFormulario(int id) {
        PersonaFormulario formulario = new PersonaFormulario();
        formulario.setPersona(personaDao.getPersona(id));
        if (patPatiDao.existePapPati(id)) {
            formulario.setPapPati(patPatiDao.getPapPati(id));
        }
        if (oviUserDao.existeOviUser(id)) {
            formulario.setOviUser(oviUserDao.getOviUser(id));
        }
        if (adminOviDao.existeAdminOvi(id)) {
            formulario.setAdminOvi(adminOviDao.getAdminOvi(id));
        }
        return formulario;
    }
    public boolean puedeVerDetallePersona(UsuarioSesion usuario, int idPersona) {
        return esAdminOvi(usuario) || esSuPropioPerfil(usuario, idPersona);
    }

    @Transactional
    public void updatePersonaFormulario(PersonaFormulario formulario) {
        Persona persona = formulario.getPersona();
        if (persona.getGenero() == null) {
            persona.setGenero(personaDao.getGeneroById(persona.getIdPersona()));
        }
        personaDao.updatePersona(persona);
        actualizarContrasenaSiProcede(persona.getIdPersona(),formulario.getNuevaContrasena());

    }
    private void actualizarContrasenaSiProcede(int idPersona, String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return;
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String newpass = passwordEncryptor.encryptPassword(nuevaContrasena);
        personaDao.updatePass(newpass, idPersona);
    }
    public Integer getIdPersonaByMail(String mail) {
        return personaDao.getIdPersonaByMail(mail);

    }
    @Transactional
    public void deletePersona(int id) {
        personaDao.deletePersona(id);
    }

    public List<Persona> getPersonasPorTipo(String tipo) {
        return switch (tipo) {
            case "todas" -> personaDao.getPersonasOrderId();
            case "ovi-users" -> personaDao.getPersonasOviUsers();
            case "pap-patis" -> personaDao.getPersonasPapPati();
            case "admins" -> personaDao.getPersonasAdminOvi();
            default -> throw new IllegalArgumentException("Tipo de listado no válido: " + tipo);
        };
    }

    public String getTituloListado(String tipo) {
        return switch (tipo) {
            case "todas" -> "Todas las personas";
            case "ovi-users" -> "Usuarios OVI";
            case "pap-patis" -> "PAP/PATI";
            case "admins" -> "Administradores OVI";
            default -> "Listado de personas";
        };
    }
    public boolean esAdminOvi(UsuarioSesion usuario) {
        return usuario != null && usuario.esAdminOvi();
    }

    public boolean esSuPropioPerfil(UsuarioSesion usuario, int idPersona) {
        return usuario != null && usuario.getIdPersona() == idPersona;
    }

    public boolean puedeEditarPersona(UsuarioSesion usuario, int idPersona) {
        return esAdminOvi(usuario) || esSuPropioPerfil(usuario, idPersona);
    }

    public boolean puedeVerBotonOviUser(UsuarioSesion usuario, PersonaFormulario formulario) {
        if (formulario == null || !formulario.tieneOviUser()) {
            return false;
        }

        if (esAdminOvi(usuario)) {
            return true;
        }

        return formulario.tieneOviUserActivo()
                || formulario.tieneOviUserRechazado();
    }

    public boolean puedeVerBotonPapPati(UsuarioSesion usuario, PersonaFormulario formulario) {
        if (formulario == null || !formulario.tienePapPati()) {
            return false;
        }

        if (esAdminOvi(usuario)) {
            return true;
        }

        return formulario.tienePapPatiActivo()
                || formulario.tienePapPatiRechazado();
    }

    public boolean puedeVerBloqueRoles(UsuarioSesion usuario, PersonaFormulario formulario) {
        return puedeVerBotonOviUser(usuario, formulario)
                || puedeVerBotonPapPati(usuario, formulario);
    }
}