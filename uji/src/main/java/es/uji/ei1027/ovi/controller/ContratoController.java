package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.ContratoDao;
import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Contrato.Contrato;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

    @Autowired
    private ContratoDao contratoDao;

    @Autowired
    private PersonaDao personaDao;

    @Autowired
    private PaRequestDao paRequestDao;

    // 1. Mostrar la pantalla para rellenar el contrato
    @GetMapping("/nuevo/{idSolicitud}/{idCandidato}")
    public String formularioContrato(@PathVariable int idSolicitud, @PathVariable int idCandidato, Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("idCandidato", idCandidato);
        model.addAttribute("idUsuarioOvi", usuario.getIdPersona());

        return "contrato/nuevo";
    }

    // 2. Guardar el contrato en la base de datos
    @PostMapping("/guardar")
    public String guardarContrato(@RequestParam int idSolicitud,
                                  @RequestParam int idUsuarioOvi,
                                  @RequestParam int idCandidato,
                                  @RequestParam String fechaInicio,
                                  @RequestParam(required = false) String fechaFin,
                                  HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        Contrato contrato = new Contrato();
        contrato.setIdSolicitud(idSolicitud);
        contrato.setIdUsuarioOvi(idUsuarioOvi);
        contrato.setIdPapPati(idCandidato);
        contrato.setFechaInicio(LocalDate.parse(fechaInicio));

        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            contrato.setFechaFin(LocalDate.parse(fechaFin));
        }

        contratoDao.addContrato(contrato);

        return "redirect:/contrato/exito";
    }

    @GetMapping("/exito")
    public String exitoContrato() {
        return "contrato/exito";
    }

    // 3. Ver mis contratos (Sirve tanto para OVI como para PAP/PATI)
    @GetMapping("/mis-contratos")
    public String misContratos(Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        List<Contrato> contratos = contratoDao.getContratosPorUsuario(usuario.getIdPersona());

        // Diccionarios auxiliares para que la vista pueda mostrar info contextual:
        //  - nombre de la "otra persona" en el contrato
        //  - tipo de asistencia (AP) que motivó el contrato
        Map<Integer, String> nombresPersona = new HashMap<>();
        Map<Integer, String> tipoAsistenciaPorSolicitud = new HashMap<>();

        for (Contrato c : contratos) {
            int idOtra = (c.getIdUsuarioOvi() == usuario.getIdPersona())
                    ? c.getIdPapPati()
                    : c.getIdUsuarioOvi();

            if (!nombresPersona.containsKey(idOtra)) {
                try {
                    Persona p = personaDao.getPersona(idOtra);
                    if (p != null) {
                        nombresPersona.put(idOtra,
                                p.getNombre() + " " + (p.getApellidos() != null ? p.getApellidos() : ""));
                    }
                } catch (Exception ignored) {}
            }

            if (!tipoAsistenciaPorSolicitud.containsKey(c.getIdSolicitud())) {
                try {
                    PaRequest pr = paRequestDao.getPaRequestById(c.getIdSolicitud());
                    if (pr != null && pr.getTipoAsistencia() != null) {
                        tipoAsistenciaPorSolicitud.put(c.getIdSolicitud(), pr.getTipoAsistencia());
                    }
                } catch (Exception ignored) {}
            }
        }

        model.addAttribute("contratos", contratos);
        model.addAttribute("nombresPersona", nombresPersona);
        model.addAttribute("tipoAsistenciaPorSolicitud", tipoAsistenciaPorSolicitud);
        model.addAttribute("miId", usuario.getIdPersona());

        return "contrato/mis_contratos";
    }
}