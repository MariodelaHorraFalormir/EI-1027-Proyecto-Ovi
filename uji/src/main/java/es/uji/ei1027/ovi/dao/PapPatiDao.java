package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.PapPatiRowMapper;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Personalidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PapPatiDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PapPati getPapPati(int id) {
        try {
           return jdbcTemplate.queryForObject("SELECT * FROM pap_pati WHERE id = ? ", new PapPatiRowMapper(), id);


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
                "centro_social_referencia = ?, " +
                "estado = ?::estado_rol_enum " +
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
                papPati.getEstadoRol().getTexto(),
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

    public List<PapPati> getTodosPapPati() {
        String sql = "SELECT * FROM pap_pati WHERE estado_rol = 'Activo'";
        return jdbcTemplate.query(sql, new PapPatiRowMapper());
    }

    public List<PapPati> getRecomendados(String genero, int expMin, Personalidad deseada) {
        String sql = "SELECT * FROM pappati WHERE genero = ? AND experiencia >= ? " +
                "ORDER BY (ABS(movimiento - ?) + ABS(habla - ?) + ABS(expresividad - ?) + " +
                "ABS(caracter - ?) + ABS(naturaleza - ?)) ASC LIMIT 5";

        return jdbcTemplate.query(sql, new PapPatiRowMapper(),
                genero, expMin,
                deseada.getMovimiento(), deseada.getHabla(), deseada.getExpresividad(),
                deseada.getCaracter(), deseada.getNaturaleza());
    }

}
