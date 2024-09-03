

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GroupMember extends UnicastRemoteObject implements GroupMemberInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int memberId;
	private String urlBD;

	GroupMember(int id, String urlBD) throws RemoteException {
		super(id);
		// TODO Auto-generated constructor stub
		
		this.memberId = id;
		this.urlBD = urlBD;
		
		
		// configurar o registro RMI para o novo membro do grupo
		// localiza o objeto remoto do líder e chama do método de adicionar ao grupo
		// println("membro adicionado")
	}

	@Override
	public void receiveSQLCommand(String sqlCommand) throws RemoteException {
		// TODO Auto-generated method stub
		
		System.out.println("Membro " + this.memberId + " executando comando SQL: " + sqlCommand);
		
        // Lógica para executar o comando SQL na base de dados local.


	}

	@Override
	public void joinGroup(int memberId) throws RemoteException {
		// TODO Auto-generated method stub
		
		// Não utilizado diretamente pelo membro

	}

	@Override
	public void leaveGroup(int memberId) throws RemoteException {
		// TODO Auto-generated method stub
		
		// Não utilizado diretamente pelo membro

	}
	
	@Override
	public boolean ping() throws RemoteException {
		// TODO Auto-generated method stub
//		print("retornado ping ao líder")
		return true;
	}
	
	public static void main(String[] args) {
		
		// instancia um novo membro no grupo a partir do id em args[0] e url banco args[1]
		
	}



}
