package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.modelo.Persona.Personalidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PersonalidadDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Personalidad getPersonalidad(int idPersona) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, ritmo, comunicacion, expresividad, caracter, naturaleza " +
                            "FROM persona WHERE id = ?",
                    (rs, rowNum) -> {
                        Personalidad p = new Personalidad();
                        p.setIdPersona(rs.getInt("id"));
                        p.setRitmo(rs.getInt("ritmo"));
                        p.setComunicacion(rs.getInt("comunicacion"));
                        p.setExpresividad(rs.getInt("expresividad"));
                        p.setCaracter(rs.getInt("caracter"));
                        p.setNaturaleza(rs.getInt("naturaleza"));
                        return p;
                    },
                    idPersona
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updatePersonalidad(Personalidad personalidad) {
        String sql = "UPDATE persona SET " +
                "ritmo = ?, " +
                "comunicacion = ?, " +
                "expresividad = ?, " +
                "caracter = ?, " +
                "naturaleza = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                personalidad.getRitmo(),
                personalidad.getComunicacion(),
                personalidad.getExpresividad(),
                personalidad.getCaracter(),
                personalidad.getNaturaleza(),
                personalidad.getIdPersona()
        );
    }
}