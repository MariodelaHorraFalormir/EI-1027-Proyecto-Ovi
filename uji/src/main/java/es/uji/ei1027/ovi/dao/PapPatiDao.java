package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.modelo.Persona.Roles.PatPati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
@Repository
public class PapPatiDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PatPati getPapPati(int id) {
        try {
           // return jdbcTemplate.queryForObject("SELECT * FROM pat_pati WHERE id = ? ", new PatPatiRowMapper(), id);
            return null;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updatePatPati(PatPati patPati) {
        jdbcTemplate.update(
                "UPDATE pat_pati SET disponibilidad = ?::disponibilidad_enum WHERE id = ?",
                patPati.getDisponibilidad().getTexto(),
                patPati.getIdPatPati()
        );
    }

}
