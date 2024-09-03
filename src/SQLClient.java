import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

// classe que lê comandos SQL e envia ao Líder do Grupo
public class SQLClient {
    public static void main(String[] args) {
    	
    	Scanner scanner = new Scanner(System.in);
    	
    	System.out.println("Por favor, forneça o comando SQL a ser executado.");
    	
    	String sqlCommand = scanner.nextLine();
    	
        // Regex simples para verificar uma consulta SQL básica
        String regex = "^(SELECT|INSERT|UPDATE|DELETE)\\s+.*";

        if (! sqlCommand.matches(regex)) {
            System.out.println("O comando informado não parece ser uma consulta SQL.");
            return;
        }
    	
    	// enviar comando ao líder do grupo   
    	try {	
    		// recupera a referência para um objeto remoto Registry no endereço e porta especificados
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            // recupera a referência para o objeto remoto "GroupLeader" do objeto Registry
            GroupMemberInterface leader = (GroupMemberInterface) registry.lookup("GroupLeader");
            
            System.out.println("Enviando comando SQL ao Líder do Grupo: " + sqlCommand);    
            // invoca remotamente o metodo receiveSQLCommand do GroupLeader
            leader.receiveSQLCommand(sqlCommand);	  
        }catch (Exception e) {
            e.printStackTrace();
        }

    	 
    }
}
