package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.SolicitudesService;
import es.uji.ei1027.ovi.Validadores.PaRequestValidator;
import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
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
    private PapPatiDao papPatiDao;
    private SolicitudesService solicitudesService;

    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {
        this.solicitudesDao = solicitudDao;
    }

    @Autowired
    public void setPaRequestDao(PaRequestDao paRequestDao) {
        this.paRequestDao = paRequestDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setSolicitudesService(SolicitudesService solicitudesService) {
        this.solicitudesService = solicitudesService;
    }

    @GetMapping("/create/{id}")
    public String mostrarFormularioRegistro(Model model,
                                            @PathVariable int id,
                                            HttpSession session) {

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
    public String procesarRegistro(@ModelAttribute("paRequest") PaRequest paRequest,
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
            System.out.println("ERROR AL GUARDAR PA REQUEST: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("paRequest", paRequest);
            model.addAttribute("solicitud", solicitud);

            return "PaRequest/create";
        }
    }

    @GetMapping("/mis/{id}")
    public String misProcesos(Model model,
                              @PathVariable int id,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "todos") String filtroEstado,
                              HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (usuario.getIdPersona() != id && !usuario.esAdminOvi()) {
            return "redirect:/";
        }

        filtroEstado = normalizarFiltroEstado(filtroEstado);

        List<PaRequest> todos = paRequestDao.getPaRequestsByOviUser(id);
        List<PaRequest> filtrados = filtrarPorEstado(todos, filtroEstado);

        cargarPaginacionProcesos(model, filtrados, page);

        model.addAttribute("idUsuario", id);
        model.addAttribute("filtroEstado", filtroEstado);

        return "PaRequest/mis";
    }

    @GetMapping("/misParticipadas/{id}")
    public String misParticipadas(Model model,
                                  @PathVariable int id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "todos") String filtroEstado,
                                  HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (usuario.getIdPersona() != id && !usuario.esAdminOvi()) {
            return "redirect:/";
        }

        filtroEstado = normalizarFiltroEstado(filtroEstado);

        List<PaRequest> todos = paRequestDao.getPaRequestsByPapPati(id);
        List<PaRequest> filtrados = filtrarPorEstado(todos, filtroEstado);

        cargarPaginacionProcesos(model, filtrados, page);

        model.addAttribute("idUsuario", id);
        model.addAttribute("filtroEstado", filtroEstado);

        return "PaRequest/misParticipadas";
    }

    @GetMapping("/list")
    public String listarPaRequests(Model model,
                                   HttpSession session,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "todos") String filtroEstado,
                                   @RequestParam(defaultValue = "") String busqueda) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!usuario.esAdminOvi()) {
            return "redirect:/";
        }

        filtroEstado = normalizarFiltroEstado(filtroEstado);

        StatusPaRequest estado = estadoDesdeFiltro(filtroEstado);

        List<PaRequest> filtrados = paRequestDao.getPaRequestsFiltrados(estado, busqueda);

        cargarPaginacionProcesos(model, filtrados, page);

        model.addAttribute("filtroEstado", filtroEstado);
        model.addAttribute("busqueda", busqueda);

        return "PaRequest/list";
    }

    @GetMapping("/detail/{id}")
    public String detallePaRequest(Model model,
                                   @PathVariable int id,
                                   HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        PaRequest paRequest = paRequestDao.getPaRequestById(id);

        if (paRequest == null) {
            return "redirect:/PaRequest/list";
        }

        model.addAttribute("paRequest", paRequest);
        model.addAttribute("usuarioActual", usuario);

        if (usuario.esAdminOvi()) {
            model.addAttribute(
                    "papPatisAsociados",
                    paRequestDao.getPapPatisAsociadosByPaRequest(id)
            );
        } else {
            model.addAttribute("papPatisAsociados", List.of());
        }

        return "PaRequest/detail";
    }

    @GetMapping("/update/{id}")
    public String mostrarFormularioUpdate(Model model,
                                          @PathVariable int id,
                                          HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        PaRequest paRequest = paRequestDao.getPaRequestById(id);

        if (paRequest == null) {
            return "redirect:/PaRequest/list";
        }

        model.addAttribute("paRequest", paRequest);

        return "PaRequest/update";
    }

    @PostMapping("/update/{id}")
    public String procesarUpdate(@ModelAttribute("paRequest") PaRequest paRequest,
                                 @PathVariable int id,
                                 HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        PaRequest original = paRequestDao.getPaRequestById(id);

        if (original == null) {
            return "redirect:/PaRequest/list";
        }

        paRequest.setId(id);

        if (paRequest.getFechaCreacion() == null) {
            paRequest.setFechaCreacion(original.getFechaCreacion());
        }

        if (paRequest.getOviUser() == 0) {
            paRequest.setOviUser(original.getOviUser());
        }

        paRequestDao.updatePaRequest(paRequest);

        return "redirect:/PaRequest/detail/" + id;
    }

    @GetMapping("/accept/{id}")
    public String aceptarPeticion(@PathVariable int id,
                                  Model model,
                                  HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        PaRequest paRequest = paRequestDao.getPaRequestById(id);

        if (paRequest == null) {
            return "redirect:/PaRequest/list";
        }

        paRequestDao.cambiarEstadoPaRequest(id, StatusPaRequest.En_activo);

        Solicitud solicitud = solicitudesDao.getSolicitudRolMasReciente(
                paRequest.getOviUser(),
                TipoSolicitud.Pa_request
        );

        if (solicitud != null) {
            UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
            int idTecnico = (usuario != null) ? usuario.getIdPersona() : 1;
            solicitudesDao.aprobarRapido(solicitud.getIdSolicitud(), idTecnico);
        }

        model.addAttribute("para", "Usuario solicitante (ID: " + paRequest.getOviUser() + ")");
        model.addAttribute("asunto", "Tu petición de asistencia ha sido aceptada");
        model.addAttribute("cuerpo", "Hola, te informamos de que el Técnico OVI ha revisado y aceptado tu petición de asistencia con número #" + id + ". Ya puedes acceder al sistema para ver los candidatos propuestos.");
        model.addAttribute("volverUrl", "/PaRequest/list");

        return "correo/simulacion";
    }

    @GetMapping("/reject/{id}")
    public String rechazarPeticion(@PathVariable int id,
                                   Model model,
                                   HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        PaRequest paRequest = paRequestDao.getPaRequestById(id);

        if (paRequest == null) {
            return "redirect:/PaRequest/list";
        }

        paRequestDao.cambiarEstadoPaRequest(id, StatusPaRequest.Finalizada);

        Solicitud solicitud = solicitudesDao.getSolicitudRolMasReciente(
                paRequest.getOviUser(),
                TipoSolicitud.Pa_request
        );

        if (solicitud != null) {
            UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
            int idTecnico = (usuario != null) ? usuario.getIdPersona() : 1;
            solicitudesDao.rechazarRapido(solicitud.getIdSolicitud(), idTecnico);
        }

        model.addAttribute("para", "Usuario solicitante (ID: " + paRequest.getOviUser() + ")");
        model.addAttribute("asunto", "Tu petición de asistencia ha sido rechazada");
        model.addAttribute("cuerpo", "Hola, lamentamos informarte de que tu petición #" + id + " ha sido rechazada tras la revisión del técnico. Ponte en contacto con la oficina para más detalles.");
        model.addAttribute("volverUrl", "/PaRequest/list");

        return "correo/simulacion";
    }

    @GetMapping("/candidatos/{idSolicitud}")
    public String verCandidatos(@PathVariable int idSolicitud,
                                Model model,
                                HttpSession session,
                                @RequestParam(defaultValue = "0") int page) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        int pageSize = 10;

        List<Map<String, Object>> todos = papPatiDao.getCandidatosDisponibles();

        int total = todos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        if (totalPages == 0) {
            page = 0;
        } else if (page < 0) {
            page = 0;
        } else if (page >= totalPages) {
            page = totalPages - 1;
        }

        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> pagina;

        if (total == 0) {
            pagina = List.of();
        } else {
            pagina = todos.subList(from, to);
        }

        model.addAttribute("candidatos", pagina);
        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "PaRequest/candidatos";
    }

    @GetMapping("/detalles/{id}")
    public String verDetallesYEditar(@PathVariable int id,
                                     Model model,
                                     HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        PaRequest paRequest = paRequestDao.getPaRequestById(id);

        if (paRequest == null) {
            return "redirect:/PaRequest/mis/" + usuario.getIdPersona();
        }

        model.addAttribute("paRequest", paRequest);

        List<Solicitud> solicitudes = solicitudesService.getSolicitudesDeUsuario(usuario);

        String notasTecnico = "No hay anotaciones adicionales registradas para este proceso.";

        for (Solicitud sol : solicitudes) {
            if (sol.getTipoSolicitud() != null
                    && sol.getTipoSolicitud().name().equalsIgnoreCase("Pa_request")) {

                if (sol.getMotivoResolucion() != null
                        && !sol.getMotivoResolucion().trim().isEmpty()) {
                    notasTecnico = sol.getMotivoResolucion();
                }

                break;
            }
        }

        model.addAttribute("comentariosTecnico", notasTecnico);

        return "paRequest/detalles";
    }

    @PostMapping("/detalles/guardar/{id}")
    public String guardarModificacionSolicitud(@PathVariable int id,
                                               @ModelAttribute("paRequest") PaRequest paRequestUpdate,
                                               HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        PaRequest paRequestOriginal = paRequestDao.getPaRequestById(id);

        if (paRequestOriginal != null) {
            paRequestOriginal.setTipoAsistencia(paRequestUpdate.getTipoAsistencia());
            paRequestOriginal.setZonaGeografica(paRequestUpdate.getZonaGeografica());
            paRequestOriginal.setDisponibilidadHoraria(paRequestUpdate.getDisponibilidadHoraria());
            paRequestOriginal.setGeneroAsistente(paRequestUpdate.getGeneroAsistente());
            paRequestOriginal.setPreferencias(paRequestUpdate.getPreferencias());

            paRequestOriginal.setStatus(StatusPaRequest.En_espera);

            paRequestDao.updatePaRequest(paRequestOriginal);
        }

        return "redirect:/PaRequest/mis/" + usuario.getIdPersona();
    }

    private String normalizarFiltroEstado(String filtroEstado) {
        if (filtroEstado == null) {
            return "todos";
        }

        switch (filtroEstado.toLowerCase()) {
            case "todos":
            case "en_espera":
            case "en_activo":
            case "caducada":
            case "finalizada":
                return filtroEstado.toLowerCase();
            default:
                return "todos";
        }
    }

    private StatusPaRequest estadoDesdeFiltro(String filtroEstado) {
        if (filtroEstado == null) {
            return null;
        }

        switch (filtroEstado.toLowerCase()) {
            case "en_espera":
                return StatusPaRequest.En_espera;
            case "en_activo":
                return StatusPaRequest.En_activo;
            case "caducada":
                return StatusPaRequest.Caducada;
            case "finalizada":
                return StatusPaRequest.Finalizada;
            default:
                return null;
        }
    }

    private List<PaRequest> filtrarPorEstado(List<PaRequest> procesos,
                                             String filtroEstado) {

        StatusPaRequest estado = estadoDesdeFiltro(filtroEstado);

        if (estado == null) {
            return procesos;
        }

        return procesos.stream()
                .filter(paRequest -> paRequest.getStatus() == estado)
                .toList();
    }

    private void cargarPaginacionProcesos(Model model,
                                          List<PaRequest> procesos,
                                          int page) {

        int pageSize = 10;

        int total = procesos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        if (totalPages == 0) {
            page = 0;
        } else if (page < 0) {
            page = 0;
        } else if (page >= totalPages) {
            page = totalPages - 1;
        }

        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);

        List<PaRequest> pagina;

        if (total == 0) {
            pagina = List.of();
        } else {
            pagina = procesos.subList(from, to);
        }

        model.addAttribute("paRequests", pagina);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalProcesos", total);
    }
}