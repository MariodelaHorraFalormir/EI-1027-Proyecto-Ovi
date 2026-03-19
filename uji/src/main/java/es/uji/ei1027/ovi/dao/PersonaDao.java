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

    // --- LISTAR (READ) ---
    // obtener todo tipo de usuarios personas por orden de id
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

    // --- OBTENER UNA SOLA PERSONA (Necesario para el UPDATE) ---
    public Persona getPersona(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM persona WHERE id=?",
                    new PersonaRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // --- AÑADIR (CREATE) ---
    public void addPersona(Persona persona) {
        jdbcTemplate.update("INSERT INTO persona (mail, nombre, apellidos, genero, direccion, pais, telefono, fecha_alta, fecha_baja, fecha_nacimiento) VALUES(?, ?, ?, ?::genero_enum, ?, ?, ?, ?, ?, '1990-01-01'::date)",
                persona.getMail(), persona.getNombre(), persona.getApellidos(), persona.getGenero(), persona.getDireccion(), persona.getPais(), persona.getTelefono(), persona.getFechaAlta(), persona.getFechaBaja());
    }

    // --- EDITAR (UPDATE) ---
    public void updatePersona(Persona persona) {
        jdbcTemplate.update("UPDATE persona SET mail=?, nombre=?, apellidos=?, genero=?::genero_enum, direccion=?, pais=?, telefono=?, fecha_alta=?, fecha_baja=?, fecha_nacimiento='1990-01-01'::date WHERE id=?",
                persona.getMail(), persona.getNombre(), persona.getApellidos(), persona.getGenero(), persona.getDireccion(), persona.getPais(), persona.getTelefono(), persona.getFechaAlta(), persona.getFechaBaja(), persona.getIdPersona());
    }
    // --- BORRAR (DELETE) ---
    public void deletePersona(int id) {
        jdbcTemplate.update("DELETE FROM persona WHERE id=?", id);
    }
}