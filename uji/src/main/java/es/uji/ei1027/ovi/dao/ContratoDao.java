package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.modelo.Contrato.Contrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ContratoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private Contrato mapContrato(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Contrato c = new Contrato();

        c.setId(rs.getInt("id"));
        c.setIdSolicitud(rs.getInt("id_solicitud"));
        c.setIdUsuarioOvi(rs.getInt("id_usuario_ovi"));
        c.setIdPapPati(rs.getInt("id_pap_pati"));

        if (rs.getDate("fecha_inicio") != null) {
            c.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        }

        if (rs.getDate("fecha_fin") != null) {
            c.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        }

        c.setEstado(rs.getString("estado"));

        return c;
    }

    public void addContrato(Contrato contrato) {
        String sql = """
            INSERT INTO contrato (
                id_solicitud,
                id_usuario_ovi,
                id_pap_pati,
                fecha_inicio,
                fecha_fin,
                estado
            )
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                contrato.getIdSolicitud(),
                contrato.getIdUsuarioOvi(),
                contrato.getIdPapPati(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getEstado() != null ? contrato.getEstado() : "Activo"
        );
    }

    public List<Contrato> getContratosPorUsuario(int idUsuario) {
        String sql = """
            SELECT *
            FROM contrato
            WHERE id_usuario_ovi = ?
               OR id_pap_pati = ?
            ORDER BY id DESC
        """;

        return jdbcTemplate.query(sql, this::mapContrato, idUsuario, idUsuario);
    }

    public Contrato getContratoPorId(int id) {
        String sql = """
            SELECT *
            FROM contrato
            WHERE id = ?
        """;

        try {
            return jdbcTemplate.queryForObject(sql, this::mapContrato, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Contrato getContratoPorSolicitud(int idSolicitud) {
        String sql = """
            SELECT *
            FROM contrato
            WHERE id_solicitud = ?
        """;

        try {
            return jdbcTemplate.queryForObject(sql, this::mapContrato, idSolicitud);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean existeContratoParaSolicitud(int idSolicitud) {
        String sql = """
            SELECT COUNT(*)
            FROM contrato
            WHERE id_solicitud = ?
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idSolicitud);
        return count != null && count > 0;
    }

    public boolean existeContratoEntreOviYPapPati(int idUsuarioOvi, int idPapPati) {
        String sql = """
            SELECT COUNT(*)
            FROM contrato
            WHERE id_usuario_ovi = ?
              AND id_pap_pati = ?
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idUsuarioOvi, idPapPati);
        return count != null && count > 0;
    }

    public void updateContrato(Contrato contrato) {
        String sql = """
            UPDATE contrato
            SET fecha_inicio = ?,
                fecha_fin = ?,
                estado = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(sql,
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getEstado(),
                contrato.getId()
        );
    }

    public List<Contrato> getTodosLosContratos() {
        String sql = """
            SELECT *
            FROM contrato
            ORDER BY id DESC
        """;

        return jdbcTemplate.query(sql, this::mapContrato);
    }

    public void deleteContrato(int id) {
        String sql = """
            DELETE FROM contrato
            WHERE id = ?
        """;

        jdbcTemplate.update(sql, id);
    }
}