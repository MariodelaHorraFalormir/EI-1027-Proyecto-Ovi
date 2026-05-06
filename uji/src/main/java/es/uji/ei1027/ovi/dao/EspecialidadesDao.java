package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.EspecialidadRowMapper;
import es.uji.ei1027.ovi.modelo.PapPati.Especialidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class EspecialidadesDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public void addEspecialidad(int idPapPati, String especialidad) {

        String sql = "INSERT INTO especialidad (pap_pati, especialidad) " +
                "VALUES (?, ?::diversidad_funcional_enum)";

        jdbcTemplate.update(sql, idPapPati, especialidad);
    }
    public void deleteEspecialidad(int idPapPati, String especialidad) {

        String sql = "DELETE FROM especialidad " +
                "WHERE pap_pati = ? AND especialidad = ?::diversidad_funcional_enum";

        jdbcTemplate.update(sql, idPapPati, especialidad);
    }
    public List<Especialidad> getEspecialidades(int idPapPati) {

        String sql = "SELECT * FROM especialidad WHERE pap_pati = ?";

        return jdbcTemplate.query(sql, new EspecialidadRowMapper(), idPapPati);
    }
    public void  deleteAllbyId(int idPapPati) {
        String sql = "DELETE FROM especialidad " +
                "WHERE pap_pati = ? ";
        jdbcTemplate.update(sql, idPapPati);
    }

}
