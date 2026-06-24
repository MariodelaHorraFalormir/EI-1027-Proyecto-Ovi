package es.uji.ei1027.ovi.Validadores;

import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OviUserValidator implements Validator {

    private static final int MIN_GRADO_DIVERSIDAD = 0;
    private static final int MAX_GRADO_DIVERSIDAD = 100;

    private static final int MIN_GRADO_DEPENDENCIA = 1;
    private static final int MAX_GRADO_DEPENDENCIA = 3;

    private static final int MAX_CENTRO_SOCIAL = 100;
    private static final int MAX_URL_PROYECTO_VIDA = 255;

    @Override
    public boolean supports(Class<?> cls) {
        return OviUser.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        OviUser oviUser = (OviUser) obj;

        Integer gradoDiversidadFuncional = oviUser.getGradoDiversidadFuncional();
        Integer gradoDependencia = oviUser.getGradoDependencia();
        String centroSocialReferencia = oviUser.getCentroSocialReferencia();
        String urlProyectoDeVida = oviUser.getUrlProyectoDeVida();

        // Grado de diversidad funcional
        if (gradoDiversidadFuncional == null) {
            errors.rejectValue(
                    "gradoDiversidadFuncional",
                    "obligatorio",
                    "Debes indicar el grado de diversidad funcional"
            );
        } else if (gradoDiversidadFuncional < MIN_GRADO_DIVERSIDAD ||
                gradoDiversidadFuncional > MAX_GRADO_DIVERSIDAD) {
            errors.rejectValue(
                    "gradoDiversidadFuncional",
                    "rango",
                    "El grado de diversidad funcional debe estar entre 0 y 100"
            );
        }

        // Grado de dependencia
        if (gradoDependencia == null) {
            errors.rejectValue(
                    "gradoDependencia",
                    "obligatorio",
                    "Debes indicar el grado de dependencia"
            );
        } else if (gradoDependencia < MIN_GRADO_DEPENDENCIA ||
                gradoDependencia > MAX_GRADO_DEPENDENCIA) {
            errors.rejectValue(
                    "gradoDependencia",
                    "rango",
                    "El grado de dependencia debe estar entre 1 y 3"
            );
        }

        // Centro social de referencia
        if (centroSocialReferencia == null || centroSocialReferencia.trim().isEmpty()) {
            errors.rejectValue(
                    "centroSocialReferencia",
                    "obligatorio",
                    "Debes indicar el centro social de referencia"
            );
        } else if (centroSocialReferencia.trim().length() > MAX_CENTRO_SOCIAL) {
            errors.rejectValue(
                    "centroSocialReferencia",
                    "longitud",
                    "El centro social de referencia no puede superar los 100 caracteres"
            );
        }

        // URL del proyecto de vida
        if (urlProyectoDeVida == null || urlProyectoDeVida.trim().isEmpty()) {
            errors.rejectValue(
                    "urlProyectoDeVida",
                    "obligatorio",
                    "Debes indicar la URL del proyecto de vida"
            );
        } else {
            String url = urlProyectoDeVida.trim();

            if (url.length() > MAX_URL_PROYECTO_VIDA) {
                errors.rejectValue(
                        "urlProyectoDeVida",
                        "longitud",
                        "La URL del proyecto de vida no puede superar los 255 caracteres"
                );
            } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                errors.rejectValue(
                        "urlProyectoDeVida",
                        "formato",
                        "La URL debe empezar por http:// o https://"
                );
            }
        }
    }
}