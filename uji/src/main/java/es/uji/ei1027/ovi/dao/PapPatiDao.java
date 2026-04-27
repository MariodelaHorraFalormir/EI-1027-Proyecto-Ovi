package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PapPatiRowMapper;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
@Repository
public class PapPatiDao {

    private JdbcTemplate jdbcTemplate;
    private  EspecialidadesDao especialidadesDao;

    @Autowired
    public void setDataSource(DataSource dataSource ,  EspecialidadesDao especialidadesDao) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.especialidadesDao = especialidadesDao;
    }

    public PapPati getPapPati(int id) {
        try {
           PapPati papPati= jdbcTemplate.queryForObject("SELECT * FROM pap_pati WHERE id = ? ", new PapPatiRowMapper(), id);
            papPati.setEspecialidades(especialidadesDao.getEspecialidades(id));
            return papPati;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public void delete(int id) {
        String sql = "DELETE FROM pap_pati WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    public void update(PapPati papPati) {

        String sql = "UPDATE pap_pati SET " +
                "disponibilidad = ?::disponibilidad_enum, " +
                "fecha_inicio_disponible = ?, " +
                "fecha_fin_disponible = ?, " +
                "experiencia = ?, " +
                "vehiculo_propio = ?, " +
                "carnet_conducir = ?, " +
                "url_cv = ?, " +
                "descripcion_perfil = ?, " +
                "centro_social_referencia = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                papPati.getDisponibilidad().getTexto(),
                papPati.getFechaInicioDisponibilidad(),
                papPati.getFechaFinDisponibilidad(),
                papPati.getExperiencia(),
                papPati.getVehiculoPropio(),
                papPati.getCarnetConducir(),
                papPati.getUrlCV(),
                papPati.getDescripcionPerfil(),
                papPati.getCentroSocial(),
                papPati.getIdPatPati()
        );
    }


    public void crear(PapPati papPati) {

        String sql = "INSERT INTO pap_pati " +
                "(id, disponibilidad, fecha_inicio_disponible, fecha_fin_disponible, experiencia, " +
                "vehiculo_propio, carnet_conducir, url_cv, descripcion_perfil, centro_social_referencia ) " +
                "VALUES (?, ?::disponibilidad_enum, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                papPati.getIdPatPati(),
                papPati.getDisponibilidad().getTexto(),
                papPati.getFechaInicioDisponibilidad(),
                papPati.getFechaFinDisponibilidad() ,
                papPati.getExperiencia(),
                papPati.getVehiculoPropio(),
                papPati.getCarnetConducir(),
                papPati.getUrlCV(),
                papPati.getDescripcionPerfil(),
                papPati.getCentroSocial()
        );

    }
    public boolean existePapPati(int idPersona) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pap_pati WHERE id = ?",
                Integer.class,
                idPersona
        );
        return count != null;
    }

}
