package main;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GroupLeader extends UnicastRemoteObject implements GroupLeaderInterface {
    private List<MemberInterface> members;
    private int leaderId; 
    
    public GroupLeader() throws RemoteException {
       this.members = new ArrayList<>();
       this.leaderId = 0;
		
    }

    @Override
    public synchronized void executeSQL(String sql) throws RemoteException {
        System.out.println("Executando SQL no grupo: " + sql);
        for (MemberInterface member : members) {
            try {
                member.receiveSQL(sql);
            } catch (RemoteException e) {
                System.err.println("Erro ao enviar SQL para membro: " + member.getMemberId());
                removeMember(member); // Remove membro com falha
            }
        }
    }

    @Override
    public synchronized void addMember(MemberInterface member) throws RemoteException {
        members.add(member);
        System.out.println("Novo membro adicionado: " + member.getMemberId());
    }

    @Override
    public synchronized void removeMember(MemberInterface member) throws RemoteException {
        members.remove(member);
        System.out.println("Membro removido: " + member.getMemberId());
    }

    // Método para detectar falhas (simples verificação de ping)
    public void checkMembers() throws RemoteException {
        for (MemberInterface member : new ArrayList<>(members)) {
            try {
                member.getMemberId();  // Apenas uma chamada remota para verificar a conexão
            } catch (RemoteException e) {
                System.out.println("Falha detectada no membro: " + member.getMemberId());
                removeMember(member);
            }
        }
    }

}
