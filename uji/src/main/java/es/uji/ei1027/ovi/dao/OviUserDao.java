package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.OviUserRowMapper;
import es.uji.ei1027.ovi.RowMapper.PersonaRowMapper;
import es.uji.ei1027.ovi.modelo.Persona.Roles.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class OviUserDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public OviUser getOviUser(int id) {
        try{
            return  jdbcTemplate.queryForObject("SELECT * FROM persona WHERE id = ? ", new OviUserRowMapper(), id);

        }catch (EmptyResultDataAccessException e){return null;}
    }



}

