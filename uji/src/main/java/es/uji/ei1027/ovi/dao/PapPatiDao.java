package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PapPatiRowMapper;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class PapPatiDao {

    private JdbcTemplate jdbcTemplate;
    private  EspecialidadesDao especialidadesDao;

    @Autowired
    public void setDataSource(DataSource dataSource ,  EspecialidadesDao especialidadesDao) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.especialidadesDao = especialidadesDao;
    }
    public void crearRapidoActivo(int idPersona) {
        String sql = "INSERT INTO pap_pati " +
                "(id, disponibilidad, fecha_inicio_disponible, fecha_fin_disponible, experiencia, " +
                "vehiculo_propio, carnet_conducir, url_cv, descripcion_perfil, centro_social_referencia, estado) " +
                "VALUES (?, 'No disponible'::disponibilidad_enum, CURRENT_DATE, NULL, 0, " +
                "FALSE, FALSE, NULL, NULL, NULL, 'Activo'::estado_rol_enum)";

        jdbcTemplate.update(sql, idPersona);
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
        return count != null && count > 0 ;
    }
    public void cambiarEstadoRol(int personaSolicitante, EstadoRol estadoRol) {
        String sql = "UPDATE pap_pati SET "
                + "estado = ?::estado_rol_enum "
                + "WHERE id = ?" ;
        jdbcTemplate.update(sql,estadoRol.getTexto(),personaSolicitante);
    }

    // Metodo para obtener los candidatos activos y mostrarlos al usuario OVI
    public List<Map<String, Object>> getCandidatosDisponibles() {
        String sql = "SELECT p.nombre, p.apellidos, pp.id AS id_candidato, pp.experiencia, pp.disponibilidad " +
                "FROM persona p JOIN pap_pati pp ON p.id = pp.id " +
                "WHERE pp.estado = 'Activo'::estado_rol_enum";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getPapPatisByPaRequest(int idPaRequest) {
        String sql = "SELECT DISTINCT p.nombre, p.apellidos, pp.id AS id_candidato, " +
                "pp.experiencia, pp.disponibilidad " +
                "FROM persona p " +
                "JOIN pap_pati pp ON p.id = pp.id " +
                "JOIN conversacion c ON c.pap_pati = pp.id " +
                "WHERE c.pa_request = ? " +
                "ORDER BY p.apellidos, p.nombre";
        return jdbcTemplate.queryForList(sql, idPaRequest);
    }

}
