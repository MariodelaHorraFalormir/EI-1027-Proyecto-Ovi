package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Persona.PersonaFormulario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PersonaService {

    private PersonaDao personaDao;
    private PapPatiDao patPatiDao;
    private OviUserDao oviUserDao;

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

    public PersonaFormulario getPersonaFormulario(int id) {
        PersonaFormulario formulario = new PersonaFormulario();
        formulario.setPersona(personaDao.getPersona(id));
        if (patPatiDao.existePapPati(id)){
            formulario.setPatPati(patPatiDao.getPapPati(id));
        }
        if (oviUserDao.existeOviUser(id)){
            formulario.setOviUser(oviUserDao.getOviUser(id));
        }
        return formulario;
    }

    public void updatePersonaFormulario(PersonaFormulario formulario) {
        personaDao.updatePersona(formulario.getPersona());

        if (formulario.getPatPati() != null) {
            patPatiDao.update(formulario.getPatPati());
        }

        if (formulario.getOviUser() != null) {

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
}