package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PersonaRowMapper;
import es.uji.ei1027.ovi.RowMapper.SolicitudRowMapper;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SolicitudesDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<Solicitud> getSolicitudesOrderId() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM solicitud ORDER BY id ASC ",
                    new SolicitudRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
    public Solicitud getSolicitudById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM solicitud WHERE id = ? ", new SolicitudRowMapper(), id);
        }catch (EmptyResultDataAccessException e){return null;}
    }
    public void createSolicitud(Solicitud solicitud) {
        String sql = "INSERT INTO solicitud " +
                "(persona_solicitante, categoria, detalle, estado,mensaje_solicitante) " +
                "VALUES (?, ?::categoria_solicitud_enum, ?::detalle_solicitud_enum, ?::estado_solicitud_enum, ?)";

        jdbcTemplate.update(
                sql,
                solicitud.getPersonaSolicitante(),
                solicitud.getCategoriaSolicitud().getTexto(),
                solicitud.getTipoSolicitud().getTexto(),
                solicitud.getEstadoSolicitud().getTexto(),
                solicitud.getMensajeSolicitud()
        );

    }
    public void updateSolicitud(int idOriginal, Solicitud solicitud) {

        String sql = """
        UPDATE solicitud
        SET
            persona_solicitante = ?,
            categoria = ?::categoria_solicitud_enum,
            detalle = ?::detalle_solicitud_enum,
            estado = ?::estado_solicitud_enum,
            fecha_creacion = ?,
            fecha_resolucion = ?,
            mensaje_solicitante = ?,
            tecnico_revisor = ?,
            motivo_resolucion = ?
        WHERE id = ?
        """;

        jdbcTemplate.update(sql,
                solicitud.getPersonaSolicitante(),
                solicitud.getCategoriaSolicitud().toString(),
                solicitud.getTipoSolicitud().toString(),
                solicitud.getEstadoSolicitud().toString(),
                solicitud.getFechaCreacion(),
                solicitud.getFechaResolucion(),
                solicitud.getMensajeSolicitud(),
                1,
                solicitud.getMotivoResolucion(),
                idOriginal
        );
    }
    public void aprobarRapido(int idOriginal) {

        String sql = "" +
                "UPDATE solicitud " +
                "SET estado = ?::estado_solicitud_enum,tecnico_revisor = ?, fecha_resolucion = ?" +
                " WHERE id = ?";
        jdbcTemplate.update(sql, EstadoSolicitud.Aprobada.toString(),1, LocalDateTime.now(), idOriginal);


    }
    public void rechazarRapido (int idOriginal) {

        String sql = "" +
                "UPDATE solicitud " +
                "SET estado = ?::estado_solicitud_enum,tecnico_revisor = ?, fecha_resolucion = ? ,motivo_resolucion =? " +
                " WHERE id = ?";
        jdbcTemplate.update(sql, EstadoSolicitud.Rechazada.toString(),1, LocalDateTime.now(),"Rechazo Rapido", idOriginal);


    }
    public void deleteSolicitud(int id) {
        jdbcTemplate.update("DELETE FROM solicitud WHERE id = ? ", id );
    }


}
