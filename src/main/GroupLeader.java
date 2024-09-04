package main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GroupLeader extends UnicastRemoteObject implements GroupMemberInterface {
	private static final long serialVersionUID = 1L;
	private Map<Integer, GroupMemberInterface> members;
	private int leaderId;
	private List<String> messageQueue;
	private ScheduledExecutorService scheduler;

	GroupLeader() throws RemoteException {
		super();
		this.leaderId = 0;
		this.members = new ConcurrentHashMap<>();
		this.messageQueue = new ArrayList<>();
		this.scheduler = Executors.newScheduledThreadPool(1);

		try {
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind("GroupLeader", this);
			System.out.println("Líder inicializado. Aguardando conexões dos membros.");
		} catch (RemoteException e) {
			System.err.println("Erro ao inicializar o líder: " + e.getMessage());
			e.printStackTrace();
		}

		// Agendar a detecção de falhas a cada 5 segundos
		scheduler.scheduleAtFixedRate(this::detectFailures, 0, 5, TimeUnit.SECONDS);
	}

	@Override
	public synchronized void receiveSQLCommand(String sqlCommand) throws RemoteException {
		System.out.println("Líder recebendo comando SQL: " + sqlCommand);
		messageQueue.add(sqlCommand);
		distributeCommands();
	}

	private void distributeCommands() {
		while (!messageQueue.isEmpty()) {
			String command = messageQueue.remove(0);
			for (Map.Entry<Integer, GroupMemberInterface> entry : members.entrySet()) {
				int memberId = entry.getKey();
				GroupMemberInterface member = entry.getValue();
				try {
					System.out.println("Enviando comando SQL para o membro " + memberId);
					member.receiveSQLCommand(command);
				} catch (RemoteException e) {
					System.err.println("Erro ao enviar comando para membro " + memberId + ": " + e.getMessage());
					// Considerar remover o membro se a comunicação falhar repetidamente
				}
			}
		}
	}

	@Override
	public synchronized void joinGroup(int memberId) throws RemoteException {
		if (!members.containsKey(memberId)) {
			try {
				Registry registry = LocateRegistry.getRegistry();
				GroupMemberInterface member = (GroupMemberInterface) registry.lookup("GroupMember_" + memberId);
				members.put(memberId, member);
				System.out.println("Membro " + memberId + " adicionado ao grupo.");
			} catch (Exception e) {
				System.err.println("Erro ao adicionar membro " + memberId + ": " + e.getMessage());
				throw new RemoteException("Falha ao adicionar membro", e);
			}
		} else {
			System.out.println("Membro " + memberId + " já existe no grupo.");
		}
	}

	@Override
	public synchronized void leaveGroup(int memberId) throws RemoteException {
		if (members.remove(memberId) != null) {
			System.out.println("Membro " + memberId + " removido do grupo.");
		} else {
			System.out.println("Membro " + memberId + " não encontrado no grupo.");
		}
	}

	public void detectFailures() {
		List<Integer> failedMembers = new ArrayList<>();
		for (Map.Entry<Integer, GroupMemberInterface> entry : members.entrySet()) {
			int memberId = entry.getKey();
			GroupMemberInterface member = entry.getValue();
			try {
				System.out.println("Ping: membro " + memberId);
				if (!member.ping()) {
					throw new RemoteException("Ping falhou");
				}
			} catch (RemoteException e) {
				System.out.println("Membro " + memberId + " falhou e será removido do grupo");
				failedMembers.add(memberId);
			}
		}
		for (int memberId : failedMembers) {
			members.remove(memberId);
		}
	}

	@Override
	public boolean ping() throws RemoteException {
		return true;
	}

	public static void main(String[] args) {
		try {
			new GroupLeader();
			System.out.println("Líder criado e pronto para operação.");
		} catch (RemoteException e) {
			System.err.println("Erro ao criar o líder: " + e.getMessage());
			e.printStackTrace();
		}
	}
}