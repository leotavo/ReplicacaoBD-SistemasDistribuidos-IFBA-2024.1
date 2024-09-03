package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection(String url, String user, String password) throws SQLException {
        // Estabelece a conexão com o banco de dados
        connection = DriverManager.getConnection(url, user, password);
    }

    public void executeSQL(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("SQL Executado: " + sql);
        } catch (SQLException e) {
            System.err.println("Erro ao executar SQL: " + e.getMessage());
            throw e;  // Repassa a exceção para permitir tratamento posterior
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Conexão com o banco de dados fechada.");
        }
    }
}
