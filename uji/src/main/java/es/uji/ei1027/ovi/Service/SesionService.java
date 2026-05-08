package es.uji.ei1027.ovi.Service;

import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SesionService {

    private static  String claveUsuario = "usuario";
    private static  String claveNextUrl = "nextUrl";

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
    public String redirigirALogin(HttpSession session, String nextUrl) {
        guardarNextUrl(session, nextUrl);
        return "redirect:/login";
    }

    public String obtenerYLimpiarNextUrl(HttpSession session) {
        String nextUrl = obtenerNextUrl(session);

        if (nextUrl != null) {
            session.removeAttribute(claveNextUrl);
        }

        return nextUrl;
    }
}