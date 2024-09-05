import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlExecutor {
	public static QueryResult executeSql(Connection connection, String sqlCommand) throws SQLException {
        if (sqlCommand.trim().toUpperCase().startsWith("SELECT")) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlCommand)) {
                return new QueryResult(resultSet);
            }
        } else {
            try (Statement statement = connection.createStatement()) {
                int rowsAffected = statement.executeUpdate(sqlCommand);
                return new QueryResult(rowsAffected);
            }
        }
    }
}
