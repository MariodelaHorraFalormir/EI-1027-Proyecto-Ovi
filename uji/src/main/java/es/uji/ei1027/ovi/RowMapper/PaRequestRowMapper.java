package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import es.uji.ei1027.ovi.modelo.PaRequest.StatusPaRequest;
import es.uji.ei1027.ovi.modelo.Persona.Genero; // Asegúrate de que la ruta sea correcta
import es.uji.ei1027.ovi.modelo.Personalidad; // La clase que creaste
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

        // Mapeo de nuevos campos
        if (rs.getDate("fecha_creacion") != null)
            paRequest.setFechaInicio(rs.getDate("fecha_creacion").toLocalDate());
        if (rs.getDate("fecha_resolucion") != null)
            paRequest.setFechaFin(rs.getDate("fecha_resolucion").toLocalDate());

        paRequest.setTipoAsistencia(rs.getString("tipo_asistencia"));
        paRequest.setPreferencias(rs.getString("preferencias"));
        paRequest.setOviUser(rs.getInt("ovi_user"));

        String generoStr = rs.getString("genero_preferido");
        if (generoStr != null) {
            paRequest.setGeneroPreferido(Genero.valueOf(generoStr));
        }

        paRequest.setExperienciaMinima(rs.getInt("experiencia_minima"));

        Personalidad p = new Personalidad();
        p.setMovimiento(rs.getInt("p_movimiento"));
        p.setHabla(rs.getInt("p_habla"));
        p.setExpresividad(rs.getInt("p_expresividad"));
        p.setCaracter(rs.getInt("p_caracter"));
        p.setNaturaleza(rs.getInt("p_naturaleza"));

        paRequest.setPersonalidadDeseada(p);

        int idAsignado = rs.getInt("id_pappati_asignado");
        if (!rs.wasNull()) {
            paRequest.setIdPapPatiAsignado(idAsignado);
        }

        return paRequest;
    }
}