import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class SQLClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Por favor, forneça o comando SQL a ser executado.");

        String sqlCommand = scanner.nextLine();
        scanner.close();
        
        // Remove aspas iniciais e finais, se existirem
        sqlCommand = sqlCommand.replaceAll("^\"|\"$", "");

        // Regex simples para verificar uma consulta SQL básica, ignorando maiúsculas e minúsculas
        String regex = "(?i)^(SELECT|INSERT|UPDATE|DELETE)\\s+.*$";

        if (!sqlCommand.matches(regex)) {
            System.out.println("O comando informado não parece ser uma consulta SQL.");
            return;
        }

        // Enviar comando ao líder do grupo   
        try {
            // Recupera a referência para um objeto remoto Registry no endereço e porta especificados
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Recupera a referência para o objeto remoto "GroupLeader" do objeto Registry
            GroupMemberInterface leader = (GroupMemberInterface) registry.lookup("GroupLeader");

            System.out.println("Enviando comando SQL ao Líder do Grupo: " + sqlCommand);
            // Invoca remotamente o método executeSQLCommand do GroupLeader
            QueryResult result = leader.executeSQLCommand(sqlCommand);

            // Adapte o tratamento de resultados conforme o tipo de QueryResult
            if (result != null) {
                System.out.println("Resultado da execução do comando SQL:");
                // Supondo que QueryResult tem um método para retornar uma representação em string
                System.out.println(result.toString());
            } else {
                System.out.println("Nenhum resultado retornado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar o comando SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}