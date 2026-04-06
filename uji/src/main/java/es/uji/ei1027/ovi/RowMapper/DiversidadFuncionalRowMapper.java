package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import es.uji.ei1027.ovi.modelo.OviUser.TipoDiversidadFuncional;
import org.springframework.jdbc.core.RowMapper;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiversidadFuncionalRowMapper implements RowMapper<DiversidadFuncional> {

    @Override
    public DiversidadFuncional mapRow(ResultSet rs , int rowNum) throws SQLException {
      DiversidadFuncional diversidad = new DiversidadFuncional();
      diversidad.setIdDiversidadFuncional(rs.getInt("id"));
      diversidad.setOviUserId(rs.getInt("ovi_user"));
      diversidad.setTipo(TipoDiversidadFuncional.valueOf(rs.getString("tipo")));
      diversidad.setObservaciones(rs.getString("observaciones"));
      return diversidad;
    }
}
