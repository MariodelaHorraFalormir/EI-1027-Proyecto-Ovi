package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PaRequestRowMapper;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;

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

        String sql = "INSERT INTO pa_request (id, status, fecha_creacion, ovi_user, fecha_inicio, fecha_fin, tipo_asistencia, preferencias) " +
                "OVERRIDING SYSTEM VALUE VALUES (?, ?::status_pa_request_enum, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                nextId,
                paRequest.getStatus().getTexto(),
                Date.valueOf(paRequest.getFechaCreacion()),
                paRequest.getOviUser(),
                paRequest.getFechaInicio() != null ? Date.valueOf(paRequest.getFechaInicio()) : null,
                paRequest.getFechaFin() != null ? Date.valueOf(paRequest.getFechaFin()) : null,
                paRequest.getTipoAsistencia(),
                paRequest.getPreferencias()
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


    public void cambiarEstadoPaRequest(int personaSolicitante, StatusPaRequest statusPaRequest) {

        String sql = "UPDATE ovi_user SET "
                + "estado = ?::status_pa_request_enum "
                + "WHERE id = ?" ;
        jdbcTemplate.update(sql,statusPaRequest.getTexto(),personaSolicitante);
    }
    public List<PaRequest> getPaRequests() {
        return jdbcTemplate.query("SELECT * FROM pa_request", new PaRequestRowMapper());
    }
}