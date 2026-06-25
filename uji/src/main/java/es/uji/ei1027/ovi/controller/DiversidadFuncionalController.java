package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.DiversidadFuncionalDao;
import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import es.uji.ei1027.ovi.modelo.OviUser.TipoDiversidadFuncional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
//comentario par github 
@Controller
@RequestMapping("/DiversidadFuncional")
public class DiversidadFuncionalController {
    private DiversidadFuncionalDao diversidadFuncionalDao;
    @Autowired
    public void  setDiversidadFuncionalDao(DiversidadFuncionalDao dao) {diversidadFuncionalDao = dao;}

    @RequestMapping("/listaID/{id}")
    public String listaID(@PathVariable int id, Model model,
                          @RequestParam(defaultValue = "0") int page){

        int pageSize = 10;
        java.util.List<DiversidadFuncional> todas = diversidadFuncionalDao.obtenerDiverdadesPorId(id);
        int total = todas.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int from = page * pageSize;
        int to = Math.min(from + pageSize, total);
        java.util.List<DiversidadFuncional> pagina = (from <= to) ? todas.subList(from, to) : java.util.Collections.emptyList();

        model.addAttribute("id", id);
        model.addAttribute("diversidades", pagina);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
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
    @GetMapping("/details/{idDiversidad}")
    public String details(@PathVariable int idDiversidad, Model model) {
        DiversidadFuncional diversidadFuncional =
                diversidadFuncionalDao.obtenerDiversidadFuncionalPorId(idDiversidad);

        model.addAttribute("diversidadFuncional", diversidadFuncional);

        return "DiversidadFuncional/details";
    }

    @PostMapping("/create/{id}")
    public String create(@PathVariable int id, @ModelAttribute("diversidadFuncional") DiversidadFuncional diversidadFuncional) {
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




