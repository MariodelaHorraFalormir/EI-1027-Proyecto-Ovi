package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/Persona")
public class PersonaController {

    private PersonaDao personaDao;
    @Autowired
    public void setPersonaDao(PersonaDao personaDao) {
        this.personaDao = personaDao;
    }

    @RequestMapping("/listId")
    public String  listaporId(Model model){

        model.addAttribute("personasOrderId", personaDao.getPersonasOrderId());
        return "Persona/listId";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        personaDao.deletePersona(id);
        return "redirect:../listId";
    }
}
