package es.uji.ei1027.ovi.WebConfig;

import es.uji.ei1027.ovi.Service.SesionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SesionRolesInterceptor implements HandlerInterceptor {

    @Autowired
    private SesionService sesionService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        HttpSession session = request.getSession(false);

        if (session != null && sesionService.hayUsuarioLogueado(session)) {
            sesionService.refrescarRolesUsuarioSesion(session);
        }

        return true;
    }
}