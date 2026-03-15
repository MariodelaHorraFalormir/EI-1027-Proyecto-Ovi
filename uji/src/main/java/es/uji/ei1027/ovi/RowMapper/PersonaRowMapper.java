package es.uji.ei1027.ovi.RowMapper;

import es.uji.ei1027.ovi.modelo.Persona;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.Period;

public class PersonaRowMapper implements RowMapper<Persona> {
    @Override
    public  Persona mapRow(ResultSet rs , int rowNum) throws SQLException {
        Persona persona = new Persona();
        String fechaAlta = "fecha_alta";
        String fechaBaja = "fecha_baja";
        String fechaNacimiento = "fecha_nacimiento";

        persona.setIdPersona(rs.getInt("id"));
        persona.setNombre(rs.getString("nombre"));
        persona.setApellido1(rs.getString("apellido_1"));
        persona.setApellido2(rs.getString("apellido_2"));
        persona.setEmail(rs.getString("mail"));
        persona.setTelefono(rs.getString("telefono"));
        persona.setSexo(rs.getString("sexo"));
        LocalDate fecha = convFechas(fechaAlta,rs);
        persona.setFechaAlta(fecha);
        fecha = convFechas(fechaBaja,rs);
        persona.setFechaBaja(fecha);

        fecha = convFechas(fechaNacimiento,rs);
        persona.setFechaNacimiento(fecha);
        Period periodo = Period.between(persona.getFechaNacimiento(), LocalDate.now());
        persona.setEdad(periodo.getYears());





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
