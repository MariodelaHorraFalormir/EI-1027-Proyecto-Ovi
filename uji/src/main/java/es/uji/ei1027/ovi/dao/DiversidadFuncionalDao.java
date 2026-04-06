package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.DiversidadFuncionalRowMapper;
import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class DiversidadFuncionalDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<DiversidadFuncional> obtenerDiverdadesPorId(int id) {
        String sql = """
            SELECT id, ovi_user, tipo, observaciones
            FROM diversidad_funcional
            WHERE ovi_user = ?
            ORDER BY id
            """;

        return jdbcTemplate.query(sql, new DiversidadFuncionalRowMapper(), id);
    }
    public void borrarDiversidadFuncional(int id) {
        String sql = """
            DELETE FROM diversidad_funcional WHERE id = ?
        """;
       jdbcTemplate.update(sql, id);
    }

    public void addDiversidadFuncional(DiversidadFuncional diversidadFuncional) {
        String sql = """
                INSERT INTO diversidad_funcional
                (ovi_user, tipo, observaciones)
                VALUES (?, ?::diversidad_funcional_enum, ?)
                """;

        jdbcTemplate.update(
                sql,
                diversidadFuncional.getOviUserId(),
                diversidadFuncional.getTipo().getTexto(),
                diversidadFuncional.getObservaciones()
        );
    }
    public DiversidadFuncional obtenerDiversidadFuncionalPorId(int idDiversidad) {
        String sql = "SELECT id, ovi_user_id, tipo " +
                "FROM diversidad_funcional WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new DiversidadFuncionalRowMapper(), idDiversidad);
    }

    public void updateDiversidadFuncional( DiversidadFuncional diversidadFuncional) {
        String sql = """
                UPDATE diversidad_funcional
                SET
                    ovi_user = ?,
                    tipo = ?::diversidad_funcional_enum,
                    observaciones = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                diversidadFuncional.getOviUserId(),
                diversidadFuncional.getTipo().getTexto(),
                diversidadFuncional.getObservaciones(),
                diversidadFuncional.getIdDiversidadFuncional()
        );
    }
}
