package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Rol")
public class RolController {

    private PersonaService personaService;

    @Autowired
    public void setPersonaService(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/solicitar/{tipo}")
    public String mostrarCuestion(@PathVariable String tipo, Model model) {
        TipoSolicitud tipoSolicitud = TipoSolicitud.fromString(tipo);

        if (!esRolValido(tipoSolicitud)) {
            return "redirect:/";
        }

        model.addAttribute("tipoSolicitud", tipoSolicitud);
        model.addAttribute("textoRol", tipoSolicitud.getTexto());

        return "Rol/cuestion";
    }

    @GetMapping("/mail/{tipo}")
    public String mostrarFormularioMail(@PathVariable String tipo, Model model) {
        TipoSolicitud tipoSolicitud = TipoSolicitud.fromString(tipo);

        if (!esRolValido(tipoSolicitud)) {
            return "redirect:/";
        }

        model.addAttribute("tipoSolicitud", tipoSolicitud);
        model.addAttribute("textoRol", tipoSolicitud.getTexto());
        model.addAttribute("persona", new Persona());

        return "Rol/introducirMail";
    }

    @PostMapping("/mail/{tipo}")
    public String procesarFormularioMail(@PathVariable String tipo,
                                         @ModelAttribute("persona") Persona persona,
                                         Model model) {
        TipoSolicitud tipoSolicitud = TipoSolicitud.fromString(tipo);

        if (!esRolValido(tipoSolicitud)) {
            return "redirect:/";
        }

        Integer idPersona = personaService.getIdPersonaByMail(persona.getMail());

        if (idPersona == null) {
            model.addAttribute("tipoSolicitud", tipoSolicitud);
            model.addAttribute("textoRol", tipoSolicitud.getTexto());
            model.addAttribute("persona", persona);
            model.addAttribute("mensajeError", "No existe ninguna persona con ese correo.");
            return "Rol/introducirMail";
        }

        switch (tipoSolicitud) {
            case Ovi_user:
                return "redirect:/OviUser/create/" + idPersona;
            case Pap_pati:
                return "redirect:/PapPati/create/" + idPersona;
            default:
                return "redirect:/";
        }
    }

    private boolean esRolValido(TipoSolicitud tipoSolicitud) {
        return tipoSolicitud == TipoSolicitud.Ovi_user ||
                tipoSolicitud == TipoSolicitud.Pap_pati;
    }
}