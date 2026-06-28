package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.SesionService;
import es.uji.ei1027.ovi.Service.SolicitudesService;
import es.uji.ei1027.ovi.Validadores.SolicitudValidator;
import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import es.uji.ei1027.ovi.modelo.PaRequest.StatusPaRequest;
import es.uji.ei1027.ovi.modelo.Solicitud.CategoriaSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/Solicitudes")
public class SolicitudController {

    private SolicitudesDao solicitudDao;
    private SolicitudesService solicitudesService;
    private SesionService sesionService;
    private PaRequestDao paRequestDao;

    @Autowired
    public void setSesionService(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {
        this.solicitudDao = solicitudDao;
    }

    @Autowired
    public void setSolicitudesService(SolicitudesService solicitudesService) {
        this.solicitudesService = solicitudesService;
    }

    @Autowired
    public void setPaRequestDao(PaRequestDao paRequestDao) {
        this.paRequestDao = paRequestDao;
    }

    private void cargarListasSolicitud(Model model) {
        model.addAttribute("categoriaList", CategoriaSolicitud.Rol.getLista());
        model.addAttribute("tipoList", TipoSolicitud.Pap_pati.getLista());
        model.addAttribute("estadoList", EstadoSolicitud.Pendiente.getLista());
    }

    @RequestMapping("/list/{tipo}")
    public String listaporId(Model model,
                             @PathVariable String tipo,
                             HttpSession session,
                             @RequestParam(defaultValue = "0") int page) {

        String url = "/Solicitudes/list/" + tipo;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        int pageSize = 10;
        List<Solicitud> todas = solicitudesService.getSolicitudesPorTipo(tipo);
        int total = todas.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);

        List<Solicitud> pagina =
                from < total ? todas.subList(from, to) : java.util.Collections.emptyList();

        model.addAttribute("solicitudes", pagina);
        model.addAttribute("tituloListado", solicitudesService.getTituloListado(tipo));
        model.addAttribute("tipoActual", tipo);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

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

    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable int id, HttpSession session) {
        String url = "/Solicitudes/update/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        Solicitud solicitud = solicitudDao.getSolicitudById(id);

        if (solicitud == null) {
            return "redirect:/Solicitudes/list/todas";
        }

        cargarListasSolicitud(model);
        model.addAttribute("solicitud", solicitud);

        return "Solicitudes/update";
    }

    @PostMapping("/update/{id}")
    public String updateSolicitud(@PathVariable int id,
                                  @ModelAttribute("solicitud") Solicitud solicitudUpdate,
                                  BindingResult bindingResult,
                                  Model model,
                                  HttpSession session,
                                  RedirectAttributes redirectAttrs) {

        String url = "/Solicitudes/update/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        Solicitud solicitudOriginal = solicitudDao.getSolicitudById(id);

        if (solicitudOriginal == null) {
            return "redirect:/Solicitudes/list/todas";
        }

        solicitudUpdate.setIdSolicitud(id);

        if (solicitudUpdate.getFechaCreacion() == null) {
            solicitudUpdate.setFechaCreacion(solicitudOriginal.getFechaCreacion());
        }

        if (solicitudUpdate.getEstadoSolicitud() == EstadoSolicitud.Pendiente) {
            solicitudUpdate.setFechaResolucion(null);
            solicitudUpdate.setMotivoResolucion(null);
        } else if (solicitudUpdate.getFechaResolucion() == null) {
            solicitudUpdate.setFechaResolucion(solicitudOriginal.getFechaResolucion());
        }

        if (solicitudUpdate.getMensajeSolicitud() != null &&
                solicitudUpdate.getMensajeSolicitud().trim().isEmpty()) {
            solicitudUpdate.setMensajeSolicitud(null);
        }

        if (solicitudUpdate.getMotivoResolucion() != null &&
                solicitudUpdate.getMotivoResolucion().trim().isEmpty()) {
            solicitudUpdate.setMotivoResolucion(null);
        }

        SolicitudValidator solicitudValidator = new SolicitudValidator();
        solicitudValidator.validate(solicitudUpdate, bindingResult);

        if (bindingResult.hasErrors()) {
            cargarListasSolicitud(model);
            return "Solicitudes/update";
        }

        solicitudesService.updateSolicitud(id, solicitudUpdate, usuario);

        Solicitud solicitudGuardada = solicitudDao.getSolicitudById(id);

        if (solicitudGuardada != null &&
                solicitudGuardada.getTipoSolicitud() == TipoSolicitud.Pa_request) {

            List<PaRequest> procesos =
                    paRequestDao.getPaRequestsByOviUser(solicitudGuardada.getPersonaSolicitante());

            StatusPaRequest nuevoEstadoPaRequest = null;

            if (solicitudGuardada.getEstadoSolicitud() == EstadoSolicitud.Aprobada) {
                nuevoEstadoPaRequest = StatusPaRequest.En_activo;
            } else if (solicitudGuardada.getEstadoSolicitud() == EstadoSolicitud.Rechazada) {
                nuevoEstadoPaRequest = StatusPaRequest.Finalizada;
            }

            if (nuevoEstadoPaRequest != null) {
                for (PaRequest proceso : procesos) {
                    if (proceso.getStatus() == StatusPaRequest.En_espera) {
                        paRequestDao.cambiarEstadoPaRequest(proceso.getId(), nuevoEstadoPaRequest);
                        break;
                    }
                }
            }
        }

        redirectAttrs.addFlashAttribute(
                "mensajeExito",
                "Solicitud con ID " + id + " actualizada correctamente."
        );

        return "redirect:/Solicitudes/list/todas";
    }

