
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class GroupLeader extends UnicastRemoteObject implements GroupMemberInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, GroupMemberInterface> members; // pares <idMember, GroupMember>
    private int leaderId;


	GroupLeader() throws RemoteException {
		this.leaderId = 0;
		this.members = new HashMap<>();
		
		// configurando Registro RMI na porta 1099 para que Líder possa receber comandos da Interface SQLClient
		try {
			Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("GroupLeader", this);

		}catch(RemoteException e) {
			if (e.getCause() instanceof java.net.BindException) {
		        System.err.println("A porta já está em uso, escolha outra porta ou encerre o processo que está usando essa porta.");
		    } else {
		        e.printStackTrace();  // Outro tipo de RemoteException
		    }
		}
		
		System.out.println("Líder inicializado. Aguardando conexões dos membros e recebimento de comandos SQL");
	}

	@Override
	public void receiveSQLCommand(String sqlCommand) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Líder recebendo comando SQL: " + sqlCommand);
		
		// distribuir  comandos para o grupo
		System.out.println("Enviando comando SQL aos Membros do Grupo: " + sqlCommand);
		
		// Para cada membro do grupo, println("Enviando comando SQL para o membro idMembro")
		// implementar a lógica da execução em ordem por id

	}

	@Override
	public void joinGroup(int memberId) throws RemoteException {
		// TODO Auto-generated method stub
		
		// logica para que um novo membro se junte ao grupo no sistema distribuído
		
		System.out.println("Membro " + memberId + "adicionado ao grupo.");
		

	}

	@Override
	public void leaveGroup(int memberId) throws RemoteException {
		// TODO Auto-generated method stub
		
		// logica para remover um membro do grupo: heartbeat / pingpong / timeout
		
		System.out.println("Membro " + memberId + "removido do grupo.");

	}

	
	
    public void detectFailures() {
        // Implementar lógica para detecção de falhas de membros. usando ping por exemplo
    	
    	// para cada membro do grupo: println("ping: membro id"), testar member.ping,  exceção: println("Membro idMebro falhou e será removido do grupo")/
    }
    
    @Override
	public boolean ping() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}
    
    public static void main(String[] args) {
    	try {
            new GroupLeader();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

	


}
