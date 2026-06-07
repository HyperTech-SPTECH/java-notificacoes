package school.sptech.service;

import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import school.sptech.config.EmailConfig;
import school.sptech.enums.TipoNotificacao;
import school.sptech.repository.FaleConoscoRepository;
import school.sptech.repository.IncidenteRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmailService {

    private final IncidenteRepository incidenteRepository = new IncidenteRepository();
    private final FaleConoscoRepository faleConoscoRepository = new FaleConoscoRepository();

    private String carregarTemplateHtml(TipoNotificacao tipo, String nomeUsuario) {
        String nomeArquivo = switch (tipo) {
            case DIARIA -> "TemplateDiario.html";
            case SEMANAL -> "TemplateSemanal.html";
            case ANUAL -> "TemplateAnual.html";
        };

        String caminhoTemplate = "templates/" + nomeArquivo;

        try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(caminhoTemplate)) {

            if (is == null) {
                throw new IOException("Arquivo de template não encontrado no classpath: " + caminhoTemplate);
            }

            String conteudoHtml = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

            conteudoHtml = conteudoHtml.replace("{nome}", nomeUsuario);

            if(tipo == TipoNotificacao.DIARIA || tipo == TipoNotificacao.SEMANAL) {

                List<String> dadosLocais = incidenteRepository.buscarLocaisMaisPerigosos(tipo);
                List<String> dadosCargas = incidenteRepository.buscarCargasMaisVisadas(tipo);

                String htmlLocais = converterParaListaHtml(dadosLocais);
                String htmlCargas = converterParaListaHtml(dadosCargas);

                conteudoHtml = conteudoHtml.replace("{locais_perigosos}", htmlLocais);
                conteudoHtml = conteudoHtml.replace("{cargas_visadas}", htmlCargas);

                if(tipo == TipoNotificacao.SEMANAL){
                    double mediaDiaria = incidenteRepository.buscarMediaDiariaSemanal();
                    String htmlMedia = String.format("<li>Média de incidentes: %.2f ocorrências por dia nesta semana", mediaDiaria);
                    conteudoHtml = conteudoHtml.replace("{locais_seguros}", htmlMedia);
                } else {
                    conteudoHtml = conteudoHtml.replace("{locais_seguros}",
                            "<li>Nenhuma anomalia de alta criticidade identificada nas rotas monitoradas hoje.</li>");
                }
            }
            return conteudoHtml;
        } catch (IOException e) {
            System.err.println("Erro ao ler o template " + nomeArquivo + " do classpath: " + e.getMessage());
            return "<h1>Olá, " + nomeUsuario + "!</h1><p>Você tem uma nova notificação.</p>"; // Fallback básico
        }
    }

    public void enviarEmail(String destinatario, String nomeUsuario, TipoNotificacao tipo) {
        try {
            MimeMessage message = new MimeMessage(EmailConfig.getSession());

            message.setFrom(new InternetAddress("sistema@seuapp.com", "HyperTech Notificação"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));

            message.setSubject("Sua Notificação " + tipo.name().toLowerCase() + " chegou!");

            String corpoHtml = carregarTemplateHtml(tipo, nomeUsuario);
            message.setContent(corpoHtml, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("[Sucesso] E-mail " + tipo + " enviado para: " + destinatario);

        } catch (Exception e) {
            System.err.println("[Erro] Falha ao enviar e-mail para " + destinatario + ": " + e.getMessage());
        }
    }

    private String converterParaListaHtml(List<String> dados) {
        if (dados == null || dados.isEmpty()) {
            return "<li>Nenhuma ocorrência crítica registrada para o período analisado.</li>";
        }

        StringBuilder sb = new StringBuilder();
        for (String item : dados) {
            String itemFormatado = formatarItemCapitalizado(item);

            sb.append("<li style='margin-bottom: 5px;'>").append(itemFormatado).append("</li>");
        }

        return sb.toString();
    }

    public String formatarItemCapitalizado(String texto) {
        if(texto == null || texto.isEmpty()) return "";

        char[] caracteres = texto.toCharArray();
        boolean capitalizarProximo = true;

        for (int i = 0; i < caracteres.length; i++) {
            if (Character.isWhitespace(caracteres[i]) || caracteres[i] == ':' || caracteres[i] == '(' || caracteres[i] == '-') {
                capitalizarProximo = true;
            } else if (capitalizarProximo) {
                caracteres[i] = Character.toUpperCase(caracteres[i]);
                capitalizarProximo = false;
            }
        }
        return new String(caracteres);
    }

    public void processarContatos() {
        try {
            Map<Integer, String> pendentes = faleConoscoRepository.buscarEmailsNaoProcessados();
            if (pendentes.isEmpty()) return;

            String template = carregarTemplateFaleConosco();

            StringBuilder listaHtml = new StringBuilder();
            for (String email : pendentes.values()) {
                String emailLimpo = email != null ? email.trim() : "";
                if (!emailLimpo.isEmpty()) {
                    listaHtml.append("<li style='margin-bottom: 5px;'>").append(emailLimpo).append("</li>");
                }
            }

            String corpoHtml = template.replace("{lista_emails}", listaHtml.toString());

            String meuEmail = "lcp600759@gmail.com";
            enviarEmailParaEquipe(meuEmail.trim(), corpoHtml);

            faleConoscoRepository.marcarComoProcessado(new ArrayList<>(pendentes.keySet()));

        } catch (Exception e) {
            System.err.println("Erro crítico no fluxo de contatos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String carregarTemplateFaleConosco() {
        String caminhoTemplate = "templates/TemplateFaleConosco.html";

        try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(caminhoTemplate)) {

            if (is == null) {
                throw new IOException("Arquivo de template não encontrado no classpath: " + caminhoTemplate);
            }

            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.err.println("Erro ao ler o template " + caminhoTemplate + ": " + e.getMessage());
            return "<html><body><h1>Novos Contatos</h1><ul>{lista_emails}</ul></body></html>";
        }
    }

    private void enviarEmailParaEquipe(String destinatario, String conteudoHtml) throws Exception {
        MimeMessage message = new MimeMessage(EmailConfig.getSession());

        message.setFrom(new InternetAddress("hypertechofiicial@gmail.com", "Sistema HyperTech"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario.trim()));
        String emailCopia = "hypertechofiicial@gmail.com";
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(emailCopia.trim()));
        message.setSubject("Relatório Diário - Fale Conosco");
        message.setContent(conteudoHtml, "text/html; charset=utf-8");

        Transport.send(message);
    }
}
