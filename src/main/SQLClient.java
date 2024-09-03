package main;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SQLClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 3232);
            GroupLeaderInterface leader = (GroupLeaderInterface) registry.lookup("GroupLeader");
            
            leader.executeSQL("INSERT INTO tabela (coluna) VALUES ('valor');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
