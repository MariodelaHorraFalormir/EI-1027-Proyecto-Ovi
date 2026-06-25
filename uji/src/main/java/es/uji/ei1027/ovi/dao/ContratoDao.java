package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.modelo.Contrato.Contrato;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ContratoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Creación automática de la tabla
    // Creación automática y FORZADA de la tabla
    @PostConstruct
    public void crearTablaSiNoExiste() {
        try {
            // 1. Borramos la tabla vieja para que no dé por saco
            jdbcTemplate.execute("DROP TABLE IF EXISTS contrato");

            // 2. Creamos la nueva con todas las columnas
            String sql = "CREATE TABLE contrato (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_solicitud INT NOT NULL, " +
                    "id_usuario_ovi INT NOT NULL, " +
                    "id_pap_pati INT NOT NULL, " +
                    "fecha_inicio DATE NOT NULL, " +
                    "fecha_fin DATE, " +
                    "estado VARCHAR(50) DEFAULT 'Activo'" +
                    ")";
            jdbcTemplate.execute(sql);
            System.out.println("✅ ¡Tabla 'contrato' RECREADA desde cero con éxito!");
        } catch (Exception e) {
            System.out.println("⚠️ Error al recrear la tabla contrato: " + e.getMessage());
        }
    }

    // Guardar un nuevo contrato
    public void addContrato(Contrato contrato) {
        String sql = "INSERT INTO contrato (id_solicitud, id_usuario_ovi, id_pap_pati, fecha_inicio, fecha_fin, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                contrato.getIdSolicitud(),
                contrato.getIdUsuarioOvi(),
                contrato.getIdPapPati(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                "Activo"
        );
    }

    // Obtener contratos de un usuario (para que los vea en su perfil)
    public List<Contrato> getContratosPorUsuario(int idUsuario) {
        String sql = "SELECT * FROM contrato WHERE id_usuario_ovi = ? OR id_pap_pati = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Contrato c = new Contrato();
            c.setId(rs.getInt("id"));
            c.setIdSolicitud(rs.getInt("id_solicitud"));
            c.setIdUsuarioOvi(rs.getInt("id_usuario_ovi"));
            c.setIdPapPati(rs.getInt("id_pap_pati"));
            if (rs.getDate("fecha_inicio") != null) c.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
            if (rs.getDate("fecha_fin") != null) c.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
            c.setEstado(rs.getString("estado"));
            return c;
        }, idUsuario, idUsuario);
    }

    // Obtener un contrato específico por su ID (para poder editarlo)
    public Contrato getContratoPorId(int id) {
        String sql = "SELECT * FROM contrato WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Contrato c = new Contrato();
                c.setId(rs.getInt("id"));
                c.setIdSolicitud(rs.getInt("id_solicitud"));
                c.setIdUsuarioOvi(rs.getInt("id_usuario_ovi"));
                c.setIdPapPati(rs.getInt("id_pap_pati"));
                if (rs.getDate("fecha_inicio") != null) c.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                if (rs.getDate("fecha_fin") != null) c.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                c.setEstado(rs.getString("estado"));
                return c;
            }, id);
        } catch (Exception e) {
            return null;
        }
    }

    // Actualizar un contrato existente
    public void updateContrato(Contrato contrato) {
        String sql = "UPDATE contrato SET fecha_inicio = ?, fecha_fin = ?, estado = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getEstado(),
                contrato.getId()
        );
    }

    public List<Contrato> getTodosLosContratos() {
        String sql = "SELECT * FROM contrato ORDER BY id DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Contrato c = new Contrato();
            c.setId(rs.getInt("id"));
            c.setIdSolicitud(rs.getInt("id_solicitud"));
            c.setIdUsuarioOvi(rs.getInt("id_usuario_ovi"));
            c.setIdPapPati(rs.getInt("id_pap_pati"));
            if (rs.getDate("fecha_inicio") != null) c.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
            if (rs.getDate("fecha_fin") != null) c.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
            c.setEstado(rs.getString("estado"));
            return c;
        });
    }
}