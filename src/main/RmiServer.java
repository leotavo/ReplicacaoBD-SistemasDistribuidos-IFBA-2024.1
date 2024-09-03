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

            // Registra o líder do grupo
            GroupLeader leader = new GroupLeader();
            registry.rebind("GroupLeader", leader);
            System.out.println("Líder do grupo registrado no servidor RMI.");
            
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
