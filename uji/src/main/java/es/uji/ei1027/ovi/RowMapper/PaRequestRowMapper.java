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

        if (rs.getDate("fecha_resolucion") != null) {
            paRequest.setFechaResolucion(rs.getDate("fecha_resolucion").toLocalDate());
        }

        paRequest.setOviUser(rs.getInt("ovi_user"));

        return paRequest;
    }
}