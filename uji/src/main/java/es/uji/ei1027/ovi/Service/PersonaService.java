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
    public void setAdminOviDao(AdminOviDao adminOviDao) {this.adminOviDao = adminOviDao;}

    public PersonaFormulario getPersonaFormulario(int id) {
        PersonaFormulario formulario = new PersonaFormulario();
        formulario.setPersona(personaDao.getPersona(id));
        if (patPatiDao.existePapPati(id) ){
            formulario.setPapPati(patPatiDao.getPapPati(id));
        }
        if (oviUserDao.existeOviUser(id)){
            formulario.setOviUser(oviUserDao.getOviUser(id));
        }
        if (adminOviDao.existeAdminOvi(id)) {
            formulario.setAdminOvi(adminOviDao.getAdminOvi(id));
        }
        return formulario;
    }
    @Transactional
    public void updatePersonaFormulario(PersonaFormulario formulario) {
        personaDao.updatePersona(formulario.getPersona());

        if (formulario.getPapPati() != null) {
            patPatiDao.update(formulario.getPapPati());
        }

        if (formulario.getOviUser() != null) {
            oviUserDao.updateOviUser(formulario.getOviUser());
        }

    }
    @Transactional
    public void registrarOviUser(PersonaFormulario formulario) {
        Persona persona = formulario.getPersona();

        if (personaDao.existeMail(persona.getMail())) {
            throw new IllegalArgumentException("Ya existe una persona registrada con ese correo.");
        }

        persona.setFechaAlta(LocalDate.now());
        persona.setFechaBaja(null);

        int idPersona = personaDao.addPersonaYDevolverId(persona);
    }
    @Transactional
    public String asignarRolOviUserPorMail(String mail) {
        Integer idPersona = personaDao.getIdPersonaByMail(mail);

        if (idPersona == null) {
            throw new IllegalArgumentException("No existe ninguna persona con ese correo.");
        }

        if (oviUserDao.existeOviUser(idPersona)) {
            return "La persona ya tiene el rol OVI user.";
        }

        return "Rol OVI user asignado correctamente.";
    }

    public Integer getIdPersonaByMail(String mail) {
        return  personaDao.getIdPersonaByMail(mail);

    }
    public UsuarioSesion autenticar(String mail, String contrasena) {
        if (mail == null || contrasena == null) {
            return null;
        }

        Persona persona = personaDao.getPersonaByMail(mail.trim());

        if (persona == null) {
            return null;
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

        if (!passwordEncryptor.checkPassword(contrasena, persona.getContrasena())) {
            return null;
        }

        PersonaFormulario formulario = getPersonaFormulario(persona.getIdPersona());

        UsuarioSesion usuarioSesion = new UsuarioSesion();
        usuarioSesion.setIdPersona(persona.getIdPersona());
        usuarioSesion.setMail(persona.getMail());
        usuarioSesion.setNombre(persona.getNombre());
        usuarioSesion.setRolesActivos(formulario.getRolesActivos());
        usuarioSesion.setRolesExistentes(formulario.getRolesExistentes());

        if (formulario.getOviUser() != null) {
            usuarioSesion.setEstadoOviUser(formulario.getOviUser().getEstado());
        }

        if (formulario.getPapPati() != null) {
            usuarioSesion.setEstadoPapPati(formulario.getPapPati().getEstadoRol());
        }
        return usuarioSesion;
    }
    @Transactional
    public int registrarPersona(Persona persona) {
        if (personaDao.existeMail(persona.getMail())) {
            throw new IllegalArgumentException("Ya existe una persona registrada con ese correo.");
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        persona.setContrasena(passwordEncryptor.encryptPassword(persona.getContrasena()));

        persona.setFechaAlta(LocalDate.now());
        persona.setFechaBaja(null);

        return personaDao.addPersonaYDevolverId(persona);
    }
}