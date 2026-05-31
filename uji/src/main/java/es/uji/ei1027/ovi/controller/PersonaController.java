package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.AuthService;
import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.Service.SesionService;
import es.uji.ei1027.ovi.Validadores.PersonaValidator;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Persona.PersonaFormulario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/Persona")
public class PersonaController {

    private PersonaService personaService;
    private AuthService authService;
    private SesionService sesionService;

    @Autowired
    public void setPersonaService(PersonaService personaService) {
        this.personaService = personaService;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setSesionService(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @GetMapping("/list/{tipo}")
    public String listarPorTipo(@PathVariable String tipo, Model model, HttpSession session) {
        String url = "/Persona/list/" + tipo;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!personaService.esAdminOvi(usuario)) {
            return "redirect:/";
        }

        List<Persona> personas = personaService.getPersonasPorTipo(tipo);

        model.addAttribute("personasOrderId", personas);
        model.addAttribute("tituloListado", personaService.getTituloListado(tipo));

        return "Persona/list";
    }

    @RequestMapping(value = "/delete/{id}")
    public String processDelete(@PathVariable int id) {
        personaService.deletePersona(id);
        return "redirect:/Persona/list/todas";
    }

    @GetMapping("/update/{id}")
    public String editPersona(Model model, @PathVariable int id, HttpSession session) {
        UsuarioSesion usuario = sesionService.getUsuario(session);
        String url = "/Persona/update/" + id;

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!personaService.puedeEditarPersona(usuario, id)) {
            return "redirect:/";
        }

        PersonaFormulario formulario = personaService.getPersonaFormulario(id);
        cargarModeloPersona(model, usuario, formulario);

        return "Persona/update";
    }

    @PostMapping("/update")
    public String processUpdateSubmit(
            @ModelAttribute("personaFormulario") PersonaFormulario formulario,
            BindingResult bindingResult,
            HttpSession session) {

        int id = formulario.getPersona().getIdPersona();
        String url = "/Persona/update/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!personaService.puedeEditarPersona(usuario, id)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "Persona/update";
        }

        personaService.updatePersonaFormulario(formulario);

        return "redirect:/Persona/details/" + id;
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable int id, Model model, HttpSession session) {
        UsuarioSesion usuario = sesionService.getUsuario(session);
        String url = "/Persona/details/" + id;

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!personaService.puedeVerDetallePersona(usuario, id)) {
            return "redirect:/";
        }

        PersonaFormulario personaFormulario = personaService.getPersonaFormulario(id);
        cargarModeloPersona(model, usuario, personaFormulario);

        return "Persona/details";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("persona", new Persona());
        return "Persona/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @ModelAttribute("persona") Persona persona,
            BindingResult bindingResult,
            Model model) {

        PersonaValidator validator = new PersonaValidator();
        validator.validate(persona, bindingResult);

        if (bindingResult.hasErrors()) {
            return "Persona/registro";
        }

        try {
            authService.registrarPersona(persona);
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMail", e.getMessage());
            persona.setContrasena(null);
            model.addAttribute("persona", persona);
            return "Persona/registro";
        }
    }

    private void cargarModeloPersona(
            Model model,
            UsuarioSesion usuario,
            PersonaFormulario formulario
    ) {
        int idPersona = formulario.getPersona().getIdPersona();

        model.addAttribute("personaFormulario", formulario);

        model.addAttribute("esAdmin", personaService.esAdminOvi(usuario));
        model.addAttribute("esSuPropioPerfil", personaService.esSuPropioPerfil(usuario, idPersona));
        model.addAttribute("puedeEditarPersona", personaService.puedeEditarPersona(usuario, idPersona));

        model.addAttribute("puedeVerBloqueRoles", personaService.puedeVerBloqueRoles(usuario, formulario));
        model.addAttribute("puedeVerBotonOviUser", personaService.puedeVerBotonOviUser(usuario, formulario));
        model.addAttribute("puedeVerBotonPapPati", personaService.puedeVerBotonPapPati(usuario, formulario));
    }
}