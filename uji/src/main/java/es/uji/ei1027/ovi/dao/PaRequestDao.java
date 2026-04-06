package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PaRequestRowMapper;
import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;

@Repository
public class PaRequestDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addPaRequest(PaRequest paRequest) {
        LocalDate fechaResolucion;
        if (paRequest.getFechaResolucion() != null) {
             fechaResolucion = paRequest.getFechaResolucion();
        }else{fechaResolucion = null;}
        jdbcTemplate.update(
                "INSERT INTO pa_request (status, fecha_creacion, fecha_resolucion, ovi_user) VALUES (?, ?, ?, ?)",
                paRequest.getStatus().getTexto(),
                Date.valueOf(paRequest.getFechaCreacion()),
                fechaResolucion,
                paRequest.getOviUser()
        );
    }

    public void updatePaRequest(PaRequest paRequest) {
        jdbcTemplate.update(
                "UPDATE pa_request SET status = ?, fecha_creacion = ?, fecha_resolucion = ?, ovi_user = ? WHERE id = ?",
                paRequest.getStatus().getTexto(),
                Date.valueOf(paRequest.getFechaCreacion()),
                paRequest.getFechaResolucion() != null ? Date.valueOf(paRequest.getFechaResolucion()) : null,
                paRequest.getOviUser(),
                paRequest.getId()
        );
    }

    public void deletePaRequest(int id) {
        jdbcTemplate.update("DELETE FROM pa_request WHERE id = ?", id);
    }

    public PaRequest getPaRequestById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM pa_request WHERE id = ?",
                    new PaRequestRowMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


}