package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.Chat.Mensaje;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MensajeRowMapper implements RowMapper<Mensaje> {

    @Override
    public Mensaje mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mensaje mensaje = new Mensaje();

        mensaje.setId(rs.getInt("id"));

        int idSolicitud = rs.getInt("id_solicitud");
        mensaje.setIdSolicitud(rs.wasNull() ? null : idSolicitud);

        mensaje.setIdEmisor(rs.getInt("id_emisor"));

        int idReceptor = rs.getInt("id_receptor");
        mensaje.setIdReceptor(rs.wasNull() ? null : idReceptor);

        mensaje.setContenido(rs.getString("contenido"));

        if (rs.getTimestamp("fecha_envio") != null) {
            mensaje.setFechaEnvio(rs.getTimestamp("fecha_envio").toLocalDateTime());
        }

        int idConversacion = rs.getInt("conversacion");
        mensaje.setConversacion(rs.wasNull() ? null : idConversacion);

        return mensaje;
    }
}