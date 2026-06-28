package es.uji.ei1027.ovi.excepcition;

public class OviException extends RuntimeException {

    private String errorName;

    public OviException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }
}