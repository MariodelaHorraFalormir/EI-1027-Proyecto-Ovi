package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.MensajeDao;
import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.modelo.Chat.Mensaje;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
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

    @Autowired
    private PaRequestDao paRequestDao; // Añadido para buscar contexto

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

        // Recuperamos los mensajes
        List<Mensaje> conversacion = mensajeDao.getMensajesPorSolicitud(idSolicitud);

// CORRECCIÓN: Recuperamos el contexto de la AP para mostrarlo en la cabecera
        PaRequest paRequest = paRequestDao.getPaRequestById(idSolicitud);

        // Le pasamos el texto del tipo de asistencia a la vista
        String tipoAsistenciaStr = (paRequest != null && paRequest.getTipoAsistencia() != null)
                ? paRequest.getTipoAsistencia() // <--- ELIMINADO EL .getTexto()
                : "Asistencia General";

        model.addAttribute("conversacion", conversacion);
        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("idReceptor", idReceptor);
        model.addAttribute("miId", usuario.getIdPersona());
        model.addAttribute("tipoAsistencia", tipoAsistenciaStr); // Pasamos el contexto

        return "chat/conversacion";
    }

    // (El resto de tus métodos enviarMensaje y misConversaciones se quedan igual)
// ...

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
    public String misConversaciones(Model model, HttpSession session,
                                    @RequestParam(required = false) String filtro,
                                    @RequestParam(defaultValue = "0") int page) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int miId = usuario.getIdPersona();

        // 1. Consulta base (buscamos nuestras conversaciones)
        String sql = "SELECT DISTINCT id_solicitud, " +
                "CASE WHEN id_emisor = ? THEN id_receptor ELSE id_emisor END as id_contacto " +
                "FROM mensaje WHERE id_emisor = ? OR id_receptor = ?";

        List<Map<String, Object>> todosLosChats = jdbcTemplate.queryForList(sql, miId, miId, miId);

        // 2. Aplicar Filtro (Si el usuario ha escrito algo en el buscador)
        if (filtro != null && !filtro.trim().isEmpty()) {
            todosLosChats.removeIf(chat ->
                    !String.valueOf(chat.get("id_solicitud")).contains(filtro.trim()) &&
                            !String.valueOf(chat.get("id_contacto")).contains(filtro.trim())
            );
            model.addAttribute("filtroActual", filtro);
        }

        // 3. Paginación (Mostramos de 5 en 5 para que se note el efecto)
        int pageSize = 5;
        int total = todosLosChats.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);

        // Evitamos errores si la página está fuera de rango
        List<Map<String, Object>> chatsPaginados;
        if (from <= total) {
            chatsPaginados = todosLosChats.subList(from, to);
        } else {
            chatsPaginados = todosLosChats; // Fallback
        }

        model.addAttribute("chats", chatsPaginados);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "chat/lista_chats";
    }

    @GetMapping("/list/todas")
    public String todasLasConversaciones(Model model, HttpSession session,
                                         @RequestParam(defaultValue = "0") int page) {
        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        if (!usuario.esAdminOvi()) return "redirect:/";

        int pageSize = 10;
        List<Map<String, Object>> todas = mensajeDao.getTodasLasConversaciones();
        int total = todas.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Map<String, Object>> pagina = todas.subList(from, to);

        model.addAttribute("conversaciones", pagina);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "chat/list_todas";
    }
}