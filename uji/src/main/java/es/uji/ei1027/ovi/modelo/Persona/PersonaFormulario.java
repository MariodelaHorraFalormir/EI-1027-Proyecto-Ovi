package es.uji.ei1027.ovi.modelo.Persona;

import es.uji.ei1027.ovi.modelo.OviUser.OviUser;
import es.uji.ei1027.ovi.modelo.PapPati.PapPati;

public class PersonaFormulario {
    private PapPati papPati;
    private OviUser oviUser;
    private Persona persona;
    private int numeroRoles ;
    public PersonaFormulario() {
        this.numeroRoles = 0;
    }

    public PapPati getPatPati() {
        return papPati;
    }

    public void setPatPati(PapPati patPati) {
        if (this.papPati != null) {
            numeroRoles +=1;
        }
        this.papPati = patPati;
    }

    public OviUser getOviUser() {
        return oviUser;
    }

    public void setOviUser(OviUser oviUser) {
        if (this.oviUser != null) {
            numeroRoles +=1;
        }
        this.oviUser = oviUser;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }


}
