package school.sptech.config;

import jakarta.mail.Session;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import java.util.Properties;

public class EmailConfig {
    public static Session getSession() {

        Properties props = new Properties();

        String host = System.getenv("SMTP_HOST");
        String port = System.getenv("SMTP_PORT");
        String auth = System.getenv("SMTP_AUTH");
        String starttls = System.getenv("SMTP_STARTTLS");

        final String user = System.getenv("SMTP_USER");
        final String senha = System.getenv("SMTP_PASSWORD");

        props.put("mail.smtp.host", host != null ? host : "smtp.gmail.com");
        props.put("mail.smtp.port", port != null ? port : "587");
        props.put("mail.smtp.auth", auth != null ? auth : "true");
        props.put("mail.smtp.starttls.enable", starttls != null ? starttls : "true");

        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        if ("true".equalsIgnoreCase(props.getProperty("mail.smtp.auth"))) {
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(
                            user != null ? user : "hypertechofiicial@gmail.com",
                            senha != null ? senha : "tjvh btmc uaaf begw"
                    );
                }
            });
        }

        return Session.getInstance(props);
    }
}
