package es.uji.ei1027.ovi.Validadores;

import es.uji.ei1027.ovi.modelo.PapPati.PapPati;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PapPatiValidator implements Validator {

    private static final int MAX_EXPERIENCIA = 80;
    private static final int MAX_CENTRO_SOCIAL = 150;
    private static final int MIN_DESCRIPCION = 20;
    private static final int MAX_DESCRIPCION = 1000;

    @Override
    public boolean supports(Class<?> clazz) {
        return PapPati.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PapPati papPati = (PapPati) target;

        validarDisponibilidad(papPati, errors);
        validarExperiencia(papPati, errors);
        validarFechasDisponibilidad(papPati, errors);
        validarCentroSocial(papPati, errors);
        validarDescripcionPerfil(papPati, errors);
    }

    private void validarDisponibilidad(PapPati papPati, Errors errors) {
        if (papPati.getDisponibilidad() == null) {
            errors.rejectValue(
                    "disponibilidad",
                    "obligatorio",
                    "Debes seleccionar una disponibilidad"
            );
        }
    }

    private void validarExperiencia(PapPati papPati, Errors errors) {
        if (papPati.getExperiencia() == null) {
            errors.rejectValue(
                    "experiencia",
                    "obligatorio",
                    "Debes indicar los años de experiencia"
            );
            return;
        }

        if (papPati.getExperiencia() < 0) {
            errors.rejectValue(
                    "experiencia",
                    "negativa",
                    "La experiencia no puede ser negativa"
            );
        } else if (papPati.getExperiencia() > MAX_EXPERIENCIA) {
            errors.rejectValue(
                    "experiencia",
                    "excesiva",
                    "Introduce una experiencia válida"
            );
        }
    }

    private void validarFechasDisponibilidad(PapPati papPati, Errors errors) {
        if (papPati.getFechaInicioDisponibilidad() == null) {
            errors.rejectValue(
                    "fechaInicioDisponibilidad",
                    "obligatorio",
                    "Debes indicar la fecha de inicio de disponibilidad"
            );
            return;
        }

        if (papPati.getFechaFinDisponibilidad() != null
                && papPati.getFechaFinDisponibilidad().isBefore(papPati.getFechaInicioDisponibilidad())) {

            errors.rejectValue(
                    "fechaFinDisponibilidad",
                    "invalida",
                    "La fecha de fin no puede ser anterior a la fecha de inicio"
            );
        }
    }

    private void validarCentroSocial(PapPati papPati, Errors errors) {
        String centroSocial = papPati.getCentroSocial();

        if (centroSocial == null || centroSocial.trim().isEmpty()) {
            errors.rejectValue(
                    "centroSocial",
                    "obligatorio",
                    "Debes indicar el centro social"
            );
            return;
        }

        if (centroSocial.trim().length() > MAX_CENTRO_SOCIAL) {
            errors.rejectValue(
                    "centroSocial",
                    "longitud",
                    "El centro social no puede superar los 150 caracteres"
            );
        }
    }

    private void validarDescripcionPerfil(PapPati papPati, Errors errors) {
        String descripcion = papPati.getDescripcionPerfil();

        if (descripcion == null || descripcion.trim().isEmpty()) {
            errors.rejectValue(
                    "descripcionPerfil",
                    "obligatorio",
                    "Debes añadir una descripción del perfil"
            );
            return;
        }

        if (descripcion.trim().length() < MIN_DESCRIPCION) {
            errors.rejectValue(
                    "descripcionPerfil",
                    "corta",
                    "La descripción debe tener al menos 20 caracteres"
            );
            return;
        }

        if (descripcion.trim().length() > MAX_DESCRIPCION) {
            errors.rejectValue(
                    "descripcionPerfil",
                    "longitud",
                    "La descripción no puede superar los 1000 caracteres"
            );
        }
    }
}