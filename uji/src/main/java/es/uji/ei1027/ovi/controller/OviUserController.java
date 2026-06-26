package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.OviUserService;
import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.Service.SesionService;
import es.uji.ei1027.ovi.Service.SolicitudesService;
import es.uji.ei1027.ovi.Validadores.OviUserValidator;
import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.DiversidadFuncionalDao;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.OviUser.TipoDiversidadFuncional;
import es.uji.ei1027.ovi.modelo.PapPati.Especialidad;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Solicitud.CategoriaSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import groovy.transform.VisibilityOptions;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/OviUser")
public class OviUserController {
    private SolicitudesDao solicitudesDao;
    private OviUserDao oviUserDao;
    private PersonaService personaService;
    private DiversidadFuncionalDao diversidadFuncionalDao;
    private SesionService sesionService;
    private OviUserService oviUserService;
    private SolicitudesService solicitudesService;

    @Autowired
    public void setOviUserService(OviUserService oviUserService) {
        this.oviUserService = oviUserService;
    }
    @Autowired
    public void setSesionService(SesionService sesionService) {
        this.sesionService = sesionService;
    }
    @Autowired
    public void  setDiversidadFuncionalDao(DiversidadFuncionalDao diversidadFuncionalDao){this.diversidadFuncionalDao=diversidadFuncionalDao;}
    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {this.solicitudesDao = solicitudDao;}
    @Autowired
    public void setPersonaService(PersonaService personaService) {this.personaService = personaService;}
    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }
    @Autowired
    public void setSolicitudesService(SolicitudesService solicitudesService) {
        this.solicitudesService = solicitudesService;
    }

    @GetMapping("/solicitud")
    public String gestionarSolicitudOviUser(HttpSession session) {
        if (!sesionService.hayUsuarioLogueado(session)) {
            sesionService.guardarNextUrl(session, "/OviUser/solicitud");
            return "redirect:/login";
        }

        int idPersona = sesionService.getUsuario(session).getIdPersona();

        // 1. Si ya es OVI User, lo mandamos a ver sus detalles
        if (oviUserDao.getOviUser(idPersona) != null) {
            return "redirect:/OviUser/details/" + idPersona;
        }

        // 2. Si no lo es, lo mandamos DIRECTO al formulario de creación
        return "redirect:/OviUser/create/" + idPersona;
    }


    @GetMapping("/create/{id}")
    public String mostrarFormularioRegistro(Model model , @PathVariable int id , HttpSession session) {
        String url = "/OviUser/create/"+id;

        if (!sesionService.hayUsuarioLogueado(session)) {
            return sesionService.redirigirALogin(session,url);
        }

        // CORRECCIÓN: Si ya existe, lo mandamos a details, no a solicitud (evita bucle infinito)
        if (oviUserDao.getOviUser(id) != null) {
            return "redirect:/OviUser/details/" + id;
        }

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        int idPersona = sesionService.getUsuario(session).getIdPersona();

        if (!personaService.esAdminOvi(usuario) && idPersona != id){
            return "redirect:/";
        }

        Solicitud solicitud = solicitudesService.solicitudRol(id, TipoSolicitud.Ovi_user);
        OviUser oviUser = new OviUser();
        oviUser.setIdOviUser(id);

        model.addAttribute("oviUser", oviUser);
        model.addAttribute("solicitud", solicitud);
        return "OviUser/create";
    }

    @PostMapping("/create/{id}")
    public String procesarRegistro(
            @ModelAttribute("oviUser") OviUser oviUser,
            BindingResult bindingResult,
            @ModelAttribute("solicitud") Solicitud solicitud,
            Model model,
            @PathVariable int id,
            HttpSession session) {

        String url = "/OviUser/create/" + id;

        if (!sesionService.hayUsuarioLogueado(session)) {
            return sesionService.redirigirALogin(session, url);
        }

        // CORRECCIÓN: Evitar bucle infinito
        if (oviUserDao.getOviUser(id) != null) {
            return "redirect:/OviUser/details/" + id;
        }

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        int idPersona = sesionService.getUsuario(session).getIdPersona();

        if (!personaService.esAdminOvi(usuario) && idPersona != id) {
            return "redirect:/";
        }

        oviUser.setIdOviUser(id);

        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("oviUser", oviUser);
            model.addAttribute("solicitud", solicitud);
            return "OviUser/create";
        }

        try {
            oviUserService.creaOviUser(oviUser, solicitud);
        } catch (Exception e) {
            model.addAttribute("oviUser", oviUser);
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("error", "No se ha podido crear el rol OviUser");
            return "OviUser/create";
        }

        return "redirect:/DiversidadFuncional/listaID/" + id;
    }



    @RequestMapping(value = "/update/{id}",method = RequestMethod.GET)
    public String editPersona(Model model, @PathVariable int id , HttpSession session) {
        String url = "/OviUser/update/" + id;

        if (!sesionService.hayUsuarioLogueado(session)) {
            return sesionService.redirigirALogin(session, url);
        }

        UsuarioSesion usuario = sesionService.getUsuario(session);
        int idPersona = usuario.getIdPersona();

        OviUser oviUser = oviUserService.getOviUser(id);
        if (oviUser == null) {
            return "redirect:/";
        }
        if (!personaService.esAdminOvi(usuario) && idPersona != id) {
            return "redirect:/";
        }
        List<String> diversidades = oviUserService.getDiversidadesTexto(id);


        model.addAttribute("oviUser", oviUser);
        model.addAttribute("diversidades", diversidades);
        return "OviUser/update";

    }
    @PostMapping(value = "/update/{id}")
    public String procesarActualizarPapPati(
            @PathVariable int id,
            @ModelAttribute("oviUser") OviUser oviUser,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {

        String url = "/OviUser/update/" + id;

        if (!sesionService.hayUsuarioLogueado(session)) {
            return sesionService.redirigirALogin(session, url);
        }

        UsuarioSesion usuario = sesionService.getUsuario(session);
        int idPersona = usuario.getIdPersona();

        if (!personaService.esAdminOvi(usuario) && idPersona != id) {
            return "redirect:/";
        }

        OviUser oviUserExistente = oviUserService.getOviUser(id);

        if (oviUserExistente == null) {
            return "redirect:/";
        }

        // Muy importante: aseguramos el id correcto
        oviUser.setIdOviUser(id);

        // Conectamos el validator
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            List<String> diversidades = oviUserService.getDiversidadesTexto(id);

            model.addAttribute("oviUser", oviUser);
            model.addAttribute("diversidades", diversidades);

            return "OviUser/update";
        }

        oviUserService.actualizarOviUser(id, oviUser);

        return "redirect:/OviUser/details/" + id;
    }
    @GetMapping("/details/{id}")
    public String detalles(Model model , @PathVariable int id ,  HttpSession session) {
        String url = "/OviUser/details/" + id;

        if (!sesionService.hayUsuarioLogueado(session)) {
            return sesionService.redirigirALogin(session, url);
        }

        UsuarioSesion usuario = sesionService.getUsuario(session);
        int idPersona = usuario.getIdPersona();

        if (!personaService.esAdminOvi(usuario) && idPersona != id) {
            return "redirect:/";
        }
        OviUser oviUser = oviUserService.getOviUser(id);

        if (oviUser == null) {
            return "redirect:/";
        }

        model.addAttribute("oviUser", oviUser);
        model.addAttribute("diversidades", diversidadFuncionalDao.obtenerDiverdadesPorId(id));
        return "OviUser/details";
    }



}
