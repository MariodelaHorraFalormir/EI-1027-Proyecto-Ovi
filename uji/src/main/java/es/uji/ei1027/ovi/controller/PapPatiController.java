package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.dao.EspecialidadesDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.OviUser.TipoDiversidadFuncional;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Solicitud.CategoriaSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/PapPati")
public class PapPatiController {
    private SolicitudesDao solicitudesDao;
    private PapPatiDao papPatiDao;
    private EspecialidadesDao especialidadesDao;
    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {this.solicitudesDao = solicitudDao;}
    @Autowired
    public void setPersonaService(EspecialidadesDao especialidadesDao) {this.especialidadesDao = especialidadesDao;}
    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {this.papPatiDao = papPatiDao;
    }
    @GetMapping("/create/{id}")
    public String mostrarFormularioRegistro(Model model , @PathVariable int id) {
        Solicitud solicitud = new Solicitud();
        solicitud.setPersonaSolicitante(id);
        solicitud.setCategoriaSolicitud(CategoriaSolicitud.Rol);
        solicitud.setTipoSolicitud(TipoSolicitud.Pap_pati);
        solicitud.setEstadoSolicitud(EstadoSolicitud.Pendiente);
        PapPati papPati = new PapPati();
        papPati.setIdPatPati(id);
        model.addAttribute("papPati", papPati);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("Especialidades", TipoDiversidadFuncional.getLista());
        return "PapPati/create";
    }
    @PostMapping("/create/{id}")
    public String procesarRegistro(
            @ModelAttribute("papPati") PapPati papPati, @ModelAttribute("solicitud") Solicitud solicitud ,
            BindingResult bindingResult,
            Model model , @PathVariable int id , @RequestParam(value = "especialidadesSeleccionadas", required = false) List<String> especialidades) {

        if (bindingResult.hasErrors()) {
            return "PapPati/create";
        }
       // try {
            papPatiDao.crear(papPati);
            solicitudesDao.createSolicitud(solicitud);
            if (papPati.getEspecialidades() != null) {
                for (String esp : especialidades) {
                    especialidadesDao.addEspecialidad(id, esp);
                }
            }
        /*
        }catch (Exception e){
            return "PapPati/create";
        }
        */


        return "redirect:/";
    }
}
