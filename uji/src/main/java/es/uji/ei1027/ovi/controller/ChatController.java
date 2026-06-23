package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.MensajeDao;
import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.modelo.Chat.Mensaje;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private MensajeDao mensajeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonaDao personaDao;

    @Autowired
    private PaRequestDao paRequestDao;

    @Autowired
    public void setMensajeDao(MensajeDao mensajeDao) {
        this.mensajeDao = mensajeDao;
    }

    // Mostrar la pantalla del chat
    @GetMapping("/{idSolicitud}/{idReceptor}")
    public String mostrarChat(@PathVariable int idSolicitud, @PathVariable int idReceptor, Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        List<Mensaje> conversacion = mensajeDao.getMensajesPorSolicitud(idSolicitud);

        // Invertimos la lista porque la vista usa flex-direction: column-reverse
        // (truco CSS-only para que el último mensaje aparezca anclado abajo sin JS).
        Collections.reverse(conversacion);

        // ----- Info contextual: con quién hablamos y a qué AP/solicitud se refiere -----
        try {
            Persona otra = personaDao.getPersona(idReceptor);
            if (otra != null) {
                model.addAttribute("nombreOtraPersona",
                        otra.getNombre() + " " + (otra.getApellidos() != null ? otra.getApellidos() : ""));
            }
        } catch (Exception e) {
            // Si no se puede recuperar, no mostramos nombre
        }

        try {
            PaRequest pr = paRequestDao.getPaRequestById(idSolicitud);
            if (pr != null && pr.getTipoAsistencia() != null) {
                model.addAttribute("tipoAsistencia", pr.getTipoAsistencia());
            }
        } catch (Exception e) {
            // Si no es una PaRequest directa, no mostramos tipo
        }

        model.addAttribute("conversacion", conversacion);
        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("idReceptor", idReceptor);
        model.addAttribute("miId", usuario.getIdPersona());

        return "chat/conversacion";
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

        if (contenido != null && !contenido.trim().isEmpty()) {
            Mensaje nuevoMensaje = new Mensaje();
            nuevoMensaje.setIdSolicitud(idSolicitud);
            nuevoMensaje.setIdEmisor(usuario.getIdPersona());
            nuevoMensaje.setIdReceptor(idReceptor);
            nuevoMensaje.setContenido(contenido);

            mensajeDao.addMensaje(nuevoMensaje);
        }

        return "redirect:/chat/" + idSolicitud + "/" + idReceptor;
    }

    // --- NUEVO MÉTODO PARA VER LA LISTA DE CHATS ---
    @GetMapping("/mis-conversaciones")
    public String misConversaciones(Model model, HttpSession session) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int miId = usuario.getIdPersona();

        String sql = "SELECT DISTINCT id_solicitud, " +
                "CASE WHEN id_emisor = ? THEN id_receptor ELSE id_emisor END as id_contacto " +
                "FROM mensaje WHERE id_emisor = ? OR id_receptor = ?";

        List<Map<String, Object>> chats = jdbcTemplate.queryForList(sql, miId, miId, miId);

        model.addAttribute("chats", chats);
        return "chat/lista_chats";
    }
}