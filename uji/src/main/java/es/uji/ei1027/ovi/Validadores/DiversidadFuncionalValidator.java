package es.uji.ei1027.ovi.Validadores;

import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class DiversidadFuncionalValidator implements Validator {

    private static final int MIN_OBSERVACIONES = 10;
    private static final int MAX_OBSERVACIONES = 500;

    @Override
    public boolean supports(Class<?> clazz) {
        return DiversidadFuncional.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DiversidadFuncional diversidadFuncional = (DiversidadFuncional) target;

        validarTipo(diversidadFuncional, errors);
        validarObservaciones(diversidadFuncional, errors);
    }

    private void validarTipo(DiversidadFuncional diversidadFuncional, Errors errors) {
        if (diversidadFuncional.getTipo() == null) {
            errors.rejectValue(
                    "tipo",
                    "obligatorio",
                    "Debes seleccionar un tipo de diversidad funcional"
            );
        }
    }

    private void validarObservaciones(DiversidadFuncional diversidadFuncional, Errors errors) {
        String observaciones = diversidadFuncional.getObservaciones();

        if (observaciones == null || observaciones.trim().isEmpty()) {
            errors.rejectValue(
                    "observaciones",
                    "obligatorio",
                    "Debes añadir una observación"
            );
            return;
        }

        if (observaciones.trim().length() < MIN_OBSERVACIONES) {
            errors.rejectValue(
                    "observaciones",
                    "corta",
                    "La observación debe tener al menos 10 caracteres"
            );
            return;
        }

        if (observaciones.trim().length() > MAX_OBSERVACIONES) {
            errors.rejectValue(
                    "observaciones",
                    "longitud",
                    "La observación no puede superar los 500 caracteres"
            );
        }
    }
}