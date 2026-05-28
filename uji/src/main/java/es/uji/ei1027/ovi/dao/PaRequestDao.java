package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PaRequestRowMapper;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import es.uji.ei1027.ovi.modelo.PaRequest.StatusPaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaRequestDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addPaRequest(PaRequest paRequest) {
        Integer maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM pa_request", Integer.class);
        int nextId = (maxId == null) ? 1 : maxId + 1;

        String sql = "INSERT INTO pa_request (id, status, fecha_creacion, fecha_resolucion, ovi_user, " +
                "genero_asistente, disponibilidad_horaria, zona_geografica) " +
                "OVERRIDING SYSTEM VALUE " +
                "VALUES (?, ?::status_pa_request_enum, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                nextId,
                paRequest.getStatus().getTexto(),
                Date.valueOf(paRequest.getFechaCreacion()),
                paRequest.getFechaResolucion() != null ? Date.valueOf(paRequest.getFechaResolucion()) : null,
                paRequest.getOviUser(),
                paRequest.getGeneroAsistente(),
                paRequest.getDisponibilidadHoraria(),
                paRequest.getZonaGeografica()
        );
    }

    public void updatePaRequest(PaRequest paRequest) {
        jdbcTemplate.update(
                "UPDATE pa_request SET status = ?::status_pa_request_enum, fecha_creacion = ?, fecha_resolucion = ?, ovi_user = ? WHERE id = ?",
                paRequest.getStatus().getTexto(),
                Date.valueOf(paRequest.getFechaCreacion()),
                paRequest.getFechaResolucion() != null ? Date.valueOf(paRequest.getFechaResolucion()) : null,
                paRequest.getOviUser(),
                paRequest.getId()
        );
    }

    public void deletePaRequest(int id) {
        jdbcTemplate.update("DELETE FROM pa_request WHERE id = ?", id);
    }

    public PaRequest getPaRequestById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM pa_request WHERE id = ?",
                    new PaRequestRowMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<PaRequest> getPaRequests() {
        return jdbcTemplate.query("SELECT * FROM pa_request", new PaRequestRowMapper());
    }

    public void cambiarEstadoPaRequest(int oviUser, StatusPaRequest statusPaRequest) {
        String sql = "UPDATE pa_request SET status = ?::status_pa_request_enum WHERE ovi_user = ?";
        jdbcTemplate.update(sql, statusPaRequest.getTexto(), oviUser);
    }

    public List<PaRequest> getPaRequestsByOviUser(int oviUser) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM pa_request WHERE ovi_user = ? ORDER BY id DESC",
                    new PaRequestRowMapper(),
                    oviUser
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}