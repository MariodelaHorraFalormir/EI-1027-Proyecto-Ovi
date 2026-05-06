package es.uji.ei1027.ovi.modelo.Roles;

import java.util.List;

public enum RolUsuario {
    Admin_ovi,
    Ovi_user,
    Pap_pati;

    public static RolUsuario fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("No ha llegado el rol de usuario o está vacío");
        }

        switch (valor.trim()) {
            case "Admin_ovi":
                return Admin_ovi;

            case "Ovi_user":
                return Ovi_user;

            case "Pap_pati":
                return Pap_pati;

            default:
                throw new IllegalArgumentException("Valor de RolUsuario no válido: " + valor);
        }
    }

    public String getTexto() {
        switch (this) {
            case Admin_ovi:
                return "Admin_ovi";
            case Ovi_user:
                return "Ovi_user";
            case Pap_pati:
                return "Pap_pati";
            default:
                throw new IllegalStateException("Valor no esperado: " + this);
        }
    }

    public String getTextoVisible() {
        switch (this) {
            case Admin_ovi:
                return "Administrador OVI";
            case Ovi_user:
                return "Usuario OVI";
            case Pap_pati:
                return "PAP/PATI";
            default:
                throw new IllegalStateException("Valor no esperado: " + this);
        }
    }

    public static List<RolUsuario> getLista() {
        return List.of(Admin_ovi, Ovi_user, Pap_pati);
    }

    public static List<RolUsuario> getRolesSolicitables() {
        return List.of(Ovi_user, Pap_pati);
    }

    public boolean esSolicitable() {
        return this == Ovi_user || this == Pap_pati;
    }
}