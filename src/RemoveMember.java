import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoveMember {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso incorreto. Informe o ID do membro.");
            System.exit(1);
        }
        
        String memberId = args[0];  // ID do membro como String

        try {
            // Conecta ao registro RMI na porta 1099
            Registry registry = LocateRegistry.getRegistry(1099);
            System.out.println("Registro RMI encontrado na porta 1099.");

            // Obtém a referência do objeto remoto GroupLeader
            GroupMemberInterface leader = (GroupMemberInterface) registry.lookup("GroupLeader");
            System.out.println("Referência ao GroupLeader obtida.");

            // Solicita ao líder para remover o membro
            leader.leaveGroup(memberId);
            System.out.println("Solicitação de remoção enviada para o membro " + memberId);
        } catch (RemoteException e) {
            System.err.println("Erro de RemoteException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Falha geral ao remover o membro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
