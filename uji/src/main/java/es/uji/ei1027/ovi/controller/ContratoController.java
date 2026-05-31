package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.ContratoDao;
import es.uji.ei1027.ovi.modelo.Contrato.Contrato;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

    @Autowired
    private ContratoDao contratoDao;

    // 1. Mostrar la pantalla para rellenar el contrato
    @GetMapping("/nuevo/{idSolicitud}/{idCandidato}")
    public String formularioContrato(@PathVariable int idSolicitud, @PathVariable int idCandidato, Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("idCandidato", idCandidato);
        model.addAttribute("idUsuarioOvi", usuario.getIdPersona()); // El que crea el contrato

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

        // Cuando termina, lo mandamos a su panel de control
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

        // Usamos el método que ya creamos en el DAO
        model.addAttribute("contratos", contratoDao.getContratosPorUsuario(usuario.getIdPersona()));

        return "contrato/mis_contratos";
    }
}
