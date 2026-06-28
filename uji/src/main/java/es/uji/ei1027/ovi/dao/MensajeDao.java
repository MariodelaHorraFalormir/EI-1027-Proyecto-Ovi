package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.RowMapper.MensajeRowMapper;
import es.uji.ei1027.ovi.modelo.Chat.Mensaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class MensajeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Guardar mensaje nuevo asociado a una conversación
    public void addMensaje(Mensaje mensaje) {
        String sql = """
                INSERT INTO mensaje (
                    id_solicitud,
                    id_emisor,
                    id_receptor,
                    contenido,
                    conversacion
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                mensaje.getIdSolicitud(),
                mensaje.getIdEmisor(),
                mensaje.getIdReceptor(),
                mensaje.getContenido(),
                mensaje.getConversacion()
        );
    }

    // Recuperar mensajes de una conversación concreta
    public List<Mensaje> getMensajesPorConversacion(int idConversacion) {
        String sql = """
                SELECT *
                FROM mensaje
                WHERE conversacion = ?
                ORDER BY fecha_envio ASC, id ASC
                """;

        return jdbcTemplate.query(sql, new MensajeRowMapper(), idConversacion);
    }

    // Método antiguo de compatibilidad
    public List<Mensaje> getMensajesPorSolicitud(int idSolicitud) {
        String sql = """
                SELECT *
                FROM mensaje
                WHERE id_solicitud = ?
                ORDER BY fecha_envio ASC, id ASC
                """;

        return jdbcTemplate.query(sql, new MensajeRowMapper(), idSolicitud);
    }

    // Sacar datos de una conversación y su solicitud asociada
    public Map<String, Object> getConversacionById(int idConversacion) {
        String sql = """
            SELECT 
                c.id AS id_conversacion,
                c.pa_request AS id_solicitud,
                c.ovi_user,
                c.pap_pati,

                po.mail AS mail_ovi_user,
                po.nombre AS nombre_ovi_user,
                po.apellidos AS apellidos_ovi_user,

                pp.mail AS mail_pap_pati,
                pp.nombre AS nombre_pap_pati,
                pp.apellidos AS apellidos_pap_pati,

                p.status,
                p.tipo_asistencia,
                p.fecha_inicio,
                p.fecha_fin,
                p.zona_geografica,
                p.preferencias
            FROM conversacion c
            INNER JOIN pa_request p ON p.id = c.pa_request
            INNER JOIN persona po ON po.id = c.ovi_user
            INNER JOIN persona pp ON pp.id = c.pap_pati
            WHERE c.id = ?
            """;

        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(sql, idConversacion);

        if (resultado.isEmpty()) {
            return null;
        }

        return resultado.get(0);
    }

    // Comprobar si el usuario pertenece a la conversación
    public boolean usuarioPerteneceAConversacion(int idConversacion, int idPersona) {
        String sql = """
                SELECT COUNT(*)
                FROM conversacion
                WHERE id = ?
                AND (ovi_user = ? OR pap_pati = ?)
                """;

        Integer total = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                idConversacion,
                idPersona,
                idPersona
        );

        return total != null && total > 0;
    }

    // Sacar el otro participante de la conversación
    public Integer getReceptorEnConversacion(int idConversacion, int miId) {
        String sql = """
                SELECT 
                    CASE 
                        WHEN ovi_user = ? THEN pap_pati
                        WHEN pap_pati = ? THEN ovi_user
                        ELSE NULL
                    END AS receptor
                FROM conversacion
                WHERE id = ?
                """;

        List<Integer> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> {
            int valor = rs.getInt("receptor");
            return rs.wasNull() ? null : valor;
        }, miId, miId, idConversacion);

        if (resultado.isEmpty()) {
            return null;
        }

        return resultado.get(0);
    }

    // Sacar la solicitud/pa_request de una conversación
    public Integer getPaRequestDeConversacion(int idConversacion) {
        String sql = """
                SELECT pa_request
                FROM conversacion
                WHERE id = ?
                """;

        List<Integer> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> {
            int valor = rs.getInt("pa_request");
            return rs.wasNull() ? null : valor;
        }, idConversacion);

        if (resultado.isEmpty()) {
            return null;
        }

        return resultado.get(0);
    }

    // Para que los enlaces antiguos /chat/{idSolicitud}/{idReceptor} sigan funcionando
    public Integer getIdConversacionPorSolicitudYParticipantes(int idSolicitud,
                                                               int personaA,
                                                               int personaB) {
        String sql = """
                SELECT id
                FROM conversacion
                WHERE pa_request = ?
                AND (
                    (ovi_user = ? AND pap_pati = ?)
                    OR
                    (ovi_user = ? AND pap_pati = ?)
                )
                LIMIT 1
                """;

        List<Integer> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> {
            int valor = rs.getInt("id");
            return rs.wasNull() ? null : valor;
        }, idSolicitud, personaA, personaB, personaB, personaA);

        if (resultado.isEmpty()) {
            return null;
        }

        return resultado.get(0);
    }

    // Lista del usuario: primero agruparemos por solicitud en el controller
    public List<Map<String, Object>> getMisConversacionesAgrupadas(int miId) {
        String sql = """
            SELECT
                c.pa_request AS id_solicitud,
                c.id AS id_conversacion,
                c.ovi_user,
                c.pap_pati,

                po.nombre AS nombre_ovi_user,
                po.apellidos AS apellidos_ovi_user,
                po.mail AS mail_ovi_user,

                pp.nombre AS nombre_pap_pati,
                pp.apellidos AS apellidos_pap_pati,
                pp.mail AS mail_pap_pati,

                LEAST(c.ovi_user, c.pap_pati) AS persona_a,
                GREATEST(c.ovi_user, c.pap_pati) AS persona_b,

                CASE
                    WHEN c.ovi_user = ? THEN c.pap_pati
                    ELSE c.ovi_user
                END AS id_contacto,

                CASE
                    WHEN c.ovi_user = ? THEN pp.nombre
                    ELSE po.nombre
                END AS nombre_contacto,

                CASE
                    WHEN c.ovi_user = ? THEN pp.apellidos
                    ELSE po.apellidos
                END AS apellidos_contacto,

                CASE
                    WHEN c.ovi_user = ? THEN pp.mail
                    ELSE po.mail
                END AS mail_contacto,

                p.status,
                p.tipo_asistencia,
                p.fecha_inicio,
                p.fecha_fin,

                COUNT(m.id) AS num_mensajes,
                MAX(m.fecha_envio) AS ultimo_mensaje
            FROM conversacion c
            INNER JOIN pa_request p ON p.id = c.pa_request
            INNER JOIN persona po ON po.id = c.ovi_user
            INNER JOIN persona pp ON pp.id = c.pap_pati
            LEFT JOIN mensaje m ON m.conversacion = c.id
            WHERE c.ovi_user = ? OR c.pap_pati = ?
            GROUP BY
                c.pa_request,
                c.id,
                c.ovi_user,
                c.pap_pati,
                po.nombre,
                po.apellidos,
                po.mail,
                pp.nombre,
                pp.apellidos,
                pp.mail,
                p.status,
                p.tipo_asistencia,
                p.fecha_inicio,
                p.fecha_fin
            ORDER BY c.pa_request DESC, ultimo_mensaje DESC NULLS LAST, c.id DESC
            """;

        return jdbcTemplate.queryForList(sql, miId, miId, miId, miId, miId, miId);
    }
    // Lista admin: todas las conversaciones agrupables por solicitud
    public List<Map<String, Object>> getTodasLasConversaciones() {
        String sql = """
                SELECT
                    c.pa_request AS id_solicitud,
                    c.id AS id_conversacion,
                    c.ovi_user,
                    c.pap_pati,
                    LEAST(c.ovi_user, c.pap_pati) AS persona_a,
                    GREATEST(c.ovi_user, c.pap_pati) AS persona_b,
                    p.status,
                    p.tipo_asistencia,
                    p.fecha_inicio,
                    p.fecha_fin,
                    COUNT(m.id) AS num_mensajes,
                    MAX(m.fecha_envio) AS ultimo_mensaje
                FROM conversacion c
                INNER JOIN pa_request p ON p.id = c.pa_request
                LEFT JOIN mensaje m ON m.conversacion = c.id
                GROUP BY
                    c.pa_request,
                    c.id,
                    c.ovi_user,
                    c.pap_pati,
                    p.status,
                    p.tipo_asistencia,
                    p.fecha_inicio,
                    p.fecha_fin
                ORDER BY c.pa_request DESC, ultimo_mensaje DESC NULLS LAST, c.id DESC
                """;

        return jdbcTemplate.queryForList(sql);
    }
    public Integer getOviUserDePaRequest(int idSolicitud) {
        String sql = """
            SELECT ovi_user
            FROM pa_request
            WHERE id = ?
            """;

        List<Integer> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("ovi_user"), idSolicitud);

        if (resultado.isEmpty()) {
            return null;
        }

        return resultado.get(0);
    }

    public Integer getIdConversacionPorSolicitudOviPap(int idSolicitud, int idOviUser, int idPapPati) {
        String sql = """
            SELECT id
            FROM conversacion
            WHERE pa_request = ?
              AND ovi_user = ?
              AND pap_pati = ?
            LIMIT 1
            """;

        List<Integer> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id"),
                idSolicitud,
                idOviUser,
                idPapPati
        );

        if (resultado.isEmpty()) {
            return null;
        }

        return resultado.get(0);
    }

    public Integer crearConversacion(int idSolicitud, int idOviUser, int idPapPati) {
        String sql = """
            INSERT INTO conversacion (
                pa_request,
                ovi_user,
                pap_pati
            )
            VALUES (?, ?, ?)
            RETURNING id
            """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                idSolicitud,
                idOviUser,
                idPapPati
        );
    }

    public Integer crearORecuperarConversacion(int idSolicitud, int idOviUser, int idPapPati) {
        Integer idConversacionExistente = getIdConversacionPorSolicitudOviPap(
                idSolicitud,
                idOviUser,
                idPapPati
        );

        if (idConversacionExistente != null) {
            return idConversacionExistente;
        }

        return crearConversacion(idSolicitud, idOviUser, idPapPati);
    }
    public List<Map<String, Object>> getTodasLasConversacionesFiltradas(String filtroMail,
                                                                        String tipoAsistencia,
                                                                        String estado) {
        filtroMail = limpiarFiltro(filtroMail);
        tipoAsistencia = limpiarFiltro(tipoAsistencia);
        estado = limpiarFiltro(estado);

        StringBuilder sql = new StringBuilder("""
            SELECT
                c.pa_request AS id_solicitud,
                c.id AS id_conversacion,
                c.ovi_user,
                c.pap_pati,

                po.mail AS mail_ovi_user,
                po.nombre AS nombre_ovi_user,
                po.apellidos AS apellidos_ovi_user,

                pp.mail AS mail_pap_pati,
                pp.nombre AS nombre_pap_pati,
                pp.apellidos AS apellidos_pap_pati,

                LEAST(c.ovi_user, c.pap_pati) AS persona_a,
                GREATEST(c.ovi_user, c.pap_pati) AS persona_b,

                p.status,
                p.tipo_asistencia,
                p.fecha_inicio,
                p.fecha_fin,

                COUNT(m.id) AS num_mensajes,
                MAX(m.fecha_envio) AS ultimo_mensaje
            FROM conversacion c
            INNER JOIN pa_request p ON p.id = c.pa_request
            INNER JOIN persona po ON po.id = c.ovi_user
            INNER JOIN persona pp ON pp.id = c.pap_pati
            LEFT JOIN mensaje m ON m.conversacion = c.id
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        if (filtroMail != null) {
            sql.append("""
                AND (
                    LOWER(po.mail) LIKE LOWER(?)
                    OR LOWER(pp.mail) LIKE LOWER(?)
                )
                """);

            String filtro = "%" + filtroMail + "%";
            params.add(filtro);
            params.add(filtro);
        }

        if (tipoAsistencia != null) {
            sql.append("""
                AND LOWER(COALESCE(p.tipo_asistencia, '')) LIKE LOWER(?)
                """);

            params.add("%" + tipoAsistencia + "%");
        }

        if (estado != null) {
            sql.append("""
                AND p.status::text = ?
                """);

            params.add(estado);
        }

        sql.append("""
            GROUP BY
                c.pa_request,
                c.id,
                c.ovi_user,
                c.pap_pati,
                po.mail,
                po.nombre,
                po.apellidos,
                pp.mail,
                pp.nombre,
                pp.apellidos,
                p.status,
                p.tipo_asistencia,
                p.fecha_inicio,
                p.fecha_fin
            ORDER BY c.pa_request DESC, ultimo_mensaje DESC NULLS LAST, c.id DESC
            """);

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public List<String> getTiposAsistenciaDisponibles() {
        String sql = """
            SELECT DISTINCT tipo_asistencia
            FROM pa_request
            WHERE tipo_asistencia IS NOT NULL
              AND TRIM(tipo_asistencia) <> ''
            ORDER BY tipo_asistencia
            """;

        return jdbcTemplate.queryForList(sql, String.class);
    }

    private String limpiarFiltro(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }
}