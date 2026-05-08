package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.AdminOviRowMapper;
import es.uji.ei1027.ovi.modelo.AdminOvi.AdminOvi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminOviDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AdminOvi getAdminOvi(int idPersona) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM admin_ovi WHERE id = ?",
                    new AdminOviRowMapper(),
                    idPersona
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean existeAdminOvi(int idPersona) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM admin_ovi WHERE id = ?",
                Integer.class,
                idPersona
        );

        return count != null && count > 0;
    }

    public List<AdminOvi> getAdminsOvi() {
        return jdbcTemplate.query(
                "SELECT id FROM admin_ovi ORDER BY id",
                new AdminOviRowMapper()
        );
    }

    public void add(AdminOvi adminOvi) {
        jdbcTemplate.update(
                "INSERT INTO admin_ovi(id) VALUES (?)",
                adminOvi.getIdAdminOvi()
        );
    }

    public void addByIdPersona(int idPersona) {
        jdbcTemplate.update(
                "INSERT INTO admin_ovi(id) VALUES (?)",
                idPersona
        );
    }

    public void delete(int idPersona) {
        jdbcTemplate.update(
                "DELETE FROM admin_ovi WHERE id = ?",
                idPersona
        );
    }
}