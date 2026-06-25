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
                "genero_asistente, disponibilidad_horaria, zona_geografica, " +
                "tipo_asistencia, fecha_inicio, fecha_fin) " +
                "OVERRIDING SYSTEM VALUE " +
                "VALUES (?, ?::status_pa_request_enum, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                nextId,
                paRequest.getStatus().getTexto(),
                Date.valueOf(paRequest.getFechaCreacion()),
                paRequest.getFechaResolucion() != null ? Date.valueOf(paRequest.getFechaResolucion()) : null,
                paRequest.getOviUser(),
                paRequest.getGeneroAsistente(),
                paRequest.getDisponibilidadHoraria(),
                paRequest.getZonaGeografica(),
                paRequest.getTipoAsistencia(),
                paRequest.getFechaInicio() != null ? Date.valueOf(paRequest.getFechaInicio()) : null,
                paRequest.getFechaFin() != null ? Date.valueOf(paRequest.getFechaFin()) : null
        );
    }

    public void updatePaRequest(PaRequest paRequest) {
        String sql = "UPDATE pa_request SET " +
                "status = ?::status_pa_request_enum, " +
                "fecha_resolucion = ?, " +
                "tipo_asistencia = ?, " +
                "fecha_inicio = ?, " +
                "fecha_fin = ?, " +
                "genero_asistente = ?, " +
                "disponibilidad_horaria = ?, " +
                "zona_geografica = ?, " +
                "preferencias = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                paRequest.getStatus().getTexto(),
                paRequest.getFechaResolucion() != null ? Date.valueOf(paRequest.getFechaResolucion()) : null,
                paRequest.getTipoAsistencia(),
                paRequest.getFechaInicio() != null ? Date.valueOf(paRequest.getFechaInicio()) : null,
                paRequest.getFechaFin() != null ? Date.valueOf(paRequest.getFechaFin()) : null,
                paRequest.getGeneroAsistente(),
                paRequest.getDisponibilidadHoraria(),
                paRequest.getZonaGeografica(),
                paRequest.getPreferencias(),
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

    public void cambiarEstadoPaRequest(int idPaRequest, StatusPaRequest statusPaRequest) {
        String sql = "UPDATE pa_request SET status = ?::status_pa_request_enum WHERE id = ?";
        jdbcTemplate.update(sql, statusPaRequest.getTexto(), idPaRequest);
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

    public List<PaRequest> getPaRequestsByPapPati(int idPapPati) {
        try {
            return jdbcTemplate.query(
                    "SELECT DISTINCT pr.* FROM pa_request pr " +
                            "JOIN mensaje m ON m.id_solicitud = pr.id " +
                            "WHERE m.id_emisor = ? OR m.id_receptor = ? " +
                            "ORDER BY pr.id DESC",
                    new PaRequestRowMapper(),
                    idPapPati, idPapPati
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}