package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.AuthService;
import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.Service.SesionService;
import es.uji.ei1027.ovi.Validadores.PersonaValidator;
import es.uji.ei1027.ovi.dao.PersonaDao;
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
    private PersonaDao personaDao;

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
    public String listarPorTipo(@PathVariable String tipo, Model model, HttpSession session,
                                @RequestParam(defaultValue = "0") int page) {
        String url = "/Persona/list/" + tipo;

        UsuarioSesion usuario = sesionService.getUsuario(session);
        if (usuario == null) return sesionService.redirigirALogin(session, url);
        if (!personaService.esAdminOvi(usuario)) return "redirect:/";

        int pageSize = 10;
        List<Persona> todas = personaService.getPersonasPorTipo(tipo);
        int total = todas.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Persona> pagina = (from <= to) ? todas.subList(from, to) : java.util.Collections.emptyList();

        model.addAttribute("personasOrderId", pagina);
        model.addAttribute("tituloListado", personaService.getTituloListado(tipo));
        model.addAttribute("tipoActual", tipo);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
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
            Model model,
            HttpSession session) {

        int id = formulario.getPersona().getIdPersona();
        String url = "/Persona/update/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }
        conservarFechasOriginalesSiVienenVacias(formulario, id);
        if (!personaService.puedeEditarPersona(usuario, id)) {
            return "redirect:/";
        }

        PersonaValidator validator = new PersonaValidator(
                personaDao,
                PersonaValidator.ModoValidacion.UPDATE
        );

        bindingResult.pushNestedPath("persona");
        try {
            validator.validate(formulario.getPersona(), bindingResult);
        } finally {
            bindingResult.popNestedPath();
        }

        validarNuevaContrasena(formulario, bindingResult);

        if (bindingResult.hasErrors()) {
            recargarRolesFormulario(formulario, id);
            cargarModeloPersona(model, usuario, formulario);
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

        PersonaValidator validator = new PersonaValidator(
                personaDao,
                PersonaValidator.ModoValidacion.REGISTRO
        );

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

    @Autowired
    public void setPersonaDao(PersonaDao personaDao) {
        this.personaDao = personaDao;
    }
    private void validarNuevaContrasena(
            PersonaFormulario formulario,
            BindingResult bindingResult
    ) {
        String nuevaContrasena = formulario.getNuevaContrasena();

        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            return;
        }

        if (nuevaContrasena.length() > 100) {
            bindingResult.rejectValue(
                    "nuevaContrasena",
                    "longitud",
                    "Longitud máxima superada"
            );
        } else if (nuevaContrasena.length() < 6) {
            bindingResult.rejectValue(
                    "nuevaContrasena",
                    "corta",
                    "La contraseña debe tener al menos 6 caracteres"
            );
        }
    }
    private void recargarRolesFormulario(PersonaFormulario formulario, int id) {
        PersonaFormulario formularioCompleto = personaService.getPersonaFormulario(id);

        if (formularioCompleto == null) {
            return;
        }

        formulario.setOviUser(formularioCompleto.getOviUser());
        formulario.setPapPati(formularioCompleto.getPapPati());
        formulario.setAdminOvi(formularioCompleto.getAdminOvi());
    }
    private void conservarFechasOriginalesSiVienenVacias(PersonaFormulario formulario, int id) {
        Persona personaOriginal = personaDao.getPersona(id);

        if (personaOriginal == null || formulario.getPersona() == null) {
            return;
        }

        Persona personaEditada = formulario.getPersona();

        if (personaEditada.getFechaNacimiento() == null) {
            personaEditada.setFechaNacimiento(personaOriginal.getFechaNacimiento());
        }

        if (personaEditada.getFechaAlta() == null) {
            personaEditada.setFechaAlta(personaOriginal.getFechaAlta());
        }

        if (personaEditada.getFechaBaja() == null) {
            personaEditada.setFechaBaja(personaOriginal.getFechaBaja());
        }
    }
}