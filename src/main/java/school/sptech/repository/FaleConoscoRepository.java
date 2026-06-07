package school.sptech.repository;

import school.sptech.config.MysqlConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FaleConoscoRepository {

    public Map<Integer, String> buscarEmailsNaoProcessados() throws SQLException {
        Map<Integer, String> pendentes = new HashMap<>();
        String sql = "SELECT id, email FROM fale_conosco WHERE status = false";

        try (Connection conn = MysqlConfig.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pendentes.put(rs.getInt("id"), rs.getString("email"));
            }
        }
        return pendentes;
    }

    public void marcarComoProcessado(List<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE fale_conosco SET status = true WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append(i == 0 ? "?" : ", ?");
        }
        sql.append(")");

        try (Connection conn = MysqlConfig.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < ids.size(); i++) {
                stmt.setInt(i + 1, ids.get(i));
            }

            stmt.executeUpdate();
            System.out.println("Status atualizado para os IDs: " + ids);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
            throw e;
        }
    }
}