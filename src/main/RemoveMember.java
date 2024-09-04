package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoveMember {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Uso: java RemoveMember <memberId>");
			System.exit(1);
		}

		int memberId = Integer.parseInt(args[0]);

		try {
			Registry registry = LocateRegistry.getRegistry();
			GroupMemberInterface leader = (GroupMemberInterface) registry.lookup("GroupLeader");

			leader.leaveGroup(memberId);
			System.out.println("Solicitação de remoção enviada para o membro " + memberId);
		} catch (Exception e) {
			System.err.println("Erro ao remover membro: " + e.getMessage());
			e.printStackTrace();
		}
	}
}