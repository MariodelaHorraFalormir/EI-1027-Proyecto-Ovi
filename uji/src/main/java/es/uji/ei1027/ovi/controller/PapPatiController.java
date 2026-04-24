package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.modelo.PapPati;
import es.uji.ei1027.ovi.modelo.Persona;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pappati")
public class PapPatiController {

    @GetMapping("/add")
    public String addPappati(Model model) {
        model.addAttribute("pappati", new PapPati());

        return "PapPati/add_pappati";
    }
}