package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Persona.PersonaFormulario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        formulario.setPatPati(patPatiDao.getPapPati(id));
        formulario.setOviUser(oviUserDao.getOviUser(id));
        return formulario;
    }

    public void updatePersonaFormulario(PersonaFormulario formulario) {

    }
}