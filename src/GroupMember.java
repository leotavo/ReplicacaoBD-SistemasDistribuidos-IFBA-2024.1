import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GroupMember extends UnicastRemoteObject implements GroupMemberInterface {

    private static final long serialVersionUID = 1L;

    private String memberId;
    private String databaseUrl;
    private String username;
    private String password;

    public GroupMember(String id, String databaseUrl, String username, String password) throws RemoteException {
        this.memberId = id;
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;

        try {
            // Conecta ao registro RMI existente criado pelo GroupLeader na porta 1099
            Registry registry = LocateRegistry.getRegistry("localhost",1099);
            System.out.println("Registro RMI encontrado na porta 1099.");

            // Rebind do objeto "GroupMember" no registro RMI
            String memberName = "GroupMember" + id; // Define o nome do membro como GroupMember+id
            registry.rebind(memberName, this);
            System.out.println("Registrando o objeto remoto " + memberName + " no registro RMI");
            

            // Solicita ao líder para adicionar este membro ao grupo
            GroupMemberInterface leader = (GroupMemberInterface) registry.lookup("GroupLeader");
            
            System.out.println(memberName + " solicitando ao GroupLeader adesão ao grupo");
            leader.joinGroup(id);
            
        } catch (RemoteException e) {
            throw new RemoteException("Falha ao registrar o GroupMember no registro RMI", e);
        } catch (Exception e) {
            throw new RemoteException("Erro inesperado: ", e);
        }
    }

    @Override
    public synchronized QueryResult executeSQLCommand(String sqlCommand) throws RemoteException, SQLException {
        QueryResult memberResult = null;
        Connection tempConnection = null;

        try {
            // Conecta ao banco de dados do membro
            tempConnection = DriverManager.getConnection(databaseUrl, username, password);

            // Executa o comando SQL no membro
            memberResult = SqlExecutor.executeSql(tempConnection, sqlCommand);
        } catch (SQLException e) {
            System.err.println("Falha ao executar o comando SQL no membro: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            // Fecha a conexão se estiver aberta
            if (tempConnection != null && !tempConnection.isClosed()) {
                tempConnection.close();
            }
        }

        return memberResult;
    }

    @Override
    public boolean ping() throws RemoteException {
        // Simplesmente retorna true para indicar que o membro está ativo
        return true;
    }

    @Override
    public void joinGroup(String memberId) throws RemoteException {
        // O método joinGroup é definido na interface, mas não é usado diretamente pelo membro
        throw new UnsupportedOperationException("Este método não deve ser chamado diretamente no membro.");
    }

    @Override
    public void leaveGroup(String memberId) throws RemoteException {
        // O método leaveGroup é definido na interface, mas não é usado diretamente pelo membro
        throw new UnsupportedOperationException("Este método não deve ser chamado diretamente no membro.");
    }

    public static void main(String[] args) {
        try {
            // Verifica se os argumentos estão corretos
            if (args.length < 3) {
                throw new IllegalArgumentException("Número insuficiente de argumentos fornecidos.");
            }
            String memberId = args[0];  // ID do membro
            String memberDatabaseUrl = args[1]; // URL do banco de dados
            String username = args[2];  // Nome de usuário do banco de dados
            String password = args[3];  // Senha do banco de dados

            new GroupMember(memberId, memberDatabaseUrl, username, password);
            System.out.println("GroupMember" + memberId + " está em execução e pronto para aceitar solicitações.");
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        } catch (RemoteException e) {
            System.err.println("Erro de RemoteException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Falha geral ao iniciar o GroupMember: " + e.getMessage());
            e.printStackTrace();
        }
    }
}