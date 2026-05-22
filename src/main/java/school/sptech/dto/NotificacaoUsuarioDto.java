package school.sptech.dto;

import java.util.ArrayList;
import java.util.List;

public class NotificacaoUsuarioDto {
    private String nome;
    private List<EmailConfigDto> emails;

    public NotificacaoUsuarioDto(String nome) {
        this.nome = nome;
        this.emails = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public List<EmailConfigDto> getEmails() { return emails; }

    public EmailConfigDto obterOuCriarEmail(String email) {
        for (EmailConfigDto cfg : emails) {
            if (cfg.getEmail().equals(email)) {
                return cfg;
            }
        }
        EmailConfigDto novo = new EmailConfigDto(email);
        emails.add(novo);
        return novo;
    }
}
