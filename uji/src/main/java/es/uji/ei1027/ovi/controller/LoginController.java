package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.AuthService;
import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.Service.SesionService;
import es.uji.ei1027.ovi.modelo.Login.LoginForm;
import es.uji.ei1027.ovi.modelo.Login.UsuarioSesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private AuthService authService;
    private SesionService sesionService;

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setSesionService(SesionService sesionService) {
        this.sesionService = sesionService;
    }


    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        if (sesionService.hayUsuarioLogueado(session)) {
            return "redirect:/";
        }
        model.addAttribute("login", new LoginForm());
        return "login/login";
    }


    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute("login") LoginForm login,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {

        if (login.getMail() == null || login.getMail().trim().isEmpty()) {
            bindingResult.rejectValue("mail", "obligatorio", "Debes introducir el correo.");
        }

        if (login.getContrasena() == null || login.getContrasena().trim().isEmpty()) {
            bindingResult.rejectValue("contrasena", "obligatorio", "Debes introducir la contraseña.");
        }

        if (bindingResult.hasErrors()) {
            return "login/login";
        }

        UsuarioSesion usuarioSesion = authService.autenticar(
                login.getMail(),
                login.getContrasena()
        );

        if (usuarioSesion == null) {
            bindingResult.rejectValue("contrasena", "badLogin", "Correo o contraseña incorrectos.");
            return "login/login";
        }

       sesionService.guardarUsuario(session, usuarioSesion);

        return sesionService.redirigirDespuesDelLogin(session);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        sesionService.cerrarSesion(session);
        return "redirect:/";
    }
}