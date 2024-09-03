package main;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;

public class RmiServer extends UnicastRemoteObject {
    private int port;
    private String address;
    private Registry registry;

    public RmiServer() throws RemoteException {
        try {
        	 this.address = InetAddress.getLocalHost().getHostAddress();
             this.port = 3232;
             registry = LocateRegistry.createRegistry(this.port);
             System.out.println("Servidor RMI iniciado no endereço: " + this.address + ", porta: " + this.port);

             GroupLeader leader = new GroupLeader();
             registry.rebind("GroupLeader", leader);
             System.out.println("Líder do grupo registrado no servidor RMI.");

             Member member1 = new Member("Membro1", "url", "user", "password");
             Member member2 = new Member("Membro2", "url", "user", "password");
             Member member3 = new Member("Membro3", "url", "user", "password");

             registry.rebind("Member1", member1);
             registry.rebind("Member2", member2);
             registry.rebind("Member3", member3);
             System.out.println("Membros registrados no servidor RMI.");

             leader.addMember(member1);
             leader.addMember(member2);
             leader.addMember(member3);
             System.out.println("Membros adicionados ao grupo liderado.");
            
        } catch (Exception e) {
            throw new RemoteException("Erro ao iniciar o servidor RMI: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            RmiServer server = new RmiServer();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
