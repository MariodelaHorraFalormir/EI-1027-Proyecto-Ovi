package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.PersonaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    @RequestMapping(value = "/update/{id}",method = RequestMethod.GET)
        public String editPersona(Model model){
            model.addAttribute("personasOrderId", personaDao.getPersonasOrderId());
            //dependiendo de si es ovi user mandar un formulario o otro
            return null;
            //

        }



}
