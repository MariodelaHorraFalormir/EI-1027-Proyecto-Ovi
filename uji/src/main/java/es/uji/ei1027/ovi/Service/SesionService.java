package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;
import es.uji.ei1027.ovi.modelo.Roles.RolUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SesionService {

    private static final String claveUsuario = "usuario";
    private static final String claveNextUrl = "nextUrl";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UsuarioSesion getUsuario(HttpSession session) {
        return (UsuarioSesion) session.getAttribute(claveUsuario);
    }

    public void guardarUsuario(HttpSession session, UsuarioSesion usuario) {
        session.setAttribute(claveUsuario, usuario);
    }

    public boolean hayUsuarioLogueado(HttpSession session) {
        return getUsuario(session) != null;
    }

    public String redirigirDespuesDelLogin(HttpSession session) {
        String nextUrl = obtenerYLimpiarNextUrl(session);

        if (nextUrl != null) {
            return "redirect:" + nextUrl;
        }

        return "redirect:/";
    }

    public void cerrarSesion(HttpSession session) {
        session.invalidate();
    }

    public void guardarNextUrl(HttpSession session, String nextUrl) {
        session.setAttribute(claveNextUrl, nextUrl);
    }

    public String obtenerNextUrl(HttpSession session) {
        return (String) session.getAttribute(claveNextUrl);
    }

    public String obtenerYLimpiarNextUrl(HttpSession session) {
        String nextUrl = obtenerNextUrl(session);
        session.removeAttribute(claveNextUrl);
        return nextUrl;
    }

    public String redirigirALogin(HttpSession session, String nextUrl) {
        guardarNextUrl(session, nextUrl);
        return "redirect:/login";
    }

    public void refrescarRolesUsuarioSesion(HttpSession session) {
        UsuarioSesion usuario = getUsuario(session);

        if (usuario == null) {
            return;
        }

        int idPersona = usuario.getIdPersona();

        List<RolUsuario> rolesActivos = new ArrayList<>();
        List<RolUsuario> rolesExistentes = new ArrayList<>();

        if (existeAdminOvi(idPersona)) {
            rolesExistentes.add(RolUsuario.Admin_ovi);
            rolesActivos.add(RolUsuario.Admin_ovi);
        }

        EstadoRol estadoOviUser = obtenerEstadoRol("ovi_user", idPersona);

        if (estadoOviUser != null) {
            rolesExistentes.add(RolUsuario.Ovi_user);

            if (estadoOviUser == EstadoRol.Activo) {
                rolesActivos.add(RolUsuario.Ovi_user);
            }
        }

        EstadoRol estadoPapPati = obtenerEstadoRol("pap_pati", idPersona);

        if (estadoPapPati != null) {
            rolesExistentes.add(RolUsuario.Pap_pati);

            if (estadoPapPati == EstadoRol.Activo) {
                rolesActivos.add(RolUsuario.Pap_pati);
            }
        }

        usuario.setRolesActivos(rolesActivos);
        usuario.setRolesExistentes(rolesExistentes);
        usuario.setEstadoOviUser(estadoOviUser);
        usuario.setEstadoPapPati(estadoPapPati);

        guardarUsuario(session, usuario);
    }

    private boolean existeAdminOvi(int idPersona) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM admin_ovi WHERE id = ?",
                Integer.class,
                idPersona
        );

        return count != null && count > 0;
    }

    private EstadoRol obtenerEstadoRol(String tabla, int idPersona) {
        try {
            String sql = "SELECT estado FROM " + tabla + " WHERE id = ?";

            String estado = jdbcTemplate.queryForObject(
                    sql,
                    String.class,
                    idPersona
            );

            if (estado == null || estado.isBlank()) {
                return null;
            }

            return EstadoRol.valueOf(estado);

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}