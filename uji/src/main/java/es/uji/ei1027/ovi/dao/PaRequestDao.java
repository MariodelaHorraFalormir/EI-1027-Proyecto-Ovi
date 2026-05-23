package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PaRequestRowMapper;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

@Repository
public class PaRequestDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addPaRequest(PaRequest paRequest) {
        // 1. Calculamos el ID manualmente para evitar el conflicto de duplicados
        Integer maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM pa_request", Integer.class);
        int nextId = (maxId == null) ? 1 : maxId + 1;

        // 2. Usamos OVERRIDING SYSTEM VALUE para que Postgres nos deje meter el ID manual
        // a pesar de ser una columna GENERATED ALWAYS
        String sql = "INSERT INTO pa_request (id, status, fecha_creacion, fecha_resolucion, ovi_user) " +
                "OVERRIDING SYSTEM VALUE " +
                "VALUES (?, ?::status_pa_request_enum, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                nextId,
                paRequest.getStatus().getTexto(),
                Date.valueOf(paRequest.getFechaCreacion()),
                paRequest.getFechaResolucion() != null ? Date.valueOf(paRequest.getFechaResolucion()) : null,
                paRequest.getOviUser()
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
}