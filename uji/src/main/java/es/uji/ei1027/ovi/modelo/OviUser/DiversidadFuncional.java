package es.uji.ei1027.ovi.modelo.OviUser;

public class DiversidadFuncional {
    private int idDiversidadFuncional;
    private int oviUserId;
    private TipoDiversidadFuncional tipo;
    private String observaciones;
    public int getIdDiversidadFuncional() {
        return idDiversidadFuncional;
    }

    public void setIdDiversidadFuncional(int idDiversidadFuncional) {
        this.idDiversidadFuncional = idDiversidadFuncional;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public TipoDiversidadFuncional getTipo() {
        return tipo;
    }

    public void setTipo(TipoDiversidadFuncional tipo) {
        this.tipo = tipo;
    }

    public int getOviUserId() {
        return oviUserId;
    }

    public void setOviUserId(int oviUserId) {
        this.oviUserId = oviUserId;
    }


}
