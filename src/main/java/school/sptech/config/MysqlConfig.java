package school.sptech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConfig {
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://mysql:3306/ConstrucaoBancoDeDados");
    private static final String user = System.getenv().getOrDefault("DB_USER", "hyper");
    private static final String senha = System.getenv().getOrDefault("DB_PASSWORD", "1234");

    public static Connection obterConexao() throws SQLException {
            return DriverManager.getConnection(URL, user, senha);
    }
}
