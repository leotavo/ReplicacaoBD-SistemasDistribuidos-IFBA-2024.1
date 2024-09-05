

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface GroupMemberInterface extends Remote {
    QueryResult executeSQLCommand(String sqlCommand) throws RemoteException, SQLException;
    void joinGroup(String memberId) throws RemoteException;
    void leaveGroup(String memberId) throws RemoteException;
    
 // Método para detecção de falhas
    boolean ping() throws RemoteException;
	
}


