package es.uji.ei1027.ovi.modelo.PapPati;

import es.uji.ei1027.ovi.modelo.OviUser.DiversidadFuncional;
import es.uji.ei1027.ovi.modelo.OviUser.TipoDiversidadFuncional;

import java.time.LocalDate;

public class Especialidad {
    private int idEspecialidad;
    private int idPapPati;
    private TipoDiversidadFuncional diversidadFuncional;
    public TipoDiversidadFuncional getDiversidadFuncional() {
        return diversidadFuncional;
    }

    public void setDiversidadFuncional(TipoDiversidadFuncional diversidadFuncional) {
        this.diversidadFuncional = diversidadFuncional;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public int getIdPapPati() {
        return idPapPati;
    }

    public void setIdPapPati(int idPapPati) {
        this.idPapPati = idPapPati;
    }


}
