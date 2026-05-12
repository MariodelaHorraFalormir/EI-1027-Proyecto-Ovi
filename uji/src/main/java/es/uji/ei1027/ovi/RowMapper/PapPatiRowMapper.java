package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.PapPati.Disponibilidad;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Personalidad; // Importante
import org.springframework.jdbc.core.RowMapper;

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
        papPati.setEstadoRol(EstadoRol.fromString(rs.getString("estado_rol")));

        // --- MAPEO DE PERSONALIDAD TOMODACHI (NUEVO) ---
        Personalidad p = new Personalidad();
        p.setMovimiento(rs.getInt("p_movimiento"));
        p.setHabla(rs.getInt("p_habla"));
        p.setExpresividad(rs.getInt("p_expresividad"));
        p.setCaracter(rs.getInt("p_caracter"));
        p.setNaturaleza(rs.getInt("p_naturaleza"));

        papPati.setPersonalidad(p);

        return papPati;
    }
}