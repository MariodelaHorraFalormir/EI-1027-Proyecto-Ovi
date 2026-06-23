package es.uji.ei1027.ovi.modelo.OviUser;

import es.uji.ei1027.ovi.modelo.Roles.EstadoRol;

import java.util.List;

public class OviUser {
    private int idOviUser;
    private  Integer gradoDiversidadFuncional;
    private Integer gradoDependencia;
    private EstadoRol estado;
    private String urlProyectoDeVida;
    private String centroSocialReferencia;

    private List<DiversidadFuncional> diversidadesFuncionales;


    public int getIdOviUser() {
        return idOviUser;
    }

    public void setIdOviUser(int idOviUser) {
        this.idOviUser = idOviUser;
    }

    public Integer getGradoDiversidadFuncional() {
        return gradoDiversidadFuncional;
    }

    public void setGradoDiversidadFuncional(Integer gradoDiversidadFuncional) {
        this.gradoDiversidadFuncional = gradoDiversidadFuncional;
    }

    public EstadoRol getEstado() {
        return estado;
    }

    public void setEstado(EstadoRol estado) {
        this.estado = estado;
    }

    public Integer getGradoDependencia() {
        return gradoDependencia;
    }

    public void setGradoDependencia(Integer gradoDependencia) {
        this.gradoDependencia = gradoDependencia;
    }

    public String getCentroSocialReferencia() {
        return centroSocialReferencia;
    }

    public void setCentroSocialReferencia(String centroSocialReferencia) {
        this.centroSocialReferencia = centroSocialReferencia;
    }

    public String getUrlProyectoDeVida() {
        return urlProyectoDeVida;
    }

    public void setUrlProyectoDeVida(String urlProyectoDeVida) {
        this.urlProyectoDeVida = urlProyectoDeVida;
    }

    public List<DiversidadFuncional> getDiversidadesFuncionales() {
        return diversidadesFuncionales;
    }

    public void setDiversidadesFuncionales(List<DiversidadFuncional> diversidadesFuncionales) {
        this.diversidadesFuncionales = diversidadesFuncionales;
    }



}
