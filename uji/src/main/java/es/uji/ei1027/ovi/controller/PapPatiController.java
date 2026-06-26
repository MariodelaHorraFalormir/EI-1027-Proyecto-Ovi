package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.Service.PapPatiService;
import es.uji.ei1027.ovi.Service.SesionService;
import es.uji.ei1027.ovi.Service.SolicitudesService;
import es.uji.ei1027.ovi.Validadores.PapPatiValidator;
import es.uji.ei1027.ovi.dao.EspecialidadesDao;
import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.dao.SolicitudesDao;
import es.uji.ei1027.ovi.modelo.OviUser.TipoDiversidadFuncional;
import es.uji.ei1027.ovi.modelo.PapPati.Especialidad;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/PapPati")
public class PapPatiController {

    private SolicitudesDao solicitudesDao;
    private PapPatiDao papPatiDao;
    private EspecialidadesDao especialidadesDao;
    private SolicitudesService solicitudesService;
    private PapPatiService papPatiService;
    private SesionService sesionService;

    private static final long MAX_CV_SIZE = 5 * 1024 * 1024; // 5 MB

    private static final Path CV_UPLOAD_DIR =
            Paths.get("uploads", "cv").toAbsolutePath().normalize();

    @Autowired
    public void setPapPatiService(PapPatiService papPatiService) {
        this.papPatiService = papPatiService;
    }

    @Autowired
    public void setSolicitudesService(SolicitudesService solicitudesService) {
        this.solicitudesService = solicitudesService;
    }

    @Autowired
    public void setSolicitudDao(SolicitudesDao solicitudDao) {
        this.solicitudesDao = solicitudDao;
    }

    @Autowired
    public void setPersonaService(EspecialidadesDao especialidadesDao) {
        this.especialidadesDao = especialidadesDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setSesionService(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    private boolean esCvPdfValido(MultipartFile cvFile) {
        if (cvFile == null || cvFile.isEmpty()) {
            return false;
        }

        String nombreOriginal = cvFile.getOriginalFilename();

        if (nombreOriginal == null) {
            return false;
        }

        return nombreOriginal.toLowerCase().endsWith(".pdf");
    }

    private String guardarCvPdf(MultipartFile cvFile, int idPersona) throws IOException {
        Files.createDirectories(CV_UPLOAD_DIR);

        String nombreArchivo = "cv_pappati_" + idPersona + "_" + UUID.randomUUID() + ".pdf";

        Path destino = CV_UPLOAD_DIR.resolve(nombreArchivo).normalize();

        Files.copy(cvFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/cv/" + nombreArchivo;
    }

    private boolean tieneCvGuardado(String urlCV) {
        return urlCV != null && !urlCV.trim().isEmpty();
    }

    private boolean esRutaCvLocal(String urlCV) {
        return urlCV != null && urlCV.startsWith("/uploads/cv/");
    }

    private void gestionarCvCreate(
            PapPati papPati,
            MultipartFile cvFile,
            BindingResult bindingResult,
            int idPersona
    ) {
        boolean yaTieneCvGuardado = tieneCvGuardado(papPati.getUrlCV());

        if (cvFile == null || cvFile.isEmpty()) {
            if (!yaTieneCvGuardado) {
                bindingResult.rejectValue(
                        "urlCV",
                        "cvFile.empty",
                        "Debes subir el CV en formato PDF."
                );
            }

            return;
        }

        if (!esCvPdfValido(cvFile)) {
            bindingResult.rejectValue(
                    "urlCV",
                    "cvFile.invalid",
                    "El CV debe ser un archivo PDF."
            );
            return;
        }

        if (cvFile.getSize() > MAX_CV_SIZE) {
            bindingResult.rejectValue(
                    "urlCV",
                    "cvFile.size",
                    "El CV no puede superar los 5 MB."
            );
            return;
        }

        try {
            String cvAnteriorFormulario = papPati.getUrlCV();
            String rutaCv = guardarCvPdf(cvFile, idPersona);
            papPati.setUrlCV(rutaCv);

            if (esRutaCvLocal(cvAnteriorFormulario) && !cvAnteriorFormulario.equals(rutaCv)) {
                borrarCvSiExiste(cvAnteriorFormulario);
            }

        } catch (IOException e) {
            bindingResult.rejectValue(
                    "urlCV",
                    "cvFile.error",
                    "No se ha podido guardar el CV."
            );
        }
    }

    private void gestionarCvUpdate(
            PapPati papPati,
            PapPati papPatiOriginal,
            MultipartFile cvFile,
            BindingResult bindingResult,
            int idPersona
    ) {
        if (cvFile == null || cvFile.isEmpty()) {
            return;
        }

        if (!esCvPdfValido(cvFile)) {
            bindingResult.rejectValue(
                    "urlCV",
                    "cvFile.invalid",
                    "El CV debe ser un archivo PDF."
            );
            return;
        }

        if (cvFile.getSize() > MAX_CV_SIZE) {
            bindingResult.rejectValue(
                    "urlCV",
                    "cvFile.size",
                    "El CV no puede superar los 5 MB."
            );
            return;
        }

        try {
            String cvAnteriorFormulario = papPati.getUrlCV();
            String cvOriginalBaseDatos = papPatiOriginal.getUrlCV();
            String nuevaRutaCv = guardarCvPdf(cvFile, idPersona);

            papPati.setUrlCV(nuevaRutaCv);

            if (esRutaCvLocal(cvAnteriorFormulario)
                    && !cvAnteriorFormulario.equals(cvOriginalBaseDatos)
                    && !cvAnteriorFormulario.equals(nuevaRutaCv)) {
                borrarCvSiExiste(cvAnteriorFormulario);
            }

        } catch (IOException e) {
            bindingResult.rejectValue(
                    "urlCV",
                    "cvFile.error",
                    "No se ha podido guardar el CV."
            );
        }
    }

    private void recargarFormularioCreate(Model model,
                                          Solicitud solicitud,
                                          PapPati papPati,
                                          List<String> especialidadesSeleccionadas) {
        model.addAttribute("papPati", papPati);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("Especialidades", TipoDiversidadFuncional.getLista());

        if (especialidadesSeleccionadas == null) {
            model.addAttribute("especialidadesSeleccionadas", List.of());
        } else {
            model.addAttribute("especialidadesSeleccionadas", especialidadesSeleccionadas);
        }
    }

    private void recargarFormularioUpdate(Model model,
                                          PapPati papPati,
                                          List<String> especialidadesSeleccionadas) {
        model.addAttribute("papPati", papPati);
        model.addAttribute("Especialidades", TipoDiversidadFuncional.getLista());

        if (especialidadesSeleccionadas == null) {
            model.addAttribute("especialidadesSeleccionadas", List.of());
        } else {
            model.addAttribute("especialidadesSeleccionadas", especialidadesSeleccionadas);
        }
    }

    @GetMapping("/solicitud")
    public String gestionarSolicitudOviUser(HttpSession session) {

        if (!sesionService.hayUsuarioLogueado(session)) {
            sesionService.guardarNextUrl(session, "/PapPati/solicitud");
            return "redirect:/login";
        }

        int idPersona = sesionService.getUsuario(session).getIdPersona();
        String rutaDestino = papPatiService.obtenerRutaSolicitudPapPati(idPersona);

        return "redirect:" + rutaDestino;
    }

    @GetMapping("/create/{id}")
    public String mostrarFormularioRegistro(Model model, @PathVariable int id, HttpSession session) {
        Solicitud solicitud = solicitudesService.solicitudRol(id, TipoSolicitud.Pap_pati);
        String url = "/PapPati/create/" + id;

        if (!sesionService.hayUsuarioLogueado(session)) {
            return sesionService.redirigirALogin(session, url);
        }

        int idPersona = sesionService.getUsuario(session).getIdPersona();

        if (idPersona != id) {
            return "redirect:/";
        }

        if (papPatiDao.getPapPati(id) != null) {
            return "redirect:/PapPati/solicitud";
        }

        PapPati papPati = new PapPati();
        papPati.setIdPatPati(id);

        model.addAttribute("papPati", papPati);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("Especialidades", TipoDiversidadFuncional.getLista());
        model.addAttribute("especialidadesSeleccionadas", List.of());

        return "PapPati/create";
    }

    @PostMapping("/create/{id}")
    public String procesarRegistro(
            @PathVariable int id,
            @ModelAttribute("papPati") PapPati papPati,
            BindingResult bindingResult,
            @ModelAttribute("solicitud") Solicitud solicitud,
            Model model,
            @RequestParam(value = "especialidadesSeleccionadas", required = false) List<String> especialidades,
            @RequestParam(value = "cvFile", required = false) MultipartFile cvFile) {

        papPati.setIdPatPati(id);

        PapPatiValidator validator = new PapPatiValidator();
        validator.validate(papPati, bindingResult);

        gestionarCvCreate(papPati, cvFile, bindingResult, id);

        if (bindingResult.hasErrors()) {
            recargarFormularioCreate(model, solicitud, papPati, especialidades);
            return "PapPati/create";
        }

        solicitudesDao.createSolicitud(solicitud);
        papPatiDao.crear(papPati);

        if (especialidades != null) {
            for (String esp : especialidades) {
                especialidadesDao.addEspecialidad(id, esp);
            }
        }

        return "redirect:/";
    }

    @GetMapping("/update/{id}")
    public String editPersona(Model model, @PathVariable int id) {
        PapPati papPati = papPatiDao.getPapPati(id);

        if (papPati == null) {
            return "redirect:/";
        }

        List<String> especialidadesSeleccionadas = new ArrayList<>();

        if (papPati.getEspecialidades() != null) {
            for (Especialidad especialidad : papPati.getEspecialidades()) {
                especialidadesSeleccionadas.add(especialidad.getDiversidadFuncional().getTexto());
            }
        }

        model.addAttribute("papPati", papPati);
        model.addAttribute("Especialidades", TipoDiversidadFuncional.getLista());
        model.addAttribute("especialidadesSeleccionadas", especialidadesSeleccionadas);

        return "PapPati/update";
    }

    @PostMapping("/update/{id}")
    public String procesarActualizarPapPati(
            @PathVariable int id,
            @ModelAttribute("papPati") PapPati papPati,
            BindingResult bindingResult,
            Model model,
            @RequestParam(value = "especialidadesSeleccionadas", required = false)
            List<String> especialidadesSeleccionadas,
            @RequestParam(value = "cvFile", required = false)
            MultipartFile cvFile) {

        papPati.setIdPatPati(id);

        PapPati papPatiOriginal = papPatiDao.getPapPati(id);

        if (papPatiOriginal == null) {
            return "redirect:/";
        }

        if (!tieneCvGuardado(papPati.getUrlCV())) {
            papPati.setUrlCV(papPatiOriginal.getUrlCV());
        }

        PapPatiValidator validator = new PapPatiValidator();
        validator.validate(papPati, bindingResult);

        gestionarCvUpdate(papPati, papPatiOriginal, cvFile, bindingResult, id);

        if (bindingResult.hasErrors()) {
            recargarFormularioUpdate(model, papPati, especialidadesSeleccionadas);
            return "PapPati/update";
        }

        String cvAntiguo = papPatiOriginal.getUrlCV();
        String cvFinal = papPati.getUrlCV();

        if (!tieneCvGuardado(cvFinal)) {
            cvFinal = cvAntiguo;
            papPati.setUrlCV(cvFinal);
        }

        especialidadesDao.deleteAllbyId(papPati.getIdPatPati());

        if (especialidadesSeleccionadas != null) {
            for (String esp : especialidadesSeleccionadas) {
                especialidadesDao.addEspecialidad(id, esp);
            }
        }

        papPatiDao.update(papPati);

        if (tieneCvGuardado(cvFinal) && !cvFinal.equals(cvAntiguo)) {
            borrarCvSiExiste(cvAntiguo);
        }

        return "redirect:/PapPati/details/" + id;
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable int id, Model model) {
        PapPati papPati = papPatiDao.getPapPati(id);

        if (papPati == null) {
            return "redirect:/";
        }

        model.addAttribute("papPati", papPati);
        model.addAttribute("especialidadesSeleccionadas", papPati.getEspecialidadesNombre());

        return "PapPati/details";
    }

    private void borrarCvSiExiste(String urlCV) {
        if (urlCV == null || urlCV.trim().isEmpty()) {
            return;
        }

        if (!urlCV.startsWith("/uploads/cv/")) {
            return;
        }

        String nombreArchivo = urlCV.substring("/uploads/cv/".length());
        Path rutaArchivo = CV_UPLOAD_DIR.resolve(nombreArchivo).normalize();

        if (!rutaArchivo.startsWith(CV_UPLOAD_DIR)) {
            return;
        }

        try {
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            System.err.println("No se pudo borrar el CV: " + rutaArchivo);
        }
    }
}
