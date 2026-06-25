package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.modelo.Chat.Mensaje;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class MensajeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // --- EL TRUCO MÁGICO PARA CREAR LA TABLA AUTOMÁTICAMENTE ---
    // --- EL TRUCO MÁGICO REVISADO ---
    @PostConstruct
    public void crearTablaSiNoExiste() {
        try {
            // 1. Borramos la tabla vieja si existe para evitar conflictos
            jdbcTemplate.execute("DROP TABLE IF EXISTS mensaje");

            // 2. Creamos la nueva con todas las columnas correctas
            String sql = "CREATE TABLE mensaje (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_solicitud INT NOT NULL, " +
                    "id_emisor INT NOT NULL, " +
                    "id_receptor INT NOT NULL, " +
                    "contenido TEXT NOT NULL, " +
                    "fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            jdbcTemplate.execute(sql);
            System.out.println("✅ ¡Tabla 'mensaje' RECREADA con éxito con las columnas correctas!");
        } catch (Exception e) {
            System.out.println("⚠️ Error al recrear la tabla: " + e.getMessage());
        }
    }
    // -----------------------------------------------------------
    // -----------------------------------------------------------

    // Guardar un mensaje nuevo
    public void addMensaje(Mensaje mensaje) {
        String sql = "INSERT INTO mensaje (id_solicitud, id_emisor, id_receptor, contenido) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                mensaje.getIdSolicitud(),
                mensaje.getIdEmisor(),
                mensaje.getIdReceptor(),
                mensaje.getContenido()
        );
    }

    // Recuperar toda la conversación
    public List<Mensaje> getMensajesPorSolicitud(int idSolicitud) {
        String sql = "SELECT * FROM mensaje WHERE id_solicitud = ? ORDER BY fecha_envio ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mensaje m = new Mensaje();
            m.setId(rs.getInt("id"));
            m.setIdSolicitud(rs.getInt("id_solicitud"));
            m.setIdEmisor(rs.getInt("id_emisor"));
            m.setIdReceptor(rs.getInt("id_receptor"));
            m.setContenido(rs.getString("contenido"));
            if (rs.getTimestamp("fecha_envio") != null) {
                m.setFechaEnvio(rs.getTimestamp("fecha_envio").toLocalDateTime());
            }
            return m;
        }, idSolicitud);
    }

    public List<Map<String, Object>> getTodasLasConversaciones() {
        String sql =
                "SELECT id_solicitud, " +
                        "LEAST(id_emisor, id_receptor) AS persona_a, " +
                        "GREATEST(id_emisor, id_receptor) AS persona_b, " +
                        "COUNT(*) AS num_mensajes, " +
                        "MAX(fecha_envio) AS ultimo_mensaje " +
                        "FROM mensaje " +
                        "GROUP BY id_solicitud, LEAST(id_emisor, id_receptor), GREATEST(id_emisor, id_receptor) " +
                        "ORDER BY ultimo_mensaje DESC";
        return jdbcTemplate.queryForList(sql);
    }
}