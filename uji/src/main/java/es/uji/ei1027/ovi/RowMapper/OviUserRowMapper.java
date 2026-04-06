package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.dao.DiversidadFuncionalDao;
import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class OviUserRowMapper implements RowMapper<OviUser> {
    private final DiversidadFuncionalDao diversidadFuncionalDao;

    public OviUserRowMapper(DiversidadFuncionalDao diversidadFuncionalDao) {
        this.diversidadFuncionalDao = diversidadFuncionalDao;
    }
    @Override
    public OviUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OviUser oviUser = new OviUser();
        oviUser.setIdOviUser(rs.getInt("idOviUser"));
        oviUser.setCentroSocialReferencia(rs.getString("centro_social_referencia"));
        oviUser.setUrlProyectoDeVida(rs.getString("url_proyecto_de_vida"));
        oviUser.setGradoDependencia(rs.getInt("grado_dependencia"));
        oviUser.setEstado(EstadoRol.fromString(rs.getString("estado")));
        oviUser.setGradoDependencia(rs.getObject("grado_dependencia", Integer.class));
        //Aqui tengo que ser capaz de Cargar las diversidades funcionales
        List<DiversidadFuncional>  diversidadFuncionals =  diversidadFuncionalDao.obtenerDiverdadesPorId(oviUser.getIdOviUser());
        return oviUser;
    }
}