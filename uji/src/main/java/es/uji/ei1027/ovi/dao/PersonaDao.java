package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PersonaRowMapper;
import es.uji.ei1027.ovi.modelo.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonaDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    //obtener todo tipo de usuarios personas por orden de id
    public List<Persona> getPersonasOrderId() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM persona ORDER BY id ASC ",
                    new PersonaRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
