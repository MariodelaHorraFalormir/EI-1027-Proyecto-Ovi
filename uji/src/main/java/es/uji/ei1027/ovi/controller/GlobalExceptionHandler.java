package es.uji.ei1027.ovi.controller;


import es.uji.ei1027.ovi.excepcition.OviException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OviException.class)
    public String handleOviException(OviException ex,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     Model model) {

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        model.addAttribute("errorName", ex.getErrorName());
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error";
    }

    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseException(DataAccessException ex,
                                          HttpServletRequest request,
                                          HttpServletResponse response,
                                          Model model) {

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        model.addAttribute("errorName", "Error de base de datos");
        model.addAttribute("message", getDatabaseMessage(ex));
        model.addAttribute("path", request.getRequestURI());

        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         Model model) {

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        model.addAttribute("errorName", "Error inesperado");
        model.addAttribute("message", "Ha ocurrido un problema al procesar la petición.");
        model.addAttribute("path", request.getRequestURI());

        return "error";
    }

    private String getDatabaseMessage(DataAccessException ex) {

        if (ex instanceof DuplicateKeyException) {
            return "No se puede guardar el registro porque ya existe otro con los mismos datos clave.";
        }

        if (ex instanceof DataIntegrityViolationException) {
            return "No se puede completar la operación porque incumple una restricción de la base de datos.";
        }

        if (ex instanceof BadSqlGrammarException) {
            return "Hay un problema en una consulta SQL de la aplicación.";
        }

        return "No se ha podido completar la operación con la base de datos.";
    }
}