package es.uji.ei1027.ovi.modelo.Chat;

public class Conversacion {

    private int id;
    private int paRequest;
    private int oviUser;
    private int papPati;



    public Conversacion(int id, int paRequest, int oviUser, int papPati) {
        this.id = id;
        this.paRequest = paRequest;
        this.oviUser = oviUser;
        this.papPati = papPati;
    }

    public Conversacion() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getPaRequest() {
        return paRequest;
    }

    public void setPaRequest(int paRequest) {
        this.paRequest = paRequest;
    }

    public int getOviUser() {
        return oviUser;
    }

    public void setOviUser(int oviUser) {
        this.oviUser = oviUser;
    }

    public int getPapPati() {
        return papPati;
    }

    public void setPapPati(int papPati) {
        this.papPati = papPati;
    }
}