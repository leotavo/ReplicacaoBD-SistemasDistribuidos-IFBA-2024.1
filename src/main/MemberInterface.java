package main;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MemberInterface extends Remote {
    void receiveSQL(String sql) throws RemoteException;
    String getMemberId() throws RemoteException;
}
