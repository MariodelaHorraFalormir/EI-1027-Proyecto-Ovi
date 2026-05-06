package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PaRequestDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.PaRequest.StatusPaRequest;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    public void updateSolicitud(int idOriginal, Solicitud solicitudNueva) {

        Solicitud solicitudAntigua = solicitudesDao.getSolicitudById(idOriginal);

        if (solicitudAntigua == null) {
            throw new IllegalArgumentException("No existe la solicitud con id " + idOriginal);
        }

        prepararResolucion(solicitudNueva);

        solicitudesDao.updateSolicitud(idOriginal, solicitudNueva);

        aplicarCambioSiProcede(solicitudAntigua, solicitudNueva);
    }

    @Transactional
    public void aprobarRapido(int idOriginal) {

        Solicitud solicitudAntigua = solicitudesDao.getSolicitudById(idOriginal);

        if (solicitudAntigua == null) {
            throw new IllegalArgumentException("No existe la solicitud con id " + idOriginal);
        }

        solicitudesDao.aprobarRapido(idOriginal);

        if (solicitudAntigua.getEstadoSolicitud() != EstadoSolicitud.Aprobada) {
            aplicarAprobacion(solicitudAntigua);
        }
    }

    @Transactional
    public void rechazarRapido(int idOriginal) {

        Solicitud solicitudAntigua = solicitudesDao.getSolicitudById(idOriginal);

        if (solicitudAntigua == null) {
            throw new IllegalArgumentException("No existe la solicitud con id " + idOriginal);
        }

        solicitudesDao.rechazarRapido(idOriginal);

        if (solicitudAntigua.getEstadoSolicitud() != EstadoSolicitud.Rechazada) {
            aplicarRechazo(solicitudAntigua);
        }
    }

    private void prepararResolucion(Solicitud solicitud) {

        if (solicitud.getEstadoSolicitud() == EstadoSolicitud.Aprobada ||
                solicitud.getEstadoSolicitud() == EstadoSolicitud.Rechazada) {

            solicitud.setFechaResolucion(LocalDate.now());

            // Temporal hasta tener login
            solicitud.setTecnicoRevisor(1);
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
}