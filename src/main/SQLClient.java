package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SQLClient {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java SQLClient <sqlCommand>");
            System.exit(1);
        }

        String sqlCommand = args[0];

        try {
            Registry registry = LocateRegistry.getRegistry();
            GroupMemberInterface leader = (GroupMemberInterface) registry.lookup("GroupLeader");

            leader.receiveSQLCommand(sqlCommand);
            System.out.println("Comando SQL enviado para o líder: " + sqlCommand);
        } catch (Exception e) {
            System.err.println("Erro ao enviar comando SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}