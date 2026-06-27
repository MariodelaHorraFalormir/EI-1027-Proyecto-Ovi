package es.uji.ei1027.ovi.Validadores;

import es.uji.ei1027.ovi.modelo.PaRequest.PaRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PaRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return PaRequest.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        PaRequest paRequest = (PaRequest) obj;

        if (paRequest.getFechaInicio() == null) {
            errors.rejectValue("fechaInicio", "obligatorio", "Debes indicar una fecha de inicio");
        }

        // CORRECCIÓN: Eliminamos la validación que exigía que la Fecha Fin fuera obligatoria

        // Si ambas fechas existen, comprobamos que la de fin no sea anterior a la de inicio
        if (paRequest.getFechaInicio() != null && paRequest.getFechaFin() != null) {
            if (paRequest.getFechaFin().isBefore(paRequest.getFechaInicio())) {
                errors.rejectValue("fechaFin", "incoherente", "La fecha de fin no puede ser anterior a la de inicio");
            }
        }
    }
}