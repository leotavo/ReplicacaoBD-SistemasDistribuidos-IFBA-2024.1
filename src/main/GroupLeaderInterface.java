package main;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GroupLeaderInterface extends Remote {
    void executeSQL(String sql) throws RemoteException;
    void addMember(MemberInterface member) throws RemoteException;
    void removeMember(MemberInterface member) throws RemoteException;
}
