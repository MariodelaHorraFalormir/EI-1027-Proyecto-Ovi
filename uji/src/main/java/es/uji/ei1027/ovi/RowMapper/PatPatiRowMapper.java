package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.Persona.Roles.Disponibilidad;
import es.uji.ei1027.ovi.modelo.Persona.Roles.PatPati;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PatPatiRowMapper implements RowMapper<PatPati> {

    @Override
    public PatPati mapRow(ResultSet rs, int rowNum) throws SQLException {
        PatPati patPati = new PatPati();
        patPati.setIdPatPati(rs.getInt("id"));
        patPati.setDisponibilidad(Disponibilidad.fromString(rs.getString("disponibilidad")));


        return patPati;
    }
}