package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Roles.RolUsuario;
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
        RolUsuario rolUsuario;
        if (!esRolValido(tipoSolicitud)) {
            return "redirect:/";
        }
        switch (tipoSolicitud) {
            case Ovi_user:
                return "redirect:/OviUser/solicitud";

            case Pap_pati:
                return "redirect:/PapPati/solicitud";

            default:
                return "redirect:/";
        }
    }


    private boolean esRolValido(TipoSolicitud tipoSolicitud) {
        return tipoSolicitud == TipoSolicitud.Ovi_user ||
                tipoSolicitud == TipoSolicitud.Pap_pati;
    }
}