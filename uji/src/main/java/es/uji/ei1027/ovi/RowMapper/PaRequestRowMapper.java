package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import es.uji.ei1027.ovi.modelo.PaRequest.StatusPaRequest;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaRequestRowMapper implements RowMapper<PaRequest> {

    @Override
    public PaRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        PaRequest paRequest = new PaRequest();
        paRequest.setId(rs.getInt("id"));
        paRequest.setStatus(StatusPaRequest.fromString(rs.getString("status")));
        paRequest.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());

        if (rs.getDate("fecha_resolucion") != null)
            paRequest.setFechaResolucion(rs.getDate("fecha_resolucion").toLocalDate());

        if (rs.getDate("fecha_inicio") != null)
            paRequest.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        if (rs.getDate("fecha_fin") != null)
            paRequest.setFechaFin(rs.getDate("fecha_fin").toLocalDate());

        paRequest.setTipoAsistencia(rs.getString("tipo_asistencia"));
        paRequest.setPreferencias(rs.getString("preferencias"));
        paRequest.setOviUser(rs.getInt("ovi_user"));

        paRequest.setGeneroAsistente(rs.getString("genero_asistente"));
        paRequest.setDisponibilidadHoraria(rs.getString("disponibilidad_horaria"));
        paRequest.setZonaGeografica(rs.getString("zona_geografica"));

        return paRequest;
    }
}