package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OviUserService {

    private OviUserDao oviUserDao;
    private SolicitudesDao solicitudesDao;

    private   String rutaCrear = "/OviUser/create/";

    private  Map<EstadoRol, String> rutasEstado = Map.of(
            EstadoRol.Activo, "/OviUser/details/",
            EstadoRol.Pendiente, "/Solicitudes/detail/",
            EstadoRol.Rechazado, "/Solicitudes/detail/"
    );

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setSolicitudesDao(SolicitudesDao solicitudesDao) {
        this.solicitudesDao = solicitudesDao;
    }

    public String obtenerRutaSolicitudOviUser(int idPersona) {

        OviUser oviUser = oviUserDao.getOviUser(idPersona);

        if (oviUser == null) {
            return rutaCrear+idPersona;
        }

        EstadoRol estadoRol = oviUser.getEstado();

        if (estadoRol == EstadoRol.Activo) {
            return rutasEstado.get(EstadoRol.Activo)+idPersona;
        }

        Solicitud solicitudMasReciente = solicitudesDao.getSolicitudRolMasReciente(idPersona, TipoSolicitud.Ovi_user);

        if (solicitudMasReciente == null) {
            return "/";
        }

        return rutasEstado.get(estadoRol)+solicitudMasReciente.getIdSolicitud();
    }
    @Transactional
    public void creaOviUser(OviUser oviUser , Solicitud solicitud) {
        oviUserDao.addOviUser(oviUser);
        solicitudesDao.createSolicitud(solicitud);
    }
    public OviUser getOviUser(int idPersona) {
        return oviUserDao.getOviUser(idPersona);
    }
    public List<String> getDiversidadesTexto(int idPersona) {
        OviUser oviUser = oviUserDao.getOviUser(idPersona);

        List<String> diversidades = new ArrayList<>();

        if (oviUser == null || oviUser.getDiversidadesFuncionales() == null) {
            return diversidades;
        }

        for (DiversidadFuncional diversidadFuncional : oviUser.getDiversidadesFuncionales()) {
            diversidades.add(diversidadFuncional.getTipo().getTexto());
        }

        return diversidades;
    }
    public void actualizarOviUser(int idPersona, OviUser oviUser) {
        oviUser.setIdOviUser(idPersona);
        oviUserDao.updateOviUser(oviUser);
    }
}