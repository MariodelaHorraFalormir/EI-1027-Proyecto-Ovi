package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class PapPatiService {
    private PapPatiDao PapPatiDao;
    private SolicitudesDao solicitudesDao;

    private   String rutaCrear = "/PapPati/create/";
    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.PapPatiDao = papPatiDao;
    }

    @Autowired
    public void setSolicitudesDao(SolicitudesDao solicitudesDao) {
        this.solicitudesDao = solicitudesDao;
    }

    private Map<EstadoRol, String> rutasEstado = Map.of(
            EstadoRol.Activo, "/PapPati/details/",
            EstadoRol.Pendiente, "/Solicitudes/detail/",
            EstadoRol.Rechazado, "/Solicitudes/detail/"
    );
    public String obtenerRutaSolicitudPapPati(int idPersona) {

        PapPati papPati = PapPatiDao.getPapPati(idPersona);

        if (papPati == null) {
            return rutaCrear+idPersona;
        }

        EstadoRol estadoRol = papPati.getEstadoRol();

        if (estadoRol == EstadoRol.Activo) {
            return rutasEstado.get(EstadoRol.Activo)+idPersona;
        }

        Solicitud solicitudMasReciente = solicitudesDao.getSolicitudRolMasReciente(idPersona, TipoSolicitud.Pap_pati);

        if (solicitudMasReciente == null) {
            return "/";
        }

        return rutasEstado.get(estadoRol)+solicitudMasReciente.getIdSolicitud();
    }
}
