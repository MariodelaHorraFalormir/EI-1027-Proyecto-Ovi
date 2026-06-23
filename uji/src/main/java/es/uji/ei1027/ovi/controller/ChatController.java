package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.MensajeDao;
import es.uji.ei1027.ovi.modelo.Chat.Mensaje;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private MensajeDao mensajeDao;

    // Añadimos esto para poder buscar la lista de chats
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setMensajeDao(MensajeDao mensajeDao) {
        this.mensajeDao = mensajeDao;
    }

    // Mostrar la pantalla del chat
    @GetMapping("/{idSolicitud}/{idReceptor}")
    public String mostrarChat(@PathVariable int idSolicitud, @PathVariable int idReceptor, Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        // Recuperamos los mensajes de esta solicitud
        List<Mensaje> conversacion = mensajeDao.getMensajesPorSolicitud(idSolicitud);

        model.addAttribute("conversacion", conversacion);
        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("idReceptor", idReceptor); // Con quién estamos hablando
        model.addAttribute("miId", usuario.getIdPersona());  // Mi ID para saber qué mensajes son míos (burbuja verde/gris)

        return "chat/conversacion"; // Esta será la vista HTML
    }

    // Enviar un mensaje
    @PostMapping("/enviar")
    public String enviarMensaje(
            @RequestParam int idSolicitud,
            @RequestParam int idReceptor,
            @RequestParam String contenido,
            HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        // Solo guardamos si el mensaje no está vacío
        if (contenido != null && !contenido.trim().isEmpty()) {
            Mensaje nuevoMensaje = new Mensaje();
            nuevoMensaje.setIdSolicitud(idSolicitud);
            nuevoMensaje.setIdEmisor(usuario.getIdPersona());
            nuevoMensaje.setIdReceptor(idReceptor);
            nuevoMensaje.setContenido(contenido);

            mensajeDao.addMensaje(nuevoMensaje);
        }

        // Recargamos la misma página del chat
        return "redirect:/chat/" + idSolicitud + "/" + idReceptor;
    }

    // --- NUEVO MÉTODO PARA VER LA LISTA DE CHATS ---
    @GetMapping("/mis-conversaciones")
    public String misConversaciones(Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int miId = usuario.getIdPersona();

        // Buscamos las conversaciones activas donde yo sea el emisor o el receptor
        String sql = "SELECT DISTINCT id_solicitud, " +
                "CASE WHEN id_emisor = ? THEN id_receptor ELSE id_emisor END as id_contacto " +
                "FROM mensaje WHERE id_emisor = ? OR id_receptor = ?";

        List<Map<String, Object>> chats = jdbcTemplate.queryForList(sql, miId, miId, miId);

        model.addAttribute("chats", chats);
        return "chat/lista_chats";
    }
}