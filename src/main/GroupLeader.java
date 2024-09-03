package main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class GroupLeader extends UnicastRemoteObject implements GroupMemberInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, GroupMemberInterface> members;
    private int leaderId;


	GroupLeader() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.leaderId = 0;
		this.members = new HashMap<>();
		
		// implementar configuração registro RMI para que um objeto remoto possa ser acessado por clientes
		
		
		System.out.println("Líder inicializado. Aguardando conexões dos membros.");
	}

	@Override
	public void receiveSQLCommand(String sqlCommand) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Líder recebendo comando SQL: " + sqlCommand);
		
		// distribuir  comandos para o grupo
		// Para cada membro do grupo, println("Enviando comando SQL para o membro idMembro")
		// verificar a lógica da execução em ordem por id

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
    	// inicializa instância do líder
    	// println("Líder criado")
    }

	


}
