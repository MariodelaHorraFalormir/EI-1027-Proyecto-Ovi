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
                "UPDATE persona SET nombre = ?, apellidos = ?, mail = ?, genero = ?::genero_enum, telefono = ?, direccion = ?, fecha_nacimiento = ?, fecha_alta = ?, fecha_baja = ? ,contrasena = ? , dni = ?  WHERE id = ?",
                persona.getNombre(),
                persona.getApellidos(),
                persona.getMail(),
                persona.getGenero().getTexto(),
                persona.getTelefono(),
                persona.getDireccion(),
                persona.getFechaNacimiento(),
                persona.getFechaAlta(),
                persona.getFechaBaja(),
                persona.getContrasena(),
                persona.getDni(),
                persona.getIdPersona()

        );

    }
    public int addPersonaYDevolverId(Persona persona) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO persona (nombre, apellidos, mail, telefono, direccion, genero, pais, fecha_nacimiento, fecha_alta, fecha_baja,contrasena,dni) " +
                        "VALUES (?, ?, ?, ?, ?, CAST(? AS genero_enum), ?, ?, ?, ?,?,?) RETURNING id",
                Integer.class,
                persona.getNombre(),
                persona.getApellidos(),
                persona.getMail(),
                persona.getTelefono(),
                persona.getDireccion(),
                persona.getGenero().getTexto(),
                persona.getPais(),
                persona.getFechaNacimiento(),
                persona.getFechaAlta(),
                persona.getFechaBaja(),
                persona.getContrasena(),
                persona.getDni()
        );
    }

    public boolean existeMail(String mail) {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM persona WHERE mail = ?",
                Integer.class,
                mail
        );
        return count != null && count > 0;
    }
    public Integer getIdPersonaByMail(String mail) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM persona WHERE mail = ?",
                    Integer.class,
                    mail
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
