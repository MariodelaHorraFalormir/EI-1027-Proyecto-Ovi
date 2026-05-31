package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.dao.EspecialidadesDao;
import es.uji.ei1027.ovi.modelo.PapPati.Disponibilidad;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PapPatiRowMapper implements RowMapper<PapPati> {

    @Override
    public PapPati mapRow(ResultSet rs, int rowNum) throws SQLException {
        PapPati papPati = new PapPati();
        papPati.setIdPatPati(rs.getInt("id"));
        papPati.setDisponibilidad(Disponibilidad.fromString(rs.getString("disponibilidad")));
        papPati.setExperiencia(rs.getInt("experiencia"));
        papPati.setVehiculoPropio(rs.getBoolean("vehiculo_propio"));
        papPati.setCarnetConducir(rs.getBoolean("carnet_conducir"));
        papPati.setUrlCV(rs.getString("url_cv"));
        papPati.setDescripcionPerfil(rs.getString("descripcion_perfil"));
        papPati.setCentroSocial(rs.getString("centro_social_referencia"));
        papPati.setEstadoRol(EstadoRol.fromString(rs.getString("estado")));
        Date fechaInicio = rs.getDate("fecha_inicio_disponible");
        Date fechaFin = rs.getDate("fecha_fin_disponible");
        if (fechaFin != null) {
            papPati.setFechaFinDisponibilidad(fechaFin.toLocalDate());
        }
        if (fechaInicio != null) {
            papPati.setFechaInicioDisponibilidad(fechaInicio.toLocalDate());
        }
        //aqui hay añadir una forma de consultar las especialidades por id

        return papPati;
    }

}