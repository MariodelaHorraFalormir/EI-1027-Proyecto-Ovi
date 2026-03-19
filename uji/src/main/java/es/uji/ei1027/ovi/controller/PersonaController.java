package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Persona.PersonaFormulario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/Persona")
public class PersonaController {
    private PersonaService personaService;
    private PersonaDao personaDao;
    @Autowired
    public void setPersonaService(PersonaService personaService) {this.personaService = personaService;}
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
        public String editPersona(Model model , @PathVariable int id) {
        PersonaFormulario formulario = personaService.getPersonaFormulario(id);
        model.addAttribute("personaFormulario", formulario);
            return "Persona/update";
            //

        }
    @RequestMapping(value="/update", method=RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("personaFormulario") PersonaFormulario formulario,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "Persona/update";
        }

        personaService.updatePersonaFormulario(formulario);
        return "redirect:/Persona/listId";
    }


}
