package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.Chat.Conversacion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConversacionRowMapper implements RowMapper<Conversacion> {

    @Override
    public Conversacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Conversacion conversacion = new Conversacion();

        conversacion.setId(rs.getInt("id"));
        conversacion.setPaRequest(rs.getInt("pa_request"));
        conversacion.setOviUser(rs.getInt("ovi_user"));
        conversacion.setPapPati(rs.getInt("pap_pati"));

        return conversacion;
    }
}