package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Solicitudes")
public class SolicitudController {
    private SolicitudesDao solicitudDao;


    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {this.solicitudDao = solicitudDao;}


    @RequestMapping("/listId")
    public String  listaporId(Model model){

        model.addAttribute("solicitudes", solicitudDao.getSolicitudesOrderId());
        return "Solicitudes/listId";
    }
    @RequestMapping("/detail/{id}")
    public String  detalles(Model model,@PathVariable int id){

        model.addAttribute("solicitud", solicitudDao.getSolicitudById(id));
        return "Solicitudes/detail";
    }
    @RequestMapping(value ="/update/{id}" ,method = RequestMethod.GET)
    public String  update(Model model,@PathVariable int id){
        Solicitud solicitud = solicitudDao.getSolicitudById(id);
        model.addAttribute("categoriaList", solicitud.getCategoriaSolicitud().getLista());
        model.addAttribute("tipoList", solicitud.getTipoSolicitud().getLista());
        model.addAttribute("estadoList", solicitud.getEstadoSolicitud().getLista());
        model.addAttribute("solicitud", solicitudDao.getSolicitudById(id));
        return "Solicitudes/update";
    }
    @PostMapping("/update/{id}")
    public String updateSolicitud(@PathVariable int id, @ModelAttribute("solicitud") Solicitud solicitud) {
        solicitudDao.updateSolicitud(id, solicitud);
        return "redirect:/Solicitudes/listId";
    }
    @RequestMapping("/aprobarRapido/{id}")
    public String  aprobarRapido(Model model,@PathVariable int id){

        solicitudDao.aprobarRapido(id);
        return "redirect:/Solicitudes/listId";
    }
    //esta en estado de pruebas la idea es que redirija a una  pagina donde te permita hem poner el motivo de la resolucion
    @RequestMapping("/rechazarRapido/{id}")
    public String  rechazarRapido(Model model,@PathVariable int id){
        //aqui cojera el metodo para mandarte a la pagina de lo de añadir motivo de rechazo
        solicitudDao.rechazarRapido(id);
        return "redirect:/Solicitudes/listId";
    }
    @RequestMapping("/delete/{id}")
    public String processDelete(@PathVariable int id) {
        solicitudDao.deleteSolicitud(id);
        return "redirect:/Solicitudes/listId";
    }

}
