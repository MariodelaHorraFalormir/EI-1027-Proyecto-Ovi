package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PersonaService;
import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.PersonaDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.Persona.Persona;
import es.uji.ei1027.ovi.modelo.Solicitud.CategoriaSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;

@Controller
@RequestMapping("/OviUser")
public class OviUserController {
    private SolicitudesDao solicitudesDao;
    private OviUserDao oviUserDao;
    private PersonaService personaService;
    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {this.solicitudesDao = solicitudDao;}
    @Autowired
    public void setPersonaService(PersonaService personaService) {this.personaService = personaService;}
    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }
    @GetMapping("/create/{id}")
    public String mostrarFormularioRegistro(Model model , @PathVariable int id) {
        Solicitud solicitud = new Solicitud();
        solicitud.setPersonaSolicitante(id);
        solicitud.setCategoriaSolicitud(CategoriaSolicitud.Rol);
        solicitud.setTipoSolicitud(TipoSolicitud.Ovi_user);
        solicitud.setEstadoSolicitud(EstadoSolicitud.Pendiente);
        OviUser oviUser = new OviUser();
        oviUser.setIdOviUser(id);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("solicitud", solicitud);
        return "OviUser/create";
    }
    @PostMapping("/create/{id}")
    public String procesarRegistro(
            @ModelAttribute("oviUser") OviUser oviUser,@ModelAttribute("solicitud") Solicitud solicitud ,
            BindingResult bindingResult,
            Model model , @PathVariable int id) {

        if (bindingResult.hasErrors()) {
            return "OviUser/create";
        }
        try {
            oviUserDao.addOviUser(oviUser);
            solicitudesDao.createSolicitud(solicitud);
        }catch (Exception e){
            return "OviUser/create";
        }



        return "redirect:/DiversidadFuncional/listaID/" + id;
    }





}
