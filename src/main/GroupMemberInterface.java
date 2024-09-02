package main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GroupMemberInterface extends Remote {
    void receiveSQLCommand(String sqlCommand) throws RemoteException;
    void joinGroup(int memberId) throws RemoteException;
    void leaveGroup(int memberId) throws RemoteException;
    
 // Método para responder ao ping do líder
    boolean ping() throws RemoteException;
}


