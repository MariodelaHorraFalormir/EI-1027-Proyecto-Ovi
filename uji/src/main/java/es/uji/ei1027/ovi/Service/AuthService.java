package es.uji.ei1027.ovi.Service;

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
public class AuthService {

    private PersonaDao personaDao;
    private PersonaService personaService;

    @Autowired
    public void setPersonaDao(PersonaDao personaDao) {
        this.personaDao = personaDao;
    }

    @Autowired
    public void setPersonaService(PersonaService personaService) {
        this.personaService = personaService;
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

        return crearUsuarioSesion(persona);
    }

    private UsuarioSesion crearUsuarioSesion(Persona persona) {
        PersonaFormulario formulario =
                personaService.getPersonaFormulario(persona.getIdPersona());

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
    public void registrarPersona(Persona persona) {
        if (personaDao.existeMail(persona.getMail())) {
            throw new IllegalArgumentException("Ya existe una persona registrada con ese correo.");
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String contrasenaCifrada = passwordEncryptor.encryptPassword(persona.getContrasena());

        persona.setContrasena(contrasenaCifrada);
        persona.setFechaAlta(LocalDate.now());
        persona.setFechaBaja(null);

        personaDao.addPersonaYDevolverId(persona);
    }
}