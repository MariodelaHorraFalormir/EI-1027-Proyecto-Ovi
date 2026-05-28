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

@Controller
@RequestMapping("/PaRequest")
public class PaRequestController {

    private SolicitudesDao solicitudesDao;
    private PaRequestDao paRequestDao;

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
    public String listarPaRequests(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        model.addAttribute("paRequests", paRequestDao.getPaRequests());
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
}