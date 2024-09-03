package main;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class Member extends UnicastRemoteObject implements MemberInterface {
    private String id;
    private DatabaseConnection dbConnection;

    public Member(String id, String dbUrl, String dbUser, String dbPassword) throws RemoteException, SQLException {
        this.id = id;
        this.dbConnection = new DatabaseConnection(dbUrl, dbUser, dbPassword);
    }

    @Override
    public void receiveSQL(String sql) throws RemoteException {
        try {
            dbConnection.executeSQL(sql);
            System.out.println("Executado no membro " + id + ": " + sql);
        } catch (SQLException e) {
            System.err.println("Falha ao executar SQL no membro " + id);
            e.printStackTrace();
        }
    }

    @Override
    public String getMemberId() throws RemoteException {
        return id;
    }

    public void closeConnection() throws SQLException {
        dbConnection.close();
    }
}
