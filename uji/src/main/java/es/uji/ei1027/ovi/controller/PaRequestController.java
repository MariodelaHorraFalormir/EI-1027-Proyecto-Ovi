package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.SolicitudesService;
import es.uji.ei1027.ovi.Validadores.PaRequestValidator;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/PaRequest")
public class PaRequestController {

    private SolicitudesDao solicitudesDao;
    private PaRequestDao paRequestDao;

    @Autowired
    private es.uji.ei1027.ovi.dao.PapPatiDao PapPatiDao;

    @Autowired
    private SolicitudesService solicitudesService;

    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {
        this.solicitudesDao = solicitudDao;
    }

    @Autowired
    public void setPaRequestDao(PaRequestDao paRequestDao) {
        this.paRequestDao = paRequestDao;
    }

    @GetMapping("/create/{id}")
    public String mostrarFormularioRegistro(Model model, @PathVariable int id, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (usuario.getRolesActivos() != null &&
                usuario.getRolesActivos().contains("PAP_PATI") &&
                !usuario.getRolesActivos().contains("OVI_USER")) {
            return "redirect:/";
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setPersonaSolicitante(id);
        solicitud.setCategoriaSolicitud(CategoriaSolicitud.Proceso);
        solicitud.setTipoSolicitud(TipoSolicitud.Pa_request);
        solicitud.setEstadoSolicitud(EstadoSolicitud.Pendiente);
        solicitud.setFechaCreacion(LocalDate.now());

        PaRequest paRequest = new PaRequest();
        paRequest.setOviUser(id);
        paRequest.setStatus(StatusPaRequest.En_espera);
        paRequest.setFechaCreacion(LocalDate.now());

        model.addAttribute("paRequest", paRequest);
        model.addAttribute("solicitud", solicitud);

        return "PaRequest/create";
    }

    @PostMapping("/create/{id}")
    public String procesarRegistro(
            @ModelAttribute("paRequest") PaRequest paRequest,
            BindingResult bindingResultPaRequest,
            @ModelAttribute("solicitud") Solicitud solicitud,
            BindingResult bindingResultSolicitud,
            @PathVariable int id,
            Model model) {

        PaRequestValidator validador = new PaRequestValidator();
        validador.validate(paRequest, bindingResultPaRequest);

        if (bindingResultPaRequest.hasErrors() || bindingResultSolicitud.hasErrors()) {
            model.addAttribute("paRequest", paRequest);
            model.addAttribute("solicitud", solicitud);
            return "PaRequest/create";
        }

        try {
            LocalDate hoy = LocalDate.now();

            paRequest.setOviUser(id);
            paRequest.setFechaCreacion(hoy);
            paRequest.setStatus(StatusPaRequest.En_espera);

            solicitud.setPersonaSolicitante(id);
            solicitud.setFechaCreacion(hoy);
            solicitud.setEstadoSolicitud(EstadoSolicitud.Pendiente);
            solicitud.setCategoriaSolicitud(CategoriaSolicitud.Proceso);
            solicitud.setTipoSolicitud(TipoSolicitud.Pa_request);

            paRequestDao.addPaRequest(paRequest);
            solicitudesDao.createSolicitud(solicitud);

            return "redirect:/PaRequest/mis/" + id;

        } catch (Exception e) {
            System.out.println("ERROR AL GUARDAR: " + e.getMessage());
            e.printStackTrace();
            return "PaRequest/create";
        }
    }

    @GetMapping("/mis/{id}")
    public String misProcesos(Model model, @PathVariable int id,
                              @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        List<PaRequest> todos = paRequestDao.getPaRequestsByOviUser(id);
        int total = todos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        List<PaRequest> pagina = todos.subList(from, to);

        model.addAttribute("paRequests", pagina);
        model.addAttribute("idUsuario", id);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "PaRequest/mis";
    }

    @GetMapping("/misParticipadas/{id}")
    public String misParticipadas(Model model, @PathVariable int id,
                                  @RequestParam(defaultValue = "0") int page,
                                  HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        // Solo el propio PAP/PATI puede ver su lista (o un admin OVI)
        if (usuario.getIdPersona() != id && !usuario.esAdminOvi()) {
            return "redirect:/";
        }

        int pageSize = 10;
        List<PaRequest> todos = paRequestDao.getPaRequestsByPapPati(id);
        int total = todos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        List<PaRequest> pagina = todos.subList(from, to);

        model.addAttribute("paRequests", pagina);
        model.addAttribute("idUsuario", id);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "PaRequest/misParticipadas";
    }

    @GetMapping("/list")
    public String listarPaRequests(Model model, HttpSession session,
                                   @RequestParam(defaultValue = "0") int page) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        int pageSize = 10;
        List<PaRequest> todos = paRequestDao.getPaRequests();
        int total = todos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        List<PaRequest> pagina = todos.subList(from, to);

        model.addAttribute("paRequests", pagina);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "PaRequest/list";
    }