    @PostMapping("/aprobarRapido/{id}")
    public String aprobarRapido(Model model, @PathVariable int id, HttpSession session) {
        String url = "/Solicitudes/aprobarRapido/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        solicitudesService.aprobarRapido(id, usuario);

        Solicitud solicitud = solicitudDao.getSolicitudById(id);

        if (solicitud != null && solicitud.getTipoSolicitud() == TipoSolicitud.Pa_request) {
            List<PaRequest> procesos =
                    paRequestDao.getPaRequestsByOviUser(solicitud.getPersonaSolicitante());

            for (PaRequest proceso : procesos) {
                if (proceso.getStatus() == StatusPaRequest.En_espera) {
                    paRequestDao.cambiarEstadoPaRequest(proceso.getId(), StatusPaRequest.En_activo);
                    break;
                }
            }
        }

        model.addAttribute("para", "Usuario solicitante (ID: " + solicitud.getPersonaSolicitante() + ")");
        model.addAttribute("asunto", "✅ Tu Solicitud OVI ha sido APROBADA");
        model.addAttribute("cuerpo", "Hola, te informamos de que el Técnico OVI ha revisado y aprobado tu solicitud general con número #" + id + ". Ya puedes continuar con el proceso en la plataforma.");
        model.addAttribute("volverUrl", "/Solicitudes/list/todas");

        return "correo/simulacion";
    }

    @PostMapping("/rechazarRapido/{id}")
    public String rechazarRapido(Model model, @PathVariable int id, HttpSession session) {
        String url = "/Solicitudes/list/todas";

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        solicitudesService.rechazarRapido(id, usuario);

        Solicitud solicitud = solicitudDao.getSolicitudById(id);

        if (solicitud != null && solicitud.getTipoSolicitud() == TipoSolicitud.Pa_request) {
            List<PaRequest> procesos =
                    paRequestDao.getPaRequestsByOviUser(solicitud.getPersonaSolicitante());

            for (PaRequest proceso : procesos) {
                if (proceso.getStatus() == StatusPaRequest.En_espera) {
                    paRequestDao.cambiarEstadoPaRequest(proceso.getId(), StatusPaRequest.Finalizada);
                    break;
                }
            }
        }

        model.addAttribute("para", "Usuario solicitante (ID: " + solicitud.getPersonaSolicitante() + ")");
        model.addAttribute("asunto", "❌ Tu Solicitud OVI ha sido RECHAZADA");
        model.addAttribute("cuerpo", "Hola, lamentamos informarte de que tu solicitud #" + id + " ha sido denegada. Por favor, revisa tus datos o ponte en contacto con la oficina.");
        model.addAttribute("volverUrl", "/Solicitudes/list/todas");

        return "correo/simulacion";
    }

    @GetMapping("/confirmarBorrado/{id}")
    public String confirmarBorrado(@PathVariable int id, Model model, HttpSession session) {
        String url = "/Solicitudes/confirmarBorrado/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        Solicitud solicitud = solicitudDao.getSolicitudById(id);

        if (solicitud == null) {
            return "redirect:/Solicitudes/list/todas";
        }

        model.addAttribute("solicitud", solicitud);

        return "Solicitudes/confirmarBorrado";
    }

    @RequestMapping("/delete/{id}")
    public String processDelete(@PathVariable int id,
                                HttpSession session,
                                RedirectAttributes redirectAttrs) {

        String url = "/Solicitudes/delete/" + id;

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        if (!solicitudesService.puedeGestionarSolicitudes(usuario)) {
            return "redirect:/";
        }

        solicitudDao.deleteSolicitud(id);

        redirectAttrs.addFlashAttribute(
                "mensajeExito",
                "Solicitud con ID " + id + " borrada correctamente."
        );

        return "redirect:/Solicitudes/list/todas";
    }

    @GetMapping("/mis")
    public String misSolicitudes(Model model,
                                 HttpSession session,
                                 @RequestParam(defaultValue = "0") int page) {

        String url = "/Solicitudes/mis";

        UsuarioSesion usuario = sesionService.getUsuario(session);

        if (usuario == null) {
            return sesionService.redirigirALogin(session, url);
        }

        int pageSize = 10;
        List<Solicitud> todas = solicitudesService.getSolicitudesDeUsuario(usuario);
        int total = todas.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);

        List<Solicitud> pagina =
                from < total ? todas.subList(from, to) : java.util.Collections.emptyList();

        model.addAttribute("solicitudes", pagina);
        model.addAttribute("tituloListado", "Mis solicitudes");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

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