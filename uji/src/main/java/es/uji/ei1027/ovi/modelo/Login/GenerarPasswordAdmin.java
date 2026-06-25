package es.uji.ei1027.ovi.modelo.Login;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GenerarPasswordAdmin implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    private static final boolean ACTIVAR = true;

    public GenerarPasswordAdmin(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!ACTIVAR) {
            return;
        }
        String  mailAdmin;
        String  passwordPlano;
        for (int i = 0 ; i < 2 ; i++){
            if(i == 0){
                mailAdmin = "admin1@ovi.es";
                passwordPlano = "admin123";
            } else {
                mailAdmin = "joel.user@ovi.es";
                passwordPlano = "user123";
            }


            BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
            String passwordCifrado = passwordEncryptor.encryptPassword(passwordPlano);

            int filasActualizadas = jdbcTemplate.update(
                    "UPDATE persona SET contrasena = ? WHERE mail = ?",
                    passwordCifrado,
                    mailAdmin
            );


            System.out.println("=================================");
            System.out.println("ADMIN TEMPORAL PREPARADO");
            System.out.println("Mail: " + mailAdmin);
            System.out.println("Password: " + passwordPlano);
            System.out.println("Filas actualizadas: " + filasActualizadas);
            System.out.println("=================================");
        }
        }

}