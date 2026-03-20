package es.uji.ei1027.ovi.modelo.Persona;

import es.uji.ei1027.ovi.modelo.Persona.Roles.OviUser;
import es.uji.ei1027.ovi.modelo.Persona.Roles.PatPati;

public class PersonaFormulario {
    private PatPati patPati;
    private OviUser oviUser;
    private Persona persona;
    private int numeroRoles ;
    public PersonaFormulario() {
        this.numeroRoles = 0;
    }

    public PatPati getPatPati() {
        return patPati;
    }

    public void setPatPati(PatPati patPati) {
        if (this.patPati != null) {
            numeroRoles +=1;
        }
        this.patPati = patPati;
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
