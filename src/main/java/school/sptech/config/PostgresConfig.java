package school.sptech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConfig {
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://postgres-hyper-container:5432/ConstrucaoBancoDeDados");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "hyper");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "1234");

    public static Connection obterConexao() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não encontrado!", e);
        }
    }
}
