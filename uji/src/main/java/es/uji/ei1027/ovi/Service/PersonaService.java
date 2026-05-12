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
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Personalidad;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

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
        formulario.setPatPati(patPatiDao.getPapPati(id));
        formulario.setOviUser(oviUserDao.getOviUser(id));
        return formulario;
    }

    public void updatePersonaFormulario(PersonaFormulario formulario) {
        personaDao.updatePersona(formulario.getPersona());

        if (formulario.getPatPati() != null) {

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

    public List<PapPati> getRecomendaciones(PaRequest request) {
        List<PapPati> todosLosAsistentes = patPatiDao.getTodosPapPati();

        return todosLosAsistentes.stream()
                .filter(asistente -> {
                    Persona datosPersona = personaDao.getPersona(asistente.getIdPatPati());
                    if (request.getGeneroPreferido() == null) return true;
                    return datosPersona.getGenero().equals(request.getGeneroPreferido());
                })
                .filter(asistente -> {
                    return asistente.getExperiencia() >= request.getExperienciaMinima();
                })
                .peek(asistente -> {
                    double score = calcularPorcentajeAfinidad(request.getPersonalidadDeseada(), asistente.getPersonalidad());
                    asistente.setScoreAfinidad(score);
                })
                .sorted(Comparator.comparing(PapPati::getScoreAfinidad).reversed())
                .collect(Collectors.toList());
    }

    private double calcularPorcentajeAfinidad(Personalidad deseada, Personalidad real) {
        if (deseada == null || real == null) return 0.0;

        int diferenciaTotal = Math.abs(deseada.getMovimiento() - real.getMovimiento()) +
                Math.abs(deseada.getHabla() - real.getHabla()) +
                Math.abs(deseada.getExpresividad() - real.getExpresividad()) +
                Math.abs(deseada.getCaracter() - real.getCaracter()) +
                Math.abs(deseada.getNaturaleza() - real.getNaturaleza());

        double afinidad = 100.0 - ((diferenciaTotal / 35.0) * 100.0);

        return Math.round(afinidad * 100.0) / 100.0;
    }
}