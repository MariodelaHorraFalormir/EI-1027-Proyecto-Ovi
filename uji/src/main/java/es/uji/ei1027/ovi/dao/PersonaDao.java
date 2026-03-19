package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PersonaRowMapper;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
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
    public void deletePersona(int id) {
        jdbcTemplate.update("DELETE FROM persona WHERE id = ? ", id );
    }
    public Persona getPersona(int id) {
        try{
            return  jdbcTemplate.queryForObject("SELECT * FROM persona WHERE id = ? ", new PersonaRowMapper(), id);

        }catch (EmptyResultDataAccessException e){return null;}
    }

    public void updatePersona(Persona persona) {
        jdbcTemplate.update(
                "UPDATE persona SET nombre = ?, apellidos = ?, mail = ?, genero = ?::genero_enum, telefono = ?, direccion = ?, fecha_nacimiento = ?, fecha_alta = ?, fecha_baja = ? WHERE id = ?",
                persona.getNombre(),
                persona.getApellidos(),
                persona.getMail(),
                persona.getGenero().getTexto(),
                persona.getTelefono(),
                persona.getDireccion(),
                persona.getFechaNacimiento(),
                persona.getFechaAlta(),
                persona.getFechaBaja(),
                persona.getIdPersona()
        );

    }

}
