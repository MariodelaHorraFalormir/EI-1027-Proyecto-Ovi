package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.Service.RolService;
import es.uji.ei1027.ovi.Service.SesionService;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.Persona.PersonaFormulario;
import es.uji.ei1027.ovi.modelo.Roles.RolUsuario;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Rol")
public class RolController {

    private PersonaService personaService;
    private RolService rolService;
    private SesionService sesionService;

    @Autowired
    public void setPersonaService(PersonaService personaService) {
        this.personaService = personaService;
    }

    @Autowired
    public void setRolService(RolService rolService) {
        this.rolService = rolService;
    }

    @Autowired
    public void setSesionService(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @GetMapping("/solicitar/{tipo}")
    public String mostrarCuestion(@PathVariable String tipo) {
        TipoSolicitud tipoSolicitud;

        try {
            tipoSolicitud = TipoSolicitud.fromString(tipo);
        } catch (IllegalArgumentException e) {
            return "redirect:/";
        }

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

    @GetMapping("/persona/{idPersona}/gestionar")
    public String gestionarRolesPersona(@PathVariable int idPersona,
                                        Model model,
                                        HttpSession session) {

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(
                    session,
                    "/Rol/persona/" + idPersona + "/gestionar"
            );
        }

        if (!usuario.esAdminOvi()) {
            return "redirect:/";
        }

        PersonaFormulario personaFormulario = personaService.getPersonaFormulario(idPersona);

        model.addAttribute("personaFormulario", personaFormulario);
        model.addAttribute("rolesAsignados", rolService.getRolesAsignados(personaFormulario));
        model.addAttribute("rolesNoAsignados", rolService.getRolesNoAsignados(personaFormulario));
        model.addAttribute("usuarioActual", usuario);

        return "Rol/gestionar";
    }

    @PostMapping("/persona/{idPersona}/crear/{rol}")
    public String crearRolRapido(@PathVariable int idPersona,
                                 @PathVariable String rol,
                                 HttpSession session) {

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(
                    session,
                    "/Rol/persona/" + idPersona + "/gestionar"
            );
        }

        if (!usuario.esAdminOvi()) {
            return "redirect:/";
        }

        RolUsuario rolUsuario;

        try {
            rolUsuario = RolUsuario.fromString(rol);
        } catch (IllegalArgumentException e) {
            return "redirect:/Rol/persona/" + idPersona + "/gestionar?error=rolNoValido";
        }

        try {
            rolService.crearRolRapido(idPersona, rolUsuario);
        } catch (IllegalArgumentException e) {
            return "redirect:/Rol/persona/" + idPersona + "/gestionar?error=rolYaExiste";
        } catch (Exception e) {
            return "redirect:/Rol/persona/" + idPersona + "/gestionar?error=rolNoCreado";
        }

        return "redirect:/Rol/persona/" + idPersona + "/gestionar?ok=rolCreado";
    }

    @PostMapping("/persona/{idPersona}/borrar/{rol}")
    public String borrarRol(@PathVariable int idPersona,
                            @PathVariable String rol,
                            HttpSession session) {

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(
                    session,
                    "/Rol/persona/" + idPersona + "/gestionar"
            );
        }

        if (!usuario.esAdminOvi()) {
            return "redirect:/";
        }

        RolUsuario rolUsuario;

        try {
            rolUsuario = RolUsuario.fromString(rol);
        } catch (IllegalArgumentException e) {
            return "redirect:/Rol/persona/" + idPersona + "/gestionar?error=rolNoValido";
        }

        if (usuario.getIdPersona() == idPersona && rolUsuario == RolUsuario.Admin_ovi) {
            return "redirect:/Rol/persona/" + idPersona + "/gestionar?error=noPuedesBorrarteAdmin";
        }

        try {
            rolService.borrarRol(idPersona, rolUsuario);
        } catch (IllegalArgumentException e) {
            return "redirect:/Rol/persona/" + idPersona + "/gestionar?error=rolNoExiste";
        } catch (IllegalStateException e) {
            return "redirect:/Rol/persona/" + idPersona + "/gestionar?error=rolNoBorrable";
        } catch (Exception e) {
            return "redirect:/Rol/persona/" + idPersona + "/gestionar?error=rolNoBorrado";
        }

        return "redirect:/Rol/persona/" + idPersona + "/gestionar?ok=rolBorrado";
    }

    private boolean esRolValido(TipoSolicitud tipoSolicitud) {
        return tipoSolicitud == TipoSolicitud.Ovi_user ||
                tipoSolicitud == TipoSolicitud.Pap_pati ||
                tipoSolicitud == TipoSolicitud.Pa_request;
    }
}