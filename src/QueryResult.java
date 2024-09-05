import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryResult implements Serializable {
	    private static final long serialVersionUID = 1L;

	    private boolean isQuery;
	    private List<List<Object>> rows;
	    private int updateCount;
	    
	    // Construtor para resultados de consulta (SELECT)
	    public QueryResult(ResultSet resultSet) throws SQLException {
	        this.isQuery = true;
	        this.rows = new ArrayList<>();
	        while (resultSet.next()) {
	            List<Object> row = new ArrayList<>();
	            int columnCount = resultSet.getMetaData().getColumnCount();
	            for (int i = 1; i <= columnCount; i++) {
	                row.add(resultSet.getObject(i));
	            }
	            rows.add(row);
	        }
	    }

	    // Construtor para resultados de atualização (INSERT, UPDATE, DELETE)
	    public QueryResult(int updateCount) {
	        this.isQuery = false;
	        this.updateCount = updateCount;
	    }
	    
	    // Método para verificar se é um resultado de consulta
	    public boolean isQuery() {
	        return isQuery;
	    }

	    // Obtém as linhas retornadas pela consulta
	    public List<List<Object>> getRows() {
	        return rows;
	    }
	    
	    // Obtém o número de linhas afetadas por uma atualização
	    public int getUpdateCount() {
	        return updateCount;
	    }
	    
	 // Método para comparar resultados (para verificação de consistência entre as consultas
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        QueryResult that = (QueryResult) o;
	        if (this.isQuery != that.isQuery) return false;
	        if (this.isQuery) {
	            return this.rows.equals(that.rows);
	        } else {
	            return this.updateCount == that.updateCount;
	        }
	    }
	    
	    @Override
	    public String toString() {
	        if (isQuery) {
	            return "QueryResult{" +
	                   "rows=" + rows +
	                   '}';
	        } else {
	            return "QueryResult{" +
	                   "updateCount=" + updateCount +
	                   '}';
	        }
	    }
}
