package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import es.uji.ei1027.ovi.modelo.PaRequest.StatusPaRequest;
import es.uji.ei1027.ovi.modelo.Solicitud.CategoriaSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
        // Objeto para la tabla 'solicitud' (Gestión técnica)
        Solicitud solicitud = new Solicitud();
        solicitud.setPersonaSolicitante(id);
        solicitud.setCategoriaSolicitud(CategoriaSolicitud.Rol); // O 'Servicio' según prefieras
        solicitud.setTipoSolicitud(TipoSolicitud.Pa_request);
        solicitud.setEstadoSolicitud(EstadoSolicitud.Pendiente);
        solicitud.setFechaCreacion(LocalDate.now());

        // Objeto para la tabla 'pa_request' (Lógica de negocio)
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
            // 1. ASIGNAR VALORES OBLIGATORIOS (Esto evita el NullPointerException)
            LocalDate hoy = LocalDate.now();

            // Datos para PaRequest
            paRequest.setOviUser(id);
            paRequest.setFechaCreacion(hoy);
            paRequest.setStatus(StatusPaRequest.En_espera); // <--- ESTO ARREGLA EL ERROR

            // Datos para la Solicitud general
            solicitud.setPersonaSolicitante(id);
            solicitud.setFechaCreacion(hoy);
            solicitud.setEstadoSolicitud(EstadoSolicitud.Pendiente);
            solicitud.setCategoriaSolicitud(CategoriaSolicitud.Rol);
            solicitud.setTipoSolicitud(TipoSolicitud.Pa_request);

            // 2. GUARDAR EN BD (Ahora ya no fallará el DAO)
            paRequestDao.addPaRequest(paRequest);
            solicitudesDao.createSolicitud(solicitud);

            // 3. REDIRECCIÓN (Ahora sí llegará aquí porque no hay excepción)
            return "redirect:/";

        } catch (Exception e) {
            // Imprimimos el error para estar seguros
            System.out.println("ERROR AL GUARDAR: " + e.getMessage());
            e.printStackTrace();
            return "PaRequest/create";
        }
    }
}