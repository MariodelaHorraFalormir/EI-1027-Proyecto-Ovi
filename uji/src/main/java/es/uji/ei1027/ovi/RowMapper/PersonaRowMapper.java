package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.Persona.Genero;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Persona.Personalidad;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PersonaRowMapper implements RowMapper<Persona> {
    @Override
    public  Persona mapRow(ResultSet rs , int rowNum) throws SQLException {
        Persona persona = new Persona();
        String fechaAlta = "fecha_alta";
        String fechaBaja = "fecha_baja";
        String fechaNacimiento = "fecha_nacimiento";

        persona.setIdPersona(rs.getInt("id"));
        persona.setNombre(rs.getString("nombre"));
        persona.setApellidos(rs.getString("apellidos"));
        persona.setMail(rs.getString("mail"));
        persona.setDireccion(String.valueOf(rs.getString("direccion")));
        persona.setPais(rs.getString("pais"));
        persona.setTelefono(rs.getString("telefono"));
        persona.setGenero(Genero.fromString(rs.getString("genero")));
        persona.setDni(rs.getString("dni"));
        persona.setContrasena(rs.getString("contrasena"));
        LocalDate fecha = convFechas(fechaAlta,rs);
        persona.setFechaAlta(fecha);
        fecha = convFechas(fechaBaja,rs);
        persona.setFechaBaja(fecha);
        fecha = convFechas(fechaNacimiento,rs);
        persona.setFechaNacimiento(fecha);

        Personalidad personalidad = new Personalidad();
        personalidad.setIdPersona(rs.getInt("id"));
        personalidad.setRitmo(rs.getInt("ritmo"));
        personalidad.setComunicacion(rs.getInt("comunicacion"));
        personalidad.setExpresividad(rs.getInt("expresividad"));
        personalidad.setCaracter(rs.getInt("caracter"));
        personalidad.setNaturaleza(rs.getInt("naturaleza"));
        persona.setPersonalidad(personalidad);

        return persona;


    }
    //metodo para calcular la edad de los usuarios

    //metodo para transformar fechas fechas
    private LocalDate convFechas (String campo,ResultSet rs) throws SQLException {
        Date fecha = rs.getDate(campo);
        if (campo.equals("fecha_baja") && fecha == null) {return null;}
        return fecha.toLocalDate();

    }
}