    @GetMapping("/detail/{id}")
    public String detallePaRequest(Model model, @PathVariable int id, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        PaRequest paRequest = paRequestDao.getPaRequestById(id);
        if (paRequest == null) return "redirect:/PaRequest/list";
        model.addAttribute("paRequest", paRequest);
        model.addAttribute("papPatisAsociados", PapPatiDao.getPapPatisByPaRequest(id));
        return "PaRequest/detail";
    }

    @GetMapping("/update/{id}")
    public String mostrarFormularioUpdate(Model model, @PathVariable int id, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        PaRequest paRequest = paRequestDao.getPaRequestById(id);
        if (paRequest == null) return "redirect:/PaRequest/list";
        model.addAttribute("paRequest", paRequest);
        return "PaRequest/update";
    }

    @PostMapping("/update/{id}")
    public String procesarUpdate(
            @ModelAttribute("paRequest") PaRequest paRequest,
            @PathVariable int id,
            HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        paRequest.setId(id);
        paRequestDao.updatePaRequest(paRequest);
        return "redirect:/PaRequest/detail/" + id;
    }

    @GetMapping("/accept/{id}")
    public String aceptarPeticion(@PathVariable int id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        PaRequest paRequest = paRequestDao.getPaRequestById(id);
        if (paRequest != null) {
            paRequestDao.cambiarEstadoPaRequest(id, StatusPaRequest.En_activo);

            Solicitud solicitud = solicitudesDao.getSolicitudRolMasReciente(paRequest.getOviUser(), TipoSolicitud.Pa_request);
            if (solicitud != null) {
                UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
                int idTecnico = (usuario != null) ? usuario.getIdPersona() : 1;
                solicitudesDao.aprobarRapido(solicitud.getIdSolicitud(), idTecnico);
            }
        }

        model.addAttribute("para", "Usuario solicitante (ID: " + paRequest.getOviUser() + ")");
        model.addAttribute("asunto", "✅ Tu Petición de Asistencia ha sido ACEPTADA");
        model.addAttribute("cuerpo", "Hola, te informamos de que el Técnico OVI ha revisado y aceptado tu petición de asistencia con número #" + id + ". Ya puedes acceder al sistema para ver los candidatos propuestos.");
        model.addAttribute("volverUrl", "/PaRequest/list");

        return "correo/simulacion";
    }

    @GetMapping("/reject/{id}")
    public String rechazarPeticion(@PathVariable int id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        PaRequest paRequest = paRequestDao.getPaRequestById(id);
        if (paRequest != null) {
            paRequestDao.cambiarEstadoPaRequest(id, StatusPaRequest.Finalizada);

            Solicitud solicitud = solicitudesDao.getSolicitudRolMasReciente(paRequest.getOviUser(), TipoSolicitud.Pa_request);
            if (solicitud != null) {
                UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
                int idTecnico = (usuario != null) ? usuario.getIdPersona() : 1;
                solicitudesDao.rechazarRapido(solicitud.getIdSolicitud(), idTecnico);
            }
        }

        model.addAttribute("para", "Usuario solicitante (ID: " + paRequest.getOviUser() + ")");
        model.addAttribute("asunto", "❌ Tu Petición de Asistencia ha sido RECHAZADA");
        model.addAttribute("cuerpo", "Hola, lamentamos informarte de que tu petición #" + id + " ha sido rechazada tras la revisión del técnico. Ponte en contacto con la oficina para más detalles.");
        model.addAttribute("volverUrl", "/PaRequest/list");

        return "correo/simulacion";
    }

    @GetMapping("/candidatos/{idSolicitud}")
    public String verCandidatos(@PathVariable int idSolicitud, Model model, HttpSession session,
                                @RequestParam(defaultValue = "0") int page) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        int pageSize = 10;
        List<Map<String, Object>> todos = PapPatiDao.getCandidatosDisponibles();
        int total = todos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Map<String, Object>> pagina = (from <= to) ? todos.subList(from, to) : java.util.Collections.emptyList();

        model.addAttribute("candidatos", pagina);
        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "PaRequest/candidatos";
    }

    @GetMapping("/detalles/{id}")
    public String verDetallesYEditar(@PathVariable int id, Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        PaRequest paRequest = paRequestDao.getPaRequestById(id);
        if (paRequest == null) return "redirect:/PaRequest/mis/" + usuario.getIdPersona();

        model.addAttribute("paRequest", paRequest);

        // 1. Buscamos todas las solicitudes de este usuario
        java.util.List<es.uji.ei1027.ovi.modelo.Solicitud.Solicitud> solicitudes = solicitudesService.getSolicitudesDeUsuario(usuario);

        String notasTecnico = "No hay anotaciones adicionales registradas para este proceso.";

        // 2. Filtramos para encontrar la de Asistencia Personal y sacamos el mensaje real
        for (es.uji.ei1027.ovi.modelo.Solicitud.Solicitud sol : solicitudes) {
            if (sol.getTipoSolicitud() != null && sol.getTipoSolicitud().name().equalsIgnoreCase("Pa_request")) {

                // ¡OJO AQUÍ! Cambia getMensajeResolucion() por el getter que uséis en vuestro modelo Solicitud
                if (sol.getMotivoResolucion() != null && !sol.getMotivoResolucion().trim().isEmpty()) {
                    notasTecnico = sol.getMotivoResolucion();
                }
                break; // Encontramos la solicitud, paramos de buscar
            }
        }

        // 3. Enviamos el mensaje real a la vista
        model.addAttribute("comentariosTecnico", notasTecnico);

        return "paRequest/detalles";
    }

    @PostMapping("/detalles/guardar/{id}")
    public String guardarModificacionSolicitud(@PathVariable int id, @ModelAttribute("paRequest") PaRequest paRequestUpdate, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        // 1. Recuperamos el proceso original de la base de datos
        PaRequest paRequestOriginal = paRequestDao.getPaRequestById(id);

        if (paRequestOriginal != null) {
            // 2. Actualizamos los campos que el usuario ha podido modificar en el formulario
            paRequestOriginal.setTipoAsistencia(paRequestUpdate.getTipoAsistencia());
            paRequestOriginal.setZonaGeografica(paRequestUpdate.getZonaGeografica());
            paRequestOriginal.setDisponibilidadHoraria(paRequestUpdate.getDisponibilidadHoraria());
            paRequestOriginal.setGeneroAsistente(paRequestUpdate.getGeneroAsistente());
            paRequestOriginal.setPreferencias(paRequestUpdate.getPreferencias());

            // 3. Importante: Al modificarla, el estado vuelve a ser "En espera" para que el técnico la revise de nuevo
            paRequestOriginal.setStatus(StatusPaRequest.En_espera);

            paRequestDao.updatePaRequest(paRequestOriginal);

            // Aquí también deberías actualizar el estado de la Solicitud general a "Pendiente" si fuera necesario
        }

        return "redirect:/PaRequest/mis/" + usuario.getIdPersona();
    }
}