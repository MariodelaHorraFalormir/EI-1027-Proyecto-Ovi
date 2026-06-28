package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.MensajeDao;
import es.uji.ei1027.ovi.modelo.Chat.Mensaje;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private MensajeDao mensajeDao;

    @Autowired
    public void setMensajeDao(MensajeDao mensajeDao) {
        this.mensajeDao = mensajeDao;
    }

    // Compatibilidad con enlaces antiguos: /chat/{idSolicitud}/{idReceptor}
    @GetMapping("/{idSolicitud}/{idReceptor}")
    public String abrirChatAntiguo(@PathVariable int idSolicitud,
                                   @PathVariable int idReceptor,
                                   HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int miId = usuario.getIdPersona();

        Integer idConversacion = mensajeDao.getIdConversacionPorSolicitudYParticipantes(
                idSolicitud,
                miId,
                idReceptor
        );

        if (idConversacion == null) {
            return "redirect:/chat/mis-conversaciones";
        }

        return "redirect:/chat/" + idConversacion;
    }

    // Abrir chat por conversación real
    @GetMapping("/{idConversacion}")
    public String mostrarChat(@PathVariable int idConversacion,
                              Model model,
                              HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int miId = usuario.getIdPersona();

        Map<String, Object> datosConversacion = mensajeDao.getConversacionById(idConversacion);

        if (datosConversacion == null) {
            return "redirect:/chat/mis-conversaciones";
        }

        boolean esAdmin = usuario.esAdminOvi();

        boolean puedeVer = esAdmin
                || mensajeDao.usuarioPerteneceAConversacion(idConversacion, miId);

        if (!puedeVer) {
            return "redirect:/";
        }

        List<Mensaje> mensajes = mensajeDao.getMensajesPorConversacion(idConversacion);

        Integer idSolicitud = ((Number) datosConversacion.get("id_solicitud")).intValue();
        Integer idOviUser = ((Number) datosConversacion.get("ovi_user")).intValue();
        Integer idPapPati = ((Number) datosConversacion.get("pap_pati")).intValue();

        Integer idReceptor = null;
        if (!esAdmin) {
            idReceptor = mensajeDao.getReceptorEnConversacion(idConversacion, miId);
        }

        String tipoAsistencia = datosConversacion.get("tipo_asistencia") != null
                ? String.valueOf(datosConversacion.get("tipo_asistencia"))
                : "Asistencia general";

        model.addAttribute("conversacion", mensajes);
        model.addAttribute("datosConversacion", datosConversacion);

        model.addAttribute("idConversacion", idConversacion);
        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("idReceptor", idReceptor);
        model.addAttribute("miId", miId);
        model.addAttribute("tipoAsistencia", tipoAsistencia);

        model.addAttribute("esAdmin", esAdmin);

        model.addAttribute("idOviUser", idOviUser);
        model.addAttribute("idPapPati", idPapPati);

        model.addAttribute("mailOviUser", datosConversacion.get("mail_ovi_user"));
        model.addAttribute("nombreOviUser", datosConversacion.get("nombre_ovi_user"));
        model.addAttribute("apellidosOviUser", datosConversacion.get("apellidos_ovi_user"));

        model.addAttribute("mailPapPati", datosConversacion.get("mail_pap_pati"));
        model.addAttribute("nombrePapPati", datosConversacion.get("nombre_pap_pati"));
        model.addAttribute("apellidosPapPati", datosConversacion.get("apellidos_pap_pati"));

        return "chat/conversacion";
    }

    // Enviar mensaje
    @PostMapping("/enviar")
    public String enviarMensaje(@RequestParam int idConversacion,
                                @RequestParam String contenido,
                                HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int miId = usuario.getIdPersona();

        Map<String, Object> datosConversacion = mensajeDao.getConversacionById(idConversacion);

        if (datosConversacion == null) {
            return "redirect:/chat/mis-conversaciones";
        }

        // El admin solo mira. No debe escribir en conversaciones ajenas.
        boolean puedeEnviar = mensajeDao.usuarioPerteneceAConversacion(idConversacion, miId);

        if (!puedeEnviar) {
            return "redirect:/";
        }

        if (contenido != null && !contenido.trim().isEmpty()) {
            Integer idReceptor = mensajeDao.getReceptorEnConversacion(idConversacion, miId);
            Integer idSolicitud = mensajeDao.getPaRequestDeConversacion(idConversacion);

            Mensaje nuevoMensaje = new Mensaje();
            nuevoMensaje.setConversacion(idConversacion);
            nuevoMensaje.setIdSolicitud(idSolicitud);
            nuevoMensaje.setIdEmisor(miId);
            nuevoMensaje.setIdReceptor(idReceptor);
            nuevoMensaje.setContenido(contenido.trim());

            mensajeDao.addMensaje(nuevoMensaje);
        }

        return "redirect:/chat/" + idConversacion;
    }

    // Crear o recuperar conversación desde candidatos
    @PostMapping("/iniciar")
    public String iniciarConversacion(@RequestParam int idSolicitud,
                                      @RequestParam int idPapPati,
                                      HttpSession session) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int miId = usuario.getIdPersona();

        Integer idOviUserSolicitud = mensajeDao.getOviUserDePaRequest(idSolicitud);

        if (idOviUserSolicitud == null) {
            return "redirect:/PaRequest/list";
        }

        if (idOviUserSolicitud != miId) {
            return "redirect:/";
        }

        Integer idConversacion = mensajeDao.crearORecuperarConversacion(
                idSolicitud,
                idOviUserSolicitud,
                idPapPati
        );

        if (idConversacion == null) {
            return "redirect:/PaRequest/candidatos/" + idSolicitud;
        }

        return "redirect:/chat/" + idConversacion;
    }

    // Mis conversaciones agrupadas por solicitud
    @GetMapping("/mis-conversaciones")
    public String misConversaciones(Model model,
                                    HttpSession session,
                                    @RequestParam(required = false) String filtro,
                                    @RequestParam(defaultValue = "0") int page) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        int miId = usuario.getIdPersona();

        List<Map<String, Object>> conversaciones = mensajeDao.getMisConversacionesAgrupadas(miId);

        if (filtro != null && !filtro.trim().isEmpty()) {
            String filtroNormalizado = filtro.trim().toLowerCase();

            conversaciones.removeIf(chat ->
                    !String.valueOf(chat.get("id_solicitud")).toLowerCase().contains(filtroNormalizado)
                            && !String.valueOf(chat.get("id_conversacion")).toLowerCase().contains(filtroNormalizado)
                            && !String.valueOf(chat.get("id_contacto")).toLowerCase().contains(filtroNormalizado)
                            && !String.valueOf(chat.get("tipo_asistencia")).toLowerCase().contains(filtroNormalizado)
            );

            model.addAttribute("filtroActual", filtro);
        }

        List<Map<String, Object>> grupos = agruparPorSolicitud(conversaciones);

        int pageSize = 5;
        int total = grupos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        if (page < 0) page = 0;
        if (page >= totalPages && totalPages > 0) page = totalPages - 1;

        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> gruposPaginados = total == 0
                ? new ArrayList<>()
                : grupos.subList(from, to);

        model.addAttribute("gruposSolicitudes", gruposPaginados);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "chat/lista_chats";
    }

    // Vista admin con filtros
    @GetMapping("/list/todas")
    public String todasLasConversaciones(Model model,
                                         HttpSession session,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(required = false) String filtroMail,
                                         @RequestParam(required = false) String tipoAsistencia,
                                         @RequestParam(required = false) String estado) {

        UsuarioSesion usuario = (UsuarioSesion) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        if (!usuario.esAdminOvi()) return "redirect:/";

        List<Map<String, Object>> conversaciones =
                mensajeDao.getTodasLasConversacionesFiltradas(filtroMail, tipoAsistencia, estado);

        List<Map<String, Object>> grupos = agruparPorSolicitud(conversaciones);

        int pageSize = 10;
        int total = grupos.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        if (page < 0) page = 0;
        if (page >= totalPages && totalPages > 0) page = totalPages - 1;

        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> pagina = total == 0
                ? new ArrayList<>()
                : grupos.subList(from, to);

        model.addAttribute("gruposSolicitudes", pagina);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("filtroMailActual", filtroMail);
        model.addAttribute("tipoAsistenciaActual", tipoAsistencia);
        model.addAttribute("estadoActual", estado);

        model.addAttribute("tiposAsistencia", mensajeDao.getTiposAsistenciaDisponibles());
        model.addAttribute("estadosPeticion", List.of("En espera", "En activo", "Caducada", "Finalizada"));

        return "chat/list_todas";
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> agruparPorSolicitud(List<Map<String, Object>> conversaciones) {
        Map<Object, Map<String, Object>> grupos = new LinkedHashMap<>();

        for (Map<String, Object> chat : conversaciones) {
            Object idSolicitud = chat.get("id_solicitud");

            if (!grupos.containsKey(idSolicitud)) {
                Map<String, Object> grupo = new LinkedHashMap<>();

                grupo.put("idSolicitud", idSolicitud);
                grupo.put("tipoAsistencia", chat.get("tipo_asistencia"));
                grupo.put("status", chat.get("status"));
                grupo.put("fechaInicio", chat.get("fecha_inicio"));
                grupo.put("fechaFin", chat.get("fecha_fin"));
                grupo.put("conversaciones", new ArrayList<Map<String, Object>>());

                grupos.put(idSolicitud, grupo);
            }

            List<Map<String, Object>> listaConversaciones =
                    (List<Map<String, Object>>) grupos.get(idSolicitud).get("conversaciones");

            listaConversaciones.add(chat);
        }

        return new ArrayList<>(grupos.values());
    }
}