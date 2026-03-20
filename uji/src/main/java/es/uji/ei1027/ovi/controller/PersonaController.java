package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Persona.PersonaFormulario;
import es.uji.ei1027.ovi.modelo.Persona.Roles.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    @GetMapping("/cuestion")
    public String mostrarCuestion() {
        return "Persona/cuestion";
    }

    @GetMapping("/solicitar-rol-oviuser")
    public String mostrarFormularioRol(Model model) {
        model.addAttribute("persona", new Persona());
        return "Persona/solicitarRolOviUser";
    }

    @PostMapping("/solicitar-rol-oviuser")
    public String procesarFormularioRol(
            @ModelAttribute("persona") Persona persona,
            Model model) {

        try {
            String mensaje = personaService.asignarRolOviUserPorMail(persona.getMail());
            model.addAttribute("mensajeExito", mensaje);
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }

        model.addAttribute("persona", persona);
        return "Persona/solicitarRolOviUser";
    }




















    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        PersonaFormulario registroForm = new PersonaFormulario();
        registroForm.setPersona(new Persona());
        registroForm.setOviUser(new OviUser());

        model.addAttribute("registroForm", registroForm);
        return "Persona/registro";
    }
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("registroForm") PersonaFormulario registroForm,
                                   BindingResult bindingResult,
                                   Model model) {

        if (bindingResult.hasErrors()) {
            return "Persona/registro";
        }

        try {
            personaService.registrarOviUser(registroForm);
            return "redirect:/Persona/listId";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMail", e.getMessage());
            return "Persona/registro";
        }
    }





}
