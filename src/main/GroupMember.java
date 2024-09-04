package main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GroupMember extends UnicastRemoteObject implements GroupMemberInterface {
	private static final long serialVersionUID = 1L;
	private int memberId;
	private String urlBD;
	private Connection connection;

	GroupMember(int id, String urlBD) throws RemoteException {
		super();
		this.memberId = id;
		this.urlBD = urlBD;

		try {
			connection = DriverManager.getConnection(urlBD);
			System.out.println("Conexão com o banco de dados estabelecida para o membro " + memberId);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("GroupMember_" + memberId, this);

			GroupMemberInterface leader = (GroupMemberInterface) registry.lookup("GroupLeader");
			leader.joinGroup(memberId);

			System.out.println("Membro " + memberId + " adicionado ao grupo.");
		} catch (Exception e) {
			System.err.println("Erro ao inicializar o membro " + memberId + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void receiveSQLCommand(String sqlCommand) throws RemoteException {
		System.out.println("Membro " + this.memberId + " executando comando SQL: " + sqlCommand);
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sqlCommand);
			System.out.println("Comando SQL executado com sucesso pelo membro " + memberId);
		} catch (SQLException e) {
			System.err.println("Erro ao executar comando SQL no membro " + memberId + ": " + e.getMessage());
			throw new RemoteException("Falha na execução do comando SQL", e);
		}
	}

	@Override
	public void joinGroup(int memberId) throws RemoteException {
		// Não utilizado diretamente pelo membro
	}

	@Override
	public void leaveGroup(int memberId) throws RemoteException {
		// Não utilizado diretamente pelo membro
	}

	@Override
	public boolean ping() throws RemoteException {
		System.out.println("Membro " + memberId + " retornando ping ao líder");
		return true;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Uso: java GroupMember <memberId> <urlBD>");
			System.exit(1);
		}

		int memberId = Integer.parseInt(args[0]);
		String urlBD = args[1];

		try {
			new GroupMember(memberId, urlBD);
		} catch (RemoteException e) {
			System.err.println("Erro ao criar o membro " + memberId + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
}