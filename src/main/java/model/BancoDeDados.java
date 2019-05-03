package model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BancoDeDados {

    public Connection criarConexao() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = null;
        conn = DriverManager
        		.getConnection("jdbc:mysql://localhost:3306/pontos_calibracao",
				"root", "1234");
        return conn;
    }

    public void fecharConexao(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Falha ao fechar conexao com o banco : "+ e.getMessage());
            }
        }
    }

    public void inicializarBancoDeDados() { 
        try {
            Connection connection = criarConexao();
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS webcal (id INTEGER PRIMARY KEY AUTO_INCREMENT, pontos JSON) ");
            statement.close();
            fecharConexao(connection);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Falha na inicialização do banco: "+e.getMessage());
        }
    }
}