package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.PaRequest.StatusPaRequest;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Solicitud.CategoriaSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudesService {

    @Autowired
    private SolicitudesDao solicitudesDao;

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private PaRequestDao paRequestDao;

    @Transactional
    public void updateSolicitud(int idOriginal, Solicitud solicitudNueva , UsuarioSesion admin) {

        if (!puedeGestionarSolicitudes(admin)) {
            throw new IllegalArgumentException("No tienes permisos para gestionar solicitudes");
        }

        Solicitud solicitudAntigua = solicitudesDao.getSolicitudById(idOriginal);

        if (solicitudAntigua == null) {
            throw new IllegalArgumentException("No existe la solicitud con id " + idOriginal);
        }

        prepararResolucion(solicitudNueva,admin);

        solicitudesDao.updateSolicitud(idOriginal, solicitudNueva);

        aplicarCambioSiProcede(solicitudAntigua, solicitudNueva);
    }

    @Transactional
    public void aprobarRapido(int idOriginal, UsuarioSesion admin) {
        if (!puedeGestionarSolicitudes(admin)) {
            throw new IllegalArgumentException("No tienes permisos para gestionar solicitudes");
        }
        Solicitud solicitudAntigua = solicitudesDao.getSolicitudById(idOriginal);

        if (solicitudAntigua == null) {
            throw new IllegalArgumentException("No existe la solicitud con id " + idOriginal);
        }

        solicitudesDao.aprobarRapido(idOriginal,admin.getIdPersona());

        if (solicitudAntigua.getEstadoSolicitud() != EstadoSolicitud.Aprobada) {
            aplicarAprobacion(solicitudAntigua);
        }
    }

    @Transactional
    public void rechazarRapido(int idOriginal , UsuarioSesion admin) {
        if (!puedeGestionarSolicitudes(admin)) {
            throw new IllegalArgumentException("No tienes permisos para gestionar solicitudes");
        }
        Solicitud solicitudAntigua = solicitudesDao.getSolicitudById(idOriginal);

        if (solicitudAntigua == null) {
            throw new IllegalArgumentException("No existe la solicitud con id " + idOriginal);
        }

        solicitudesDao.rechazarRapido(idOriginal , admin.getIdPersona());

        if (solicitudAntigua.getEstadoSolicitud() != EstadoSolicitud.Rechazada) {
            aplicarRechazo(solicitudAntigua);
        }
    }

    private void prepararResolucion(Solicitud solicitud , UsuarioSesion admin) {

        if (solicitud.getEstadoSolicitud() == EstadoSolicitud.Aprobada ||
                solicitud.getEstadoSolicitud() == EstadoSolicitud.Rechazada) {

            solicitud.setFechaResolucion(LocalDate.now());

            // Temporal hasta tener login
            solicitud.setTecnicoRevisor(1);
            solicitud.setTecnicoRevisor(admin.getIdPersona());
        }

        if (solicitud.getEstadoSolicitud() == EstadoSolicitud.Rechazada &&
                (solicitud.getMotivoResolucion() == null || solicitud.getMotivoResolucion().isBlank())) {

            solicitud.setMotivoResolucion("Solicitud rechazada");
        }
    }

    private void aplicarCambioSiProcede(Solicitud antigua, Solicitud nueva) {

        EstadoSolicitud estadoAntiguo = antigua.getEstadoSolicitud();
        EstadoSolicitud estadoNuevo = nueva.getEstadoSolicitud();

        if (estadoAntiguo == estadoNuevo) {
            return;
        }

        if (estadoNuevo == EstadoSolicitud.Aprobada) {
            aplicarAprobacion(nueva);
        } else if (estadoNuevo == EstadoSolicitud.Rechazada) {
            aplicarRechazo(nueva);
        }
    }

    private void aplicarAprobacion(Solicitud solicitud) {

        switch (solicitud.getTipoSolicitud()) {

            case Ovi_user -> oviUserDao.cambiarEstadoRol(
                    solicitud.getPersonaSolicitante(),
                    EstadoRol.Activo
            );

            case Pap_pati -> papPatiDao.cambiarEstadoRol(
                    solicitud.getPersonaSolicitante(),
                    EstadoRol.Activo
            );

            case Pa_request -> paRequestDao.cambiarEstadoPaRequest(
                    solicitud.getPersonaSolicitante(),
                    StatusPaRequest.En_activo
            );

            default -> {
                // Otros tipos de solicitud no modifican roles/procesos todavía
            }
        }
    }
    public Solicitud solicitudRol(int id , TipoSolicitud tipoSolicitud) {
        Solicitud solicitud = new Solicitud();
        solicitud.setPersonaSolicitante(id);
        solicitud.setCategoriaSolicitud(CategoriaSolicitud.Rol);
        solicitud.setTipoSolicitud(tipoSolicitud);
        solicitud.setEstadoSolicitud(EstadoSolicitud.Pendiente);
        return solicitud;
    }

    private void aplicarRechazo(Solicitud solicitud) {

        switch (solicitud.getTipoSolicitud()) {

            case Ovi_user -> oviUserDao.cambiarEstadoRol(
                    solicitud.getPersonaSolicitante(),
                    EstadoRol.Rechazado
            );

            case Pap_pati -> papPatiDao.cambiarEstadoRol(
                    solicitud.getPersonaSolicitante(),
                    EstadoRol.Rechazado
            );

            case Pa_request -> paRequestDao.cambiarEstadoPaRequest(
                    solicitud.getPersonaSolicitante(),
                    StatusPaRequest.Caducada
            );

            default -> {
                // Otros tipos de solicitud no modifican roles/procesos todavía
            }
        }
    }
    public List<Solicitud> getSolicitudesPorTipo(String tipo) {
        return switch (tipo) {
            case "todas" -> solicitudesDao.getSolicitudesOrderId();
            case "ovi-users" -> solicitudesDao.getSolicitudesPorTipo(TipoSolicitud.Ovi_user);
            case "pap-patis" -> solicitudesDao.getSolicitudesPorTipo(TipoSolicitud.Pap_pati);
            case "pa-requests" -> solicitudesDao.getSolicitudesPorTipo(TipoSolicitud.Pa_request);
                case "pendientes" -> solicitudesDao.getSolicitudesPendientes();
            default -> throw new IllegalArgumentException("Tipo de listado de solicitudes no válido: " + tipo);
        };
    }

    public String getTituloListado(String tipo) {
        return switch (tipo) {
            case "todas" -> "Todas las solicitudes";
            case "ovi-users" -> "Solicitudes de OVI User";
            case "pap-patis" -> "Solicitudes de PAP/PATI";
            case "pa-requests" -> "Solicitudes de procesos PA Request";
            default -> "Listado de solicitudes";
        };
    }
    public List<Solicitud> getSolicitudesDeUsuario(UsuarioSesion usuario) {
        if (usuario == null) {
            return List.of();
        }

        return solicitudesDao.getSolicitudesPorPersona(usuario.getIdPersona());
    }

    public boolean puedeVerSolicitud(UsuarioSesion usuario, Solicitud solicitud) {
        if (usuario == null || solicitud == null) {
            return false;
        }

        return puedeGestionarSolicitudes(usuario)
                || solicitud.getPersonaSolicitante() == usuario.getIdPersona();
    }

    public boolean puedeGestionarSolicitudes(UsuarioSesion usuario) {
        return usuario != null && usuario.esAdminOvi();
    }
    @Transactional
    public void solicitarRevision(int idSolicitud, UsuarioSesion usuario, String mensajeRevision) {

        if (usuario == null) {
            throw new IllegalArgumentException("No hay usuario en sesión");
        }

        Solicitud solicitud = solicitudesDao.getSolicitudById(idSolicitud);

        if (solicitud == null) {
            throw new IllegalArgumentException("No existe la solicitud con id " + idSolicitud);
        }

        boolean esSolicitante = solicitud.getPersonaSolicitante() == usuario.getIdPersona();
        boolean esAdmin = puedeGestionarSolicitudes(usuario);

        if (!esSolicitante && !esAdmin) {
            throw new IllegalArgumentException("No puedes solicitar revisión de esta solicitud");
        }

        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.Rechazada) {
            throw new IllegalStateException("Solo se puede solicitar revisión de solicitudes rechazadas");
        }

        if (mensajeRevision == null || mensajeRevision.isBlank()) {
            if (esAdmin) {
                mensajeRevision = "Un administrador ha reabierto la solicitud para una nueva revisión.";
            } else {
                mensajeRevision = "El usuario solicita una nueva revisión tras realizar los cambios.";
            }
        }

        int filasActualizadas = solicitudesDao.solicitarRevision(idSolicitud, mensajeRevision);

        if (filasActualizadas == 0) {
            throw new IllegalStateException("No se ha podido solicitar la revisión");
        }

        aplicarRevision(solicitud);
    }
    private void aplicarRevision(Solicitud solicitud) {

        switch (solicitud.getTipoSolicitud()) {

            case Ovi_user -> oviUserDao.cambiarEstadoRol(
                    solicitud.getPersonaSolicitante(),
                    EstadoRol.Pendiente
            );

            case Pap_pati -> papPatiDao.cambiarEstadoRol(
                    solicitud.getPersonaSolicitante(),
                    EstadoRol.Pendiente
            );

            case Pa_request -> paRequestDao.cambiarEstadoPaRequest(
                    solicitud.getPersonaSolicitante(),
                    StatusPaRequest.En_espera
            );

            default -> {
                // Otros tipos no modifican roles/procesos todavía
            }
        }
    }
    public boolean puedeSolicitarRevision(UsuarioSesion usuario, Solicitud solicitud) {
        if (usuario == null || solicitud == null) {
            return false;
        }

        boolean esSolicitante = solicitud.getPersonaSolicitante() == usuario.getIdPersona();
        boolean esAdmin = puedeGestionarSolicitudes(usuario);

        return solicitud.getEstadoSolicitud() == EstadoSolicitud.Rechazada
                && (esSolicitante || esAdmin);
    }
}