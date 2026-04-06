package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.DiversidadFuncionalDao;
import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import es.uji.ei1027.ovi.modelo.OviUser.TipoDiversidadFuncional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/DiversidadFuncional")
public class DiversidadFuncionalController {
    private DiversidadFuncionalDao diversidadFuncionalDao;
    @Autowired
    public void  setDiversidadFuncionalDao(DiversidadFuncionalDao dao) {diversidadFuncionalDao = dao;}
    @RequestMapping("/listaID/{id}")
    public String listaID(@PathVariable int id, Model model){

        model.addAttribute("id",id);
        model.addAttribute("diversidades",diversidadFuncionalDao.obtenerDiverdadesPorId(id));
        return "DiversidadFuncional/listaID";

    }
    @RequestMapping("/delete/{idDiversidad}/{idUsuario}")
    public String processDelete(@PathVariable int idDiversidad, @PathVariable int idUsuario) {
        diversidadFuncionalDao.borrarDiversidadFuncional(idDiversidad);
        return "redirect:/DiversidadFuncional/listaID/" + idUsuario;
    }
    @GetMapping("/create/{id}")
    public String createForm(@PathVariable int id, Model model) {
        DiversidadFuncional diversidadFuncional = new DiversidadFuncional();
        diversidadFuncional.setOviUserId(id);

        model.addAttribute("diversidadFuncional", diversidadFuncional);
        model.addAttribute("listaTipos", TipoDiversidadFuncional.getLista());

        return "DiversidadFuncional/create";
    }

    @PostMapping("/create/{id}")
    public String create(
                         @ModelAttribute("diversidadFuncional") DiversidadFuncional diversidadFuncional) {
        int id = diversidadFuncional.getOviUserId();
        diversidadFuncional.setOviUserId(id);
        diversidadFuncionalDao.addDiversidadFuncional(diversidadFuncional);

        return "redirect:/DiversidadFuncional/listaID/" + id;
    }

    @GetMapping("/update/{idDiversidad}")
    public String updateForm(@PathVariable int idDiversidad, Model model) {
        DiversidadFuncional diversidadFuncional =
                diversidadFuncionalDao.obtenerDiversidadFuncionalPorId(idDiversidad);

        model.addAttribute("diversidadFuncional", diversidadFuncional);
        model.addAttribute("listaTipos", TipoDiversidadFuncional.getLista());

        return "DiversidadFuncional/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("diversidadFuncional") DiversidadFuncional diversidadFuncional) {
        diversidadFuncionalDao.updateDiversidadFuncional(diversidadFuncional);
        return "redirect:/DiversidadFuncional/listaID/" + diversidadFuncional.getOviUserId();
    }
}




