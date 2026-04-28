package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.OviUserRowMapper;
import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class OviUserDao {
    private JdbcTemplate jdbcTemplate;
    private DiversidadFuncionalDao diversidadFuncionalDao;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public void setDiversidadFuncionalDao(DiversidadFuncionalDao diversidadFuncionalDao) {
        this.diversidadFuncionalDao = diversidadFuncionalDao;
    }
    public OviUser getOviUser(int id) {
        try{
            OviUser oviUser = jdbcTemplate.queryForObject("SELECT * FROM ovi_user WHERE id = ? ", new OviUserRowMapper(), id);
            oviUser.setDiversidadesFuncionales(diversidadFuncionalDao.obtenerDiverdadesPorId(id));
            return oviUser;
        }catch (EmptyResultDataAccessException e){return null;}
    }
    public void updateOviUser(OviUser oviUser) {
        // de momento no hay campos que actualizar
    }
    public void addOviUser(OviUser oviUser) {
        String sql = "INSERT INTO ovi_user " +
                "(id, grado_diversidad_funcional, grado_dependencia, url_proyecto_de_vida, centro_social_referencia) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                oviUser.getIdOviUser(),
                oviUser.getGradoDiversidadFuncional(),
                oviUser.getGradoDependencia(),// o .name() según tu enum
                oviUser.getUrlProyectoDeVida(),
                oviUser.getCentroSocialReferencia()
        );
    }
    public boolean existeOviUser(int idPersona) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ovi_user WHERE id = ?",
                Integer.class,
                idPersona
        );
        return count != null ;
    }


}

