
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupLeader extends UnicastRemoteObject implements GroupMemberInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private String leaderId;
    private Connection leaderConnection;
    
    private List<String> memberNames;

    public GroupLeader(String id, String databaseUrl, String username, String password) throws RemoteException, SQLException {
		this.leaderId = id;
		this.memberNames = new ArrayList<>();
        
		
        try {
            leaderConnection = DriverManager.getConnection(databaseUrl, username, password);
            
            Registry registry;
            try {
                // Tenta obter um registro RMI existente na porta 1099
                registry = LocateRegistry.getRegistry("localhost",1099);
                // Tenta verificar se o objeto já está registrado
                registry.lookup("GroupLeader");
                System.out.println("Registro RMI já está em execução na porta 1099. Reutilizando o registro existente.");
            } catch (NotBoundException e) {
                // Se o objeto "GroupLeader" não estiver registrado
                System.out.println("O objeto 'GroupLeader' não estava vinculado no registro.");
                registry = LocateRegistry.getRegistry("localhost",1099); // Reusa o registro existente
            } catch (RemoteException e) {
                // Se não conseguir obter o registro, cria um novo registro RMI
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("Registro RMI criado na porta 1099.");
            }

            // Rebind do objeto "GroupLeader" no registro RMI
            registry.rebind("GroupLeader", this);            
            System.out.println("GroupLeader registrado no Registro RMI com ID = " + id);
        } catch (SQLException e) {
            throw new RemoteException("Falha ao conectar ao banco de dados do GroupLeader: ", e);
        } catch (RemoteException e) {
            throw new RemoteException("Falha ao registrar o GroupLeader no registro RMI", e);
        }
	}

    @Override
    public synchronized QueryResult executeSQLCommand(String sqlCommand) throws RemoteException, SQLException {
        QueryResult leaderResult = null;

        // Executa o comando SQL no líder
        try {
            leaderResult = SqlExecutor.executeSql(leaderConnection, sqlCommand);
        } catch (Exception e) {
            System.err.println("Falha ao executar o comando SQL no líder: " + e.getMessage());
            e.printStackTrace();
        }

        // Verifica se o comando foi executado com sucesso no líder
        if (leaderResult != null) {
        	System.out.println("Comando SQL executado no GroupLeader: " + sqlCommand);
        	System.out.println("Resultado da execução do comando SQL no GroupLeader:");
            System.out.println(leaderResult.toString());
        	
            Registry registry = LocateRegistry.getRegistry("localhost",1099);
            Iterator<String> iterator = memberNames.iterator();
            
        	
            while (iterator.hasNext()) {
                String memberName = iterator.next();
                boolean success = false;
                int attempt = 0;
                final int maxAttempts = 3;
                final long retryDelay = 5000; // 5 segundos de intervalo entre tentativas

                // Tenta verificar se o membro está respondendo
                while (attempt < maxAttempts && !success) {
                    try {
                        GroupMemberInterface remoteMember = (GroupMemberInterface) registry.lookup(memberName);

                        // Verifica se o membro está respondendo
                        try {
                        	System.out.println("Enviando ping para " + memberName);
                            if (remoteMember.ping()) {
                            	System.out.println(memberName + " respondeu ao ping");
                            	
                                // Executa o comando SQL no membro
                                QueryResult memberResult = null;
                                try {
                                	System.out.println("Enviando comando SQL ao " + memberName);
                                    memberResult = remoteMember.executeSQLCommand(sqlCommand);

                                    // Verifica consistência dos resultados
                                    if (memberResult != null && leaderResult.equals(memberResult)) {
                                        success = true; // Comando executado com sucesso
                                        System.out.println("Comando SQL executado no" + memberName + ": " + sqlCommand);
                                    	System.out.println("Resultado da execução do comando SQL no " + memberName);
                                        System.out.println(memberResult.toString());
                                    } else {
                                        System.out.println(memberName + " removido do grupo devido à inconsistências com o resultado do comando no GroupLeader.");
                                        iterator.remove();
                                        break; // Sai do loop de tentativas para este membro
                                    }

                                } catch (SQLException e) {
                                    System.out.println(memberName + " falhou ao executar o comando SQL. Tentando novamente... (Tentativa " + (attempt + 1) + ")");
                                    e.printStackTrace();
                                } catch (RemoteException e) {
                                    System.out.println(memberName + " removido devido a uma falha de RemoteException ao executar o comando.");
                                    iterator.remove();
                                    break; // Sai do loop de tentativas para este membro
                                }

                            } else {
                                System.out.println(memberName + " não está respondendo. Tentando novamente... (Tentativa " + (attempt + 1) + ")");
                            }
                        } catch (RemoteException e) {
                            System.out.println("Falha ao verificar o ping do membro " + memberName + ". Tentando novamente... (Tentativa " + (attempt + 1) + ")");
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        System.out.println(memberName + " falhou ao tentar acessar o membro. Tentando novamente... (Tentativa " + (attempt + 1) + ")");
                        e.printStackTrace();
                    }

                    // Incrementa o número da tentativa
                    attempt++;

                    // Se não for a última tentativa, aguarda antes de tentar novamente
                    if (attempt < maxAttempts) {
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt(); // Restaurar o estado de interrupção
                            throw new RemoteException("Falha ao aguardar entre as tentativas: " + ie.getMessage(), ie);
                        }
                    } else {
                        // Se atingiu o número máximo de tentativas, remove o membro
                        System.out.println(memberName + " removido devido a falhas repetidas de ping.");
                        iterator.remove();
                    }
                }
            }
        } else {
            // Caso o comando SQL falhe no líder, ele não é distribuído aos membros
            System.out.println("Comando SQL não foi executado no líder, portanto, não foi distribuído para os membros.");
            return null;
        }

        return leaderResult;
    }

	@Override
	public synchronized void joinGroup(String memberId) throws RemoteException {
		String memberName = "GroupMember" + memberId;
        if (!memberNames.contains(memberName)) {
            memberNames.add(memberName);
            System.out.println(memberName + " adicionado ao grupo.");
        }
        else {
        	System.out.println("Já existe um membro do grupo com o nome = " + memberName);
        }
	}

	@Override
	public void leaveGroup(String memberId) throws RemoteException {
		String memberName = "GroupMember" + memberId;
        if (memberNames.contains(memberName)) {
            memberNames.remove(memberName);
            System.out.println(memberName + " removido do grupo.");
        }
        else {
        	System.out.println("Não existe membro do grupo com o nome = " + memberName);
        }
	}

    
    @Override
	public boolean ping() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}
    
    public static void main(String[] args) {
    	try {
    		// Verifica se os argumentos estão corretos
            if (args.length < 3) {
                throw new IllegalArgumentException("Número insuficiente de argumentos fornecidos.");
            }
            String leaderDatabaseUrl = args[0];
            String username = args[1];
            String password = args[2];

            new GroupLeader("0",leaderDatabaseUrl, username, password);
            System.out.println("GroupLeader está em execução e pronto para aceitar solicitações.");
    	} catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro de SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (RemoteException e) {
            // Trata o erro de porta já em uso
            if (e.getCause() instanceof java.rmi.server.ExportException &&
                e.getCause().getMessage().contains("Port already in use")) {
                System.err.println("Falha ao iniciar o GroupLeader: Porta RMI já está em uso.");
            } else {
                System.err.println("Erro de RemoteException: " + e.getMessage());
            }
            e.printStackTrace();
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Falha geral ao iniciar o GroupLeader: " + e.getMessage());
            e.printStackTrace();
        }

    }

	


}
