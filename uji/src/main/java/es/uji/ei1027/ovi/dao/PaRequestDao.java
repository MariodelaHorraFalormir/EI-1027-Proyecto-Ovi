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
import java.util.Map;

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
                "tipo_asistencia, fecha_inicio, fecha_fin, preferencias) " +
                "OVERRIDING SYSTEM VALUE " +
                "VALUES (?, ?::status_pa_request_enum, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                paRequest.getFechaFin() != null ? Date.valueOf(paRequest.getFechaFin()) : null,
                paRequest.getPreferencias()
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
        return jdbcTemplate.query(
                "SELECT * FROM pa_request ORDER BY id DESC",
                new PaRequestRowMapper()
        );
    }

    public void cambiarEstadoPaRequest(int idPaRequest, StatusPaRequest statusPaRequest) {
        String sql = "UPDATE pa_request SET status = ?::status_pa_request_enum WHERE id = ?";
        jdbcTemplate.update(sql, statusPaRequest.getTexto(), idPaRequest);
    }

    public List<PaRequest> getPaRequestsByOviUser(int oviUser) {
        return jdbcTemplate.query(
                "SELECT * FROM pa_request WHERE ovi_user = ? ORDER BY id DESC",
                new PaRequestRowMapper(),
                oviUser
        );
    }

    public List<PaRequest> getPaRequestsByPapPati(int idPapPati) {
        return jdbcTemplate.query(
                "SELECT DISTINCT pr.* " +
                        "FROM pa_request pr " +
                        "JOIN conversacion c ON c.pa_request = pr.id " +
                        "WHERE c.pap_pati = ? " +
                        "ORDER BY pr.id DESC",
                new PaRequestRowMapper(),
                idPapPati
        );
    }

    public List<Map<String, Object>> getPapPatisAsociadosByPaRequest(int idPaRequest) {
        String sql =
                "SELECT DISTINCT " +
                        "p.id AS id_candidato, " +
                        "p.nombre AS nombre, " +
                        "p.apellidos AS apellidos, " +
                        "pp.experiencia AS experiencia, " +
                        "pp.disponibilidad AS disponibilidad " +
                        "FROM conversacion c " +
                        "JOIN pap_pati pp ON pp.id = c.pap_pati " +
                        "JOIN persona p ON p.id = pp.id " +
                        "WHERE c.pa_request = ? " +
                        "ORDER BY p.apellidos, p.nombre";

        return jdbcTemplate.queryForList(sql, idPaRequest);
    }

    public List<PaRequest> getPaRequestsFiltrados(StatusPaRequest estado, String busqueda) {
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT pr.* " +
                        "FROM pa_request pr " +
                        "LEFT JOIN ovi_user ou ON ou.id = pr.ovi_user " +
                        "LEFT JOIN persona p ON p.id = ou.id " +
                        "WHERE 1 = 1 "
        );

        List<Object> parametros = new ArrayList<>();

        if (estado != null) {
            sql.append("AND pr.status = ?::status_pa_request_enum ");
            parametros.add(estado.getTexto());
        }

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            String texto = "%" + busqueda.trim().toLowerCase() + "%";

            sql.append("AND (")
                    .append("CAST(pr.id AS TEXT) LIKE ? ")
                    .append("OR CAST(pr.ovi_user AS TEXT) LIKE ? ")
                    .append("OR LOWER(COALESCE(p.nombre, '')) LIKE ? ")
                    .append("OR LOWER(COALESCE(p.apellidos, '')) LIKE ? ")
                    .append("OR LOWER(COALESCE(p.mail, '')) LIKE ? ")
                    .append("OR LOWER(CONCAT(COALESCE(p.nombre, ''), ' ', COALESCE(p.apellidos, ''))) LIKE ? ")
                    .append("OR LOWER(COALESCE(pr.tipo_asistencia, '')) LIKE ? ")
                    .append("OR LOWER(COALESCE(pr.zona_geografica, '')) LIKE ? ")
                    .append("OR LOWER(COALESCE(pr.disponibilidad_horaria, '')) LIKE ? ")
                    .append(") ");

            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
        }

        sql.append("ORDER BY pr.id DESC");

        return jdbcTemplate.query(
                sql.toString(),
                new PaRequestRowMapper(),
                parametros.toArray()
        );
    }
}