package school.sptech.repository;

import school.sptech.config.MysqlConfig;
import school.sptech.dto.EmailConfigDto;
import school.sptech.dto.NotificacaoUsuarioDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class NotificacaoRepository {

    public List<NotificacaoUsuarioDto> buscarNotificacoesAtivas() throws SQLException {

        Map<String, NotificacaoUsuarioDto> mapaUsuarios = new LinkedHashMap<>();

        String sql = "SELECT u.nome, ue.email, p.tipo " +
                "FROM usuario_email_notificacao ue " +
                "INNER JOIN usuarios u ON ue.usuario_id = u.id " +
                "INNER JOIN preferencias_notificacao p ON ue.id = p.usuario_email_id " +
                "WHERE p.ativo = true";

        try (Connection conn = MysqlConfig.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String tipo = rs.getString("tipo");

                NotificacaoUsuarioDto usuarioDto = mapaUsuarios.computeIfAbsent(nome, NotificacaoUsuarioDto::new);
                EmailConfigDto emailDto = usuarioDto.obterOuCriarEmail(email);
                emailDto.adicionarTipo(tipo);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar dados: " + e.getMessage());
            throw e;
        }
        return new ArrayList<>(mapaUsuarios.values());
    }
}
