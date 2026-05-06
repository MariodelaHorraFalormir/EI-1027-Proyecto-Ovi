package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
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

    private PersonaService personaService;

    @Autowired
    public void setPersonaService(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
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

        UsuarioSesion usuarioSesion = personaService.autenticar(
                login.getMail(),
                login.getContrasena()
        );

        if (usuarioSesion == null) {
            bindingResult.rejectValue("contrasena", "badLogin", "Correo o contraseña incorrectos.");
            return "login/login";
        }

        session.setAttribute("usuario", usuarioSesion);

        String nextUrl = (String) session.getAttribute("nextUrl");

        if (nextUrl != null) {
            session.removeAttribute("nextUrl");
            return "redirect:" + nextUrl;
        }

        return "redirect:/Persona/mi-perfil";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}