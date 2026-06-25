package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.ContratoDao;
import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.modelo.Contrato.Contrato;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.PaRequest.StatusPaRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

    @Autowired
    private ContratoDao contratoDao;

    @Autowired
    private PaRequestDao paRequestDao; // Inyectamos para poder cerrar la solicitud automáticamente

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

    // 2. Guardar el contrato en la base de datos y cerrar la solicitud
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

        // Guardamos el contrato de forma normal
        contratoDao.addContrato(contrato);

        // ¡CLAVE CORRECCIÓN!: Al crearse el contrato, cerramos de forma automática la PaRequest
        // Así pasa de 'En proceso' a 'Finalizada' y se evita que se dupliquen contratos para la misma solicitud
        paRequestDao.cambiarEstadoPaRequest(idSolicitud, StatusPaRequest.Finalizada);

        return "redirect:/contrato/exito";
    }

    @GetMapping("/exito")
    public String exitoContrato() {
        return "contrato/exito";
    }

    // 3. Ver mis contratos (Sirve tanto para OVI como para PAP/PATI)
    @GetMapping("/mis-contratos")
    public String misContratos(Model model, HttpSession session,
                               @RequestParam(defaultValue = "0") int page) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int pageSize = 10;
        List<Contrato> todos = contratoDao.getContratosPorUsuario(usuario.getIdPersona());
        int total = todos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Contrato> pagina = (from <= to) ? todos.subList(from, to) : java.util.Collections.emptyList();

        model.addAttribute("contratos", pagina);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "contrato/mis_contratos";
    }

    @GetMapping("/list/todos")
    public String todosLosContratos(Model model, HttpSession session,
                                    @RequestParam(defaultValue = "0") int page) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        if (!usuario.esAdminOvi()) return "redirect:/";

        int pageSize = 10;
        List<Contrato> todos = contratoDao.getTodosLosContratos();
        int total = todos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Contrato> pagina = todos.subList(from, to);

        model.addAttribute("contratos", pagina);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "contrato/list_todos";
    }

    // 4. CORRECCIÓN: Mostrar formulario para editar un contrato existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        Contrato contrato = contratoDao.getContratoPorId(id);
        if (contrato == null) return "redirect:/contrato/mis-contratos";

        model.addAttribute("contrato", contrato);
        return "contrato/editar";
    }

    // 5. CORRECCIÓN: Procesar la edición del contrato
    @PostMapping("/editar/{id}")
    public String procesarEditar(@PathVariable int id,
                                 @ModelAttribute("contrato") Contrato contrato,
                                 HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        contrato.setId(id);
        contratoDao.updateContrato(contrato);

        return "redirect:/contrato/mis-contratos";
    }
}