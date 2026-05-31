package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.Service.SesionService;
import es.uji.ei1027.ovi.Service.SolicitudesService;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Solicitudes")
public class SolicitudController {
    private SolicitudesDao solicitudDao;
    private SolicitudesService solicitudesService;
    private SesionService sesionService;
    @Autowired
    public void setSesionService(SesionService sesionService) {this.sesionService = sesionService;}
    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {this.solicitudDao = solicitudDao;}

    @Autowired
    public void setSolicitudesService(SolicitudesService solicitudesService) {
        this.solicitudesService = solicitudesService;
    }


    @RequestMapping("/list/{tipo}")
    public String  listaporId(Model model , @PathVariable String tipo , HttpSession session){
        String url = "/Solicitudes/list/" + tipo;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }
        model.addAttribute("solicitudes", solicitudesService.getSolicitudesPorTipo(tipo));
        model.addAttribute("tituloListado", solicitudesService.getTituloListado(tipo));
        model.addAttribute("tipoActual", tipo);
        return "Solicitudes/listId";
    }
    @GetMapping("/detail/{id}")
    public String detalles(Model model, @PathVariable int id, HttpSession session) {
        String url = "/Solicitudes/detail/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        Solicitud solicitud = solicitudDao.getSolicitudById(id);

        if (solicitud == null) {
            return "redirect:/";
        }

        if (!solicitudesService.puedeVerSolicitud(usuario, solicitud)) {
            return "redirect:/";
        }

        boolean esAdmin = solicitudesService.puedeGestionarSolicitudes(usuario);

        if (esAdmin) {
            model.addAttribute("urlVolverSolicitudes", "/Solicitudes/list/todas");
            model.addAttribute("textoVolverSolicitudes", "Volver al listado");
        } else {
            model.addAttribute("urlVolverSolicitudes", "/Solicitudes/mis");
            model.addAttribute("textoVolverSolicitudes", "Volver a mis solicitudes");
        }

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("puedeEditarSolicitud", esAdmin);
        model.addAttribute(
                "puedeSolicitarRevision",
                solicitudesService.puedeSolicitarRevision(usuario, solicitud)
        );
        return "Solicitudes/detail";
    }
    @RequestMapping(value ="/update/{id}" ,method = RequestMethod.GET)
    public String  update(Model model,@PathVariable int id , HttpSession session){
        String url = "/Solicitudes/update/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }
        Solicitud solicitud = solicitudDao.getSolicitudById(id);
        model.addAttribute("categoriaList", solicitud.getCategoriaSolicitud().getLista());
        model.addAttribute("tipoList", solicitud.getTipoSolicitud().getLista());
        model.addAttribute("estadoList", solicitud.getEstadoSolicitud().getLista());
        model.addAttribute("solicitud", solicitudDao.getSolicitudById(id));
        return "Solicitudes/update";
    }
    @PostMapping("/update/{id}")
    public String updateSolicitud(@PathVariable int id, @ModelAttribute("solicitud") Solicitud solicitud , HttpSession session) {
        String url = "/Solicitudes/update/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);
        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        solicitudesService.updateSolicitud(id, solicitud,usuario);
        return "redirect:/Solicitudes/list/todas";
    }
    @PostMapping("/aprobarRapido/{id}")
    public String aprobarRapido(Model model, @PathVariable int id , HttpSession session){
        String url = "/Solicitudes/aprobarRapido/" + id;
        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }
        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        // 1. Aprueba la solicitud en base de datos
        solicitudesService.aprobarRapido(id,usuario);

        // 2. Buscamos la solicitud para saber a quién enviarle el correo
        Solicitud solicitud = solicitudDao.getSolicitudById(id);

        // 3. Preparamos el correo simulado
        model.addAttribute("para", "Usuario solicitante (ID: " + solicitud.getPersonaSolicitante() + ")");
        model.addAttribute("asunto", "✅ Tu Solicitud OVI ha sido APROBADA");
        model.addAttribute("cuerpo", "Hola, te informamos de que el Técnico OVI ha revisado y aprobado tu solicitud general con número #" + id + ". Ya puedes continuar con el proceso en la plataforma.");
        model.addAttribute("volverUrl", "/Solicitudes/list/todas");

        return "correo/simulacion";
    }


    @PostMapping("/rechazarRapido/{id}")
    public String rechazarRapido(Model model,@PathVariable int id , HttpSession session){
        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            sesionService.guardarNextUrl(session, "/Solicitudes/list/todas");
            return "redirect:/login";
        }
        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        // 1. Rechaza la solicitud en base de datos
        solicitudesService.rechazarRapido(id, usuario);

        Solicitud solicitud = solicitudDao.getSolicitudById(id);

        // 2. Preparamos el correo simulado
        model.addAttribute("para", "Usuario solicitante (ID: " + solicitud.getPersonaSolicitante() + ")");
        model.addAttribute("asunto", "❌ Tu Solicitud OVI ha sido RECHAZADA");
        model.addAttribute("cuerpo", "Hola, lamentamos informarte de que tu solicitud #" + id + " ha sido denegada. Por favor, revisa tus datos o ponte en contacto con la oficina.");
        model.addAttribute("volverUrl", "/Solicitudes/list/todas");

        return "correo/simulacion";
    }
    @RequestMapping("/delete/{id}")
    public String processDelete(@PathVariable int id , HttpSession session) {
        String url = "/Solicitudes/delete/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }
        solicitudDao.deleteSolicitud(id);
        return "redirect:/Solicitudes/list/todas";    }


    @GetMapping("/mis")
    public String misSolicitudes(Model model, HttpSession session) {
        String url = "/Solicitudes/mis";

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        model.addAttribute("solicitudes", solicitudesService.getSolicitudesDeUsuario(usuario));
        model.addAttribute("tituloListado", "Mis solicitudes");

        return "Solicitudes/mis";
    }

    @PostMapping("/solicitarRevision/{id}")
    public String solicitarRevision(@PathVariable int id,
                                    @RequestParam(required = false) String mensajeRevision,
                                    HttpSession session) {

        String url = "/Solicitudes/detail/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        try {
            solicitudesService.solicitarRevision(id, usuario, mensajeRevision);
        } catch (IllegalArgumentException e) {
            return "redirect:/Solicitudes/detail/" + id + "?error=revisionNoPermitida";
        } catch (IllegalStateException e) {
            return "redirect:/Solicitudes/detail/" + id + "?error=revisionNoValida";
        } catch (Exception e) {
            return "redirect:/Solicitudes/detail/" + id + "?error=revisionNoSolicitada";
        }

        return "redirect:/Solicitudes/detail/" + id + "?ok=revisionSolicitada";
    }

}
