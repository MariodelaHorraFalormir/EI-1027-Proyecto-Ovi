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
    @RequestMapping("/detail/{id}")
    public String  detalles(Model model,@PathVariable int id , HttpSession session){
        String url = "/Solicitudes/detail/" + id;
        UsuarioSesion usuario = sesionService.getUsuario(session);
        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }
        Solicitud solicitud = solicitudDao.getSolicitudById(id);
        if (solicitud == null) {
            return "redirect:/";
        }
        if (!solicitudesService.puedeGestionarSolicitudes(usuario)){
            return "redirect:/";
        }
        boolean esAdmin = solicitudesService.puedeGestionarSolicitudes(usuario);
        boolean esSuSolicitud = solicitud.getIdSolicitud() == usuario.getIdPersona();
        if (esAdmin) {
            model.addAttribute("urlVolverSolicitudes", "/Solicitudes/list/todas");
            model.addAttribute("textoVolverSolicitudes", "Volver al listado");
        } else {
            model.addAttribute("urlVolverSolicitudes", "/Solicitudes/mis");
            model.addAttribute("textoVolverSolicitudes", "Volver a mis solicitudes");
        }
        model.addAttribute("solicitud", solicitudDao.getSolicitudById(id));
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("puedeEditarSolicitud", esAdmin);
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
    public String  aprobarRapido(Model model, @PathVariable int id , HttpSession session){
        String url = "/Solicitudes/aprobarRapido/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }
        solicitudesService.puedeGestionarSolicitudes(usuario);
        solicitudesService.aprobarRapido(id,usuario);

        return "redirect:/Solicitudes/list/todas";
    }
    //esta en estado de pruebas la idea es que redirija a una  pagina donde te permita hem poner el motivo de la resolucion
    @PostMapping("/rechazarRapido/{id}")
    public String  rechazarRapido(Model model,@PathVariable int id , HttpSession session){
        //aqui cojera el metodo para mandarte a la pagina de lo de añadir motivo de rechazo
        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            sesionService.guardarNextUrl(session, "/Solicitudes/list/todas");
            return "redirect:/login";
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        solicitudesService.rechazarRapido(id, usuario);
        return "redirect:/Solicitudes/list/todas";
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

    // --- VISTAS DEL FRONT-OFFICE (USUARIO) ---

    @GetMapping("/misSolicitudes")
    public String misSolicitudes(Model model) {
        // Como dijo Mario, hasta que tengamos el login hecho,
        // para ver que la tabla funciona y tiene buen diseño,
        // le paso temporalmente todas las solicitudes que haya en la BD.
        model.addAttribute("solicitudes", solicitudDao.getSolicitudesOrderId());

        return "Solicitudes/misSolicitudes";
    }

    @GetMapping("/nueva")
    public String crearSolicitud() {
        return "Solicitudes/create";
    }

}
