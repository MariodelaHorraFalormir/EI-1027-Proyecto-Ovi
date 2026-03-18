package es.uji.ei1027.ovi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
    @RequestMapping("/test")
    public String  testWeb(Model model){
        String mensaje = "prueba de funcionamiento";
        model.addAttribute("mensaje",mensaje);
        return "test";
    }
}
