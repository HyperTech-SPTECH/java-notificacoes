package school.sptech;

import school.sptech.dto.EmailConfigDto;
import school.sptech.dto.NotificacaoUsuarioDto;
import school.sptech.enums.TipoNotificacao;
import school.sptech.repository.NotificacaoRepository;
import school.sptech.service.EmailService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== [HyperTech] Iniciando Rotina Diária de Emails ===");

        LocalDate hoje = LocalDate.now();
        System.out.println("[Info] Data de processamento: " + hoje);

        boolean ehDiaDeSemanal = (hoje.getDayOfWeek() == DayOfWeek.MONDAY);
        boolean ehDiaDeAnual = (hoje.getMonthValue() == 1 && hoje.getDayOfMonth() == 1);

        try {
            NotificacaoRepository repository = new NotificacaoRepository();
            EmailService emailService = new EmailService();

            List<NotificacaoUsuarioDto> usuarios = repository.buscarNotificacoesAtivas();

            for (NotificacaoUsuarioDto usuario : usuarios) {
                for (EmailConfigDto configEmail : usuario.getEmails()) {
                    for (String tipoString : configEmail.getTiposAtivos()) {

                        TipoNotificacao tipoEnum = TipoNotificacao.valueOf(tipoString);

                        if (tipoEnum == TipoNotificacao.DIARIA) {
                            emailService.enviarEmail(configEmail.getEmail(), usuario.getNome(), tipoEnum);

                        } else if (tipoEnum == TipoNotificacao.SEMANAL && ehDiaDeSemanal) {
                            System.out.println("   [Calendário] Segunda-feira detectada. Processando e-mail Semanal...");
                            emailService.enviarEmail(configEmail.getEmail(), usuario.getNome(), tipoEnum);

                        } else if (tipoEnum == TipoNotificacao.ANUAL && ehDiaDeAnual) {
                            System.out.println("   [Calendário] Primeiro dia do ano detectado. Processando e-mail Anual...");
                            emailService.enviarEmail(configEmail.getEmail(), usuario.getNome(), tipoEnum);
                        }
                    }
                }
            }

            System.out.println("=== [HyperTech] Rotina de Emails Finalizada com Sucesso ===");

        } catch (Exception e) {
            System.err.println("[Erro Crítico] Interrupção no fluxo principal: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            System.out.println("=== [HyperTech] Iniciando processamento do Fale Conosco ===");
            EmailService emailService = new EmailService();
            emailService.processarContatos();
            System.out.println("=== [HyperTech] Processamento do Fale Conosco finalizado ===");
        } catch (Exception e) {
            System.err.println("[Erro Crítico] Falha no processamento do Fale Conosco: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
