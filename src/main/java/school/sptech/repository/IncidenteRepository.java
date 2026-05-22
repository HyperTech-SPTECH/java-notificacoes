package school.sptech.repository;

import school.sptech.config.PostgresConfig;
import school.sptech.enums.TipoNotificacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncidenteRepository {

    public List<String> buscarLocaisMaisPerigosos(TipoNotificacao tipo) {
        List<String> locais = new ArrayList<>();

        String filtroSQL = (tipo == TipoNotificacao.DIARIA) ?
                "WHERE EXTRACT(MONTH FROM data_hora) = EXTRACT(MONTH FROM NOW()) AND EXTRACT(DAY FROM data_hora) = EXTRACT(DAY FROM NOW())" :
                "WHERE EXTRACT(WEEK FROM data_hora) = EXTRACT(WEEK FROM NOW())";

        String sql = "SELECT municipio, bairro, COUNT(*) as total " +
                "FROM incidente " +
                filtroSQL + " " +
                "GROUP BY municipio, bairro " +
                "ORDER BY total DESC LIMIT 3";

        try (Connection conn = PostgresConfig.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String local = rs.getString("municipio") + " (Bairro: " + rs.getString("bairro") + ")";
                locais.add(local + " - " + rs.getInt("total") + " ocorrências");
            }

        } catch (SQLException e) {
            System.out.println();
        }
        return locais;
    }

    public List<String> buscarCargasMaisVisadas(TipoNotificacao tipo) {
        List<String> cargas = new ArrayList<>();

        String filtroSQL = (tipo == TipoNotificacao.DIARIA) ?
                "WHERE EXTRACT(MONTH FROM data_hora) = EXTRACT(MONTH FROM NOW()) AND EXTRACT(DAY FROM data_hora) = EXTRACT(DAY FROM NOW())" :
                "WHERE EXTRACT(WEEK FROM data_hora) = EXTRACT(WEEK FROM NOW())";

        String sql = "SELECT grupo_carga, COUNT(*) as total " +
                "FROM incidente " +
                filtroSQL + " " +
                "GROUP BY grupo_carga " +
                "ORDER BY total DESC LIMIT 3";

        try (Connection conn = PostgresConfig.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cargas.add(rs.getString("grupo_carga") + " - " + rs.getInt("total") + " roubos");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cargas;
    }

    public double buscarMediaDiariaSemanal() {
        String sql = "SELECT COUNT(*)::float / 7.0 as media " +
                "FROM incidente " +
                "WHERE EXTRACT(WEEK FROM data_hora) = EXTRACT(WEEK FROM NOW())";

        try (Connection conn = PostgresConfig.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("media");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0.0;
    }
}