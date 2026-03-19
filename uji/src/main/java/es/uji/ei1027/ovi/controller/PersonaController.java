package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/persona") // Todo en minúscula para que coincida con los botones HTML
public class PersonaController {

    private PersonaDao personaDao;

    @Autowired
    public void setPersonaDao(PersonaDao personaDao) {
        this.personaDao = personaDao;
    }

    // --- LISTAR (READ) ---
    @RequestMapping("/listId")
    public String listaporId(Model model){
        model.addAttribute("personasOrderId", personaDao.getPersonasOrderId());
        return "Persona/listId";
    }

    // --- AÑADIR (CREATE) ---
    @RequestMapping("/add")
    public String addPersona(Model model) {
        model.addAttribute("persona", new Persona());
        return "Persona/add"; // Redirige a un formulario HTML (que crearemos luego)
    }

    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("persona") Persona persona) {
        personaDao.addPersona(persona);
        return "redirect:/persona/listId";
    }

    // --- EDITAR (UPDATE) ---
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editPersona(Model model, @PathVariable int id) {
        model.addAttribute("persona", personaDao.getPersona(id));
        return "Persona/update"; // Redirige a un formulario HTML (que crearemos luego)
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("persona") Persona persona) {
        personaDao.updatePersona(persona);
        return "redirect:/persona/listId";
    }

    // --- BORRAR (DELETE) ---
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        personaDao.deletePersona(id);
        return "redirect:/persona/listId";
    }
}