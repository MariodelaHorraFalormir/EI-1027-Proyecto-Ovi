package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Persona.Roles.OviUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OviUserRowMapper implements RowMapper<OviUser> {
    @Override
    public OviUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OviUser oviUser = new OviUser();
        oviUser.setId(rs.getInt("id"));
        return oviUser;
    }
}