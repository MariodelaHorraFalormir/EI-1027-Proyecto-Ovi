package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstadisticasController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/admin/estadisticas")
    public String verEstadisticas(Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");

        // Protegemos la ruta: solo administradores/técnicos pueden entrar
        if (usuario == null || !usuario.esAdminOvi()) {
            return "redirect:/login";
        }

        try {
            // Hacemos consultas rápidas a la base de datos para contar cosas
            Integer totalUsuarios = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM persona", Integer.class);
            Integer totalContratos = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM contrato", Integer.class);
            Integer contratosActivos = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM contrato WHERE estado = 'Activo'", Integer.class);
            Integer totalMensajes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mensaje", Integer.class);

            model.addAttribute("totalUsuarios", totalUsuarios != null ? totalUsuarios : 0);
            model.addAttribute("totalContratos", totalContratos != null ? totalContratos : 0);
            model.addAttribute("contratosActivos", contratosActivos != null ? contratosActivos : 0);
            model.addAttribute("totalMensajes", totalMensajes != null ? totalMensajes : 0);

        } catch (Exception e) {
            // Si alguna tabla aún no tiene datos o falla, enviamos ceros para que no pete
            model.addAttribute("totalUsuarios", 0);
            model.addAttribute("totalContratos", 0);
            model.addAttribute("contratosActivos", 0);
            model.addAttribute("totalMensajes", 0);
        }

        return "admin/estadisticas";
    }
}