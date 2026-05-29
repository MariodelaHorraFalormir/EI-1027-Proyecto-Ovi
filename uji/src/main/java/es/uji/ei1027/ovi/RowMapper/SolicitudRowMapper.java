package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Solicitud.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SolicitudRowMapper implements RowMapper<Solicitud> {
    @Override
    public Solicitud mapRow(ResultSet rs , int rowNum) throws SQLException {
        Solicitud solicitud = new Solicitud();
        solicitud.setIdSolicitud(rs.getInt("id"));
        solicitud.setMensajeSolicitud(rs.getString("mensaje_solicitante"));
        solicitud.setTecnicoRevisor(rs.getObject("tecnico_revisor", Integer.class));
        solicitud.setCategoriaSolicitud(CategoriaSolicitud.fromString(rs.getString("categoria")));
        solicitud.setTipoSolicitud(TipoSolicitud.fromString(rs.getString("detalle")));
        solicitud.setEstadoSolicitud(EstadoSolicitud.fromString(rs.getString("estado")));
        solicitud.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
        if (rs.getDate("fecha_resolucion") != null) {
            solicitud.setFechaResolucion(rs.getDate("fecha_resolucion").toLocalDate());
        }
        solicitud.setMensajeRevision(rs.getString("mensaje_revision"));

        Timestamp fechaUltimaRevision = rs.getTimestamp("fecha_ultima_revision");
        if (fechaUltimaRevision != null) {
            solicitud.setFechaUltimaRevision(fechaUltimaRevision.toLocalDateTime());
        }

        int numeroRevisiones = rs.getInt("numero_revisiones");
        solicitud.setNumeroRevisiones(rs.wasNull() ? 0 : numeroRevisiones);
        solicitud.setPersonaSolicitante(rs.getInt("persona_solicitante"));
        return  solicitud;
    }


}
