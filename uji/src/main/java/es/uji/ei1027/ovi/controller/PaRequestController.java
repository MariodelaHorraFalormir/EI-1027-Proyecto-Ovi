package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
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
    public void setSolicitudDao(SolicitudesDao solicitudDao) {
        this.solicitudesDao = solicitudDao;
    }

    @Autowired
    public void setPaRequestDao(PaRequestDao paRequestDao) {
        this.paRequestDao = paRequestDao;
    }

    @GetMapping("/create/{id}")
    public String mostrarFormularioRegistro(Model model, @PathVariable int id) {
        Solicitud solicitud = new Solicitud();
        solicitud.setPersonaSolicitante(id);
        solicitud.setCategoriaSolicitud(CategoriaSolicitud.Proceso); // O 'Servicio' según prefieras
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
            @ModelAttribute("solicitud") Solicitud solicitud,
            BindingResult bindingResult,
            @PathVariable int id) {

        if (bindingResult.hasErrors()) {
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

// --- MÉTODOS PARA EL TÉCNICO: ACEPTAR Y RECHAZAR ---

    @GetMapping("/accept/{id}")
    public String aceptarPeticion(@PathVariable int id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        // 1. Buscamos la petición y la actualizamos
        PaRequest paRequest = paRequestDao.getPaRequestById(id);
        if (paRequest != null) {
            // CORREÇÃO: Usamos "En_activo" porque é o que existe no StatusPaRequest
            paRequest.setStatus(StatusPaRequest.En_activo);
            paRequestDao.updatePaRequest(paRequest);
        }

        // 2. Preparamos los datos para la pantalla del correo falso
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
            // CORREÇÃO: Usamos "Finalizada" para simular que foi rejeitada e encerrada
            paRequest.setStatus(StatusPaRequest.Finalizada);
            paRequestDao.updatePaRequest(paRequest);
        }

        model.addAttribute("para", "Usuario solicitante (ID: " + paRequest.getOviUser() + ")");
        model.addAttribute("asunto", "❌ Tu Petición de Asistencia ha sido RECHAZADA");
        model.addAttribute("cuerpo", "Hola, lamentamos informarte de que tu petición #" + id + " ha sido rechazada tras la revisión del técnico. Ponte en contacto con la oficina para más detalles.");
        model.addAttribute("volverUrl", "/PaRequest/list");

        return "correo/simulacion";
    }

    @GetMapping("/candidatos/{idSolicitud}")
    public String verCandidatos(@PathVariable int idSolicitud, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        // Obtenemos la lista de candidatos usando el método que acabas de crear
        List<Map<String, Object>> candidatos = PapPatiDao.getCandidatosDisponibles();

        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idSolicitud", idSolicitud);

        return "PaRequest/candidatos"; // Nos lleva a la vista HTML
    }
}