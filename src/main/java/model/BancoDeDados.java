package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BancoDeDados {

	public Connection criarConexao() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
//Desenvolvimento 
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pontos_calibracao", "root", "1234");
	return conn;
	}

	public void fecharConexao(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Falha ao fechar conexao com o banco : " + e.getMessage());
			}
		}
	}

	public void inicializarBancoDeDados() {
		try {
			Connection connection = criarConexao();
			Statement statement = connection.createStatement();

			statement.execute(
					"CREATE TABLE IF NOT EXISTS `usuarios`\r\n" + "(`id_usuario` int(11) NOT NULL AUTO_INCREMENT,\r\n"
							+ "`nome` varchar(30) NOT NULL,\r\n" + "`senha` varchar(30) NOT NULL,\r\n"
							+ "`login` varchar(30) NOT NULL,\r\n" + "`UsuarioAdmin` tinyint(1) NOT NULL,\r\n"
							+ "`data_de_criacao` timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\r\n"
							+ "PRIMARY KEY (`id_usuario`)\r\n" + ");");

			statement.execute("CREATE TABLE IF NOT EXISTS `webcal`\r\n" + "(`id` INTEGER AUTO_INCREMENT ,\r\n"
					+ "`pontos` LONGTEXT,\r\n" + "`M` float  DEFAULT NULL,\r\n" + "`B` float  DEFAULT NULL,\r\n"
					+ "`data_de_criacao` datetime  DEFAULT CURRENT_TIMESTAMP ,\r\n"
					+ "`data_de_modificacao` datetime  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\r\n"
					+ "`id_fk_usuario` int(11),\r\n" + "`desabilitado` tinyint(1),\r\n" + "`produto` varchar(30),\r\n"
					+ "`medidor` varchar(30),\r\n" + "PRIMARY KEY (`id`),\r\n"
					+ "KEY `id_fk_usuario` (`id_fk_usuario`),\r\n"
					+ "CONSTRAINT `id_fk_usuario` FOREIGN KEY (`id_fk_usuario`) REFERENCES `usuarios` (`id_usuario`)\r\n"
					+ ");");

			statement.close();
			fecharConexao(connection);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Falha na inicialização do banco: " + e.getMessage());

		}
	}
}
