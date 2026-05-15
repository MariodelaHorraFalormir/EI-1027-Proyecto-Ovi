package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.AdminOvi.AdminOvi;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminOviRowMapper implements RowMapper<AdminOvi> {

    @Override
    public AdminOvi mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdminOvi adminOvi = new AdminOvi();
        adminOvi.setIdAdminOvi(rs.getInt("id"));
        return adminOvi;
    }
}