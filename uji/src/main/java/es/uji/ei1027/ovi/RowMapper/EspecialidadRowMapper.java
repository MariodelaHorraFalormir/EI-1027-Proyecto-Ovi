package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import es.uji.ei1027.ovi.modelo.OviUser.TipoDiversidadFuncional;
import es.uji.ei1027.ovi.modelo.PapPati.Especialidad;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EspecialidadRowMapper implements RowMapper<Especialidad> {

    @Override
    public Especialidad mapRow(ResultSet rs, int rowNum) throws SQLException {
        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(rs.getInt("id"));
        especialidad.setIdPapPati(rs.getInt("pap_pati"));
        especialidad.setDiversidadFuncional(TipoDiversidadFuncional.fromString(rs.getString("especialidad")));
        return especialidad;
    }
}