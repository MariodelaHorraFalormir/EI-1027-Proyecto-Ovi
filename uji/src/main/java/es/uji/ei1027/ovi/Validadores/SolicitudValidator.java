package es.uji.ei1027.ovi.Validadores;

import es.uji.ei1027.ovi.modelo.Solicitud.CategoriaSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.EstadoSolicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.Solicitud;
import es.uji.ei1027.ovi.modelo.Solicitud.TipoSolicitud;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class SolicitudValidator implements Validator {

    private static final int MAX_MENSAJE = 500;
    private static final int MAX_MOTIVO = 500;

    @Override
    public boolean supports(Class<?> clazz) {
        return Solicitud.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Solicitud solicitud = (Solicitud) target;

        validarCamposObligatorios(solicitud, errors);
        validarCombinacionCategoriaDetalle(solicitud, errors);
        validarEstadoYResolucion(solicitud, errors);
        validarFechas(solicitud, errors);
        validarLongitudes(solicitud, errors);
    }

    private void validarCamposObligatorios(Solicitud solicitud, Errors errors) {
        if (solicitud.getPersonaSolicitante() <= 0) {
            errors.rejectValue(
                    "personaSolicitante",
                    "personaSolicitante.obligatorio",
                    "La persona solicitante es obligatoria."
            );
        }

        if (solicitud.getCategoriaSolicitud() == null) {
            errors.rejectValue(
                    "categoriaSolicitud",
                    "categoriaSolicitud.obligatoria",
                    "La categoría de la solicitud es obligatoria."
            );
        }

        if (solicitud.getTipoSolicitud() == null) {
            errors.rejectValue(
                    "tipoSolicitud",
                    "tipoSolicitud.obligatorio",
                    "El tipo de solicitud es obligatorio."
            );
        }

        if (solicitud.getEstadoSolicitud() == null) {
            errors.rejectValue(
                    "estadoSolicitud",
                    "estadoSolicitud.obligatorio",
                    "El estado de la solicitud es obligatorio."
            );
        }

        if (solicitud.getFechaCreacion() == null) {
            errors.rejectValue(
                    "fechaCreacion",
                    "fechaCreacion.obligatoria",
                    "La fecha de creación es obligatoria."
            );
        }
    }

    private void validarCombinacionCategoriaDetalle(Solicitud solicitud, Errors errors) {
        CategoriaSolicitud categoria = solicitud.getCategoriaSolicitud();
        TipoSolicitud tipo = solicitud.getTipoSolicitud();

        if (categoria == null || tipo == null) {
            return;
        }

        if (categoria == CategoriaSolicitud.Rol) {
            boolean detalleValido =
                    tipo == TipoSolicitud.Pap_pati ||
                            tipo == TipoSolicitud.Ovi_user;

            if (!detalleValido) {
                errors.rejectValue(
                        "tipoSolicitud",
                        "tipoSolicitud.invalido",
                        "Si la categoría es Rol, el tipo debe ser Pap_pati u Ovi_user."
                );
            }
        }

        if (categoria == CategoriaSolicitud.Proceso) {
            boolean detalleValido =
                    tipo == TipoSolicitud.Pa_request ||
                            tipo == TipoSolicitud.Asistencia_tecnica ||
                            tipo == TipoSolicitud.Otro;

            if (!detalleValido) {
                errors.rejectValue(
                        "tipoSolicitud",
                        "tipoSolicitud.invalido",
                        "Si la categoría es Proceso, el tipo debe ser Pa_request, Asistencia_tecnica u Otro."
                );
            }
        }
    }

    private void validarEstadoYResolucion(Solicitud solicitud, Errors errors) {
        EstadoSolicitud estado = solicitud.getEstadoSolicitud();

        if (estado == null) {
            return;
        }

        if (estado == EstadoSolicitud.Pendiente) {
            if (solicitud.getFechaResolucion() != null) {
                errors.rejectValue(
                        "fechaResolucion",
                        "fechaResolucion.noPermitida",
                        "Una solicitud pendiente no puede tener fecha de resolución."
                );
            }

            if (!estaVacio(solicitud.getMotivoResolucion())) {
                errors.rejectValue(
                        "motivoResolucion",
                        "motivoResolucion.noPermitido",
                        "Una solicitud pendiente no debería tener motivo de resolución."
                );
            }
        }

        if (estado == EstadoSolicitud.Aprobada || estado == EstadoSolicitud.Rechazada) {
            if (solicitud.getFechaResolucion() == null) {
                errors.rejectValue(
                        "fechaResolucion",
                        "fechaResolucion.obligatoria",
                        "Una solicitud aprobada o rechazada debe tener fecha de resolución."
                );
            }
        }

        if (estado == EstadoSolicitud.Rechazada && estaVacio(solicitud.getMotivoResolucion())) {
            errors.rejectValue(
                    "motivoResolucion",
                    "motivoResolucion.obligatorio",
                    "Una solicitud rechazada debe tener motivo de resolución."
            );
        }
    }

    private void validarFechas(Solicitud solicitud, Errors errors) {
        LocalDate hoy = LocalDate.now();

        if (solicitud.getFechaCreacion() != null &&
                solicitud.getFechaCreacion().isAfter(hoy)) {
            errors.rejectValue(
                    "fechaCreacion",
                    "fechaCreacion.futura",
                    "La fecha de creación no puede ser futura."
            );
        }

        if (solicitud.getFechaResolucion() != null &&
                solicitud.getFechaResolucion().isAfter(hoy)) {
            errors.rejectValue(
                    "fechaResolucion",
                    "fechaResolucion.futura",
                    "La fecha de resolución no puede ser futura."
            );
        }

        if (solicitud.getFechaCreacion() != null &&
                solicitud.getFechaResolucion() != null &&
                solicitud.getFechaResolucion().isBefore(solicitud.getFechaCreacion())) {
            errors.rejectValue(
                    "fechaResolucion",
                    "fechaResolucion.anteriorCreacion",
                    "La fecha de resolución no puede ser anterior a la fecha de creación."
            );
        }
    }

    private void validarLongitudes(Solicitud solicitud, Errors errors) {
        if (solicitud.getMensajeSolicitud() != null &&
                solicitud.getMensajeSolicitud().length() > MAX_MENSAJE) {
            errors.rejectValue(
                    "mensajeSolicitud",
                    "mensajeSolicitud.largo",
                    "El mensaje no puede superar los 500 caracteres."
            );
        }

        if (solicitud.getMotivoResolucion() != null &&
                solicitud.getMotivoResolucion().length() > MAX_MOTIVO) {
            errors.rejectValue(
                    "motivoResolucion",
                    "motivoResolucion.largo",
                    "El motivo de resolución no puede superar los 500 caracteres."
            );
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}