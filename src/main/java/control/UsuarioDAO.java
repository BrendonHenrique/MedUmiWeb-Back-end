package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.BancoDeDados;
import model.Usuario;

public class UsuarioDAO {

	private Connection con = null;
	private BancoDeDados bd = null;
	String searchForAllFields = "SELECT id_usuario,nome,senha,login,UsuarioAdmin FROM usuarios";
	String searchWithLoginAndPassword = searchForAllFields + " WHERE login = ? AND senha = ?";
	String searchWithLogin = searchForAllFields + " WHERE login = ?";
	PreparedStatement preparedStatement;

	public UsuarioDAO() {
		this.bd = new BancoDeDados();
		try {
			this.con = bd.criarConexao();
		} catch (ClassNotFoundException | SQLException error) {
			System.out.println(error.getMessage());
		}
	}

	public void fecharConexao() {
		bd.fecharConexao(con);
		try {
			preparedStatement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public String validateLoginAndPassword(String login) {
		try {
			preparedStatement = con.prepareStatement(searchWithLogin);
			preparedStatement.setString(1, login);
			ResultSet result = preparedStatement.executeQuery();

			if (!result.isBeforeFirst()) {
				return "Login incorreto";
			} else {
				return "Senha incorreta";
			}
		} catch (SQLException error) {
			return error.getMessage();
		}
	}

	public Usuario searchUserInDatabase(String login, String senha) {
		try {
			preparedStatement = con.prepareStatement(searchWithLoginAndPassword);
			preparedStatement.setString(1, login);
			preparedStatement.setString(2, senha);
			ResultSet result = preparedStatement.executeQuery();

			if (!result.isBeforeFirst()) {
				return new Usuario();
			} else {

				result.next();
				Usuario usuarioEncontrado = new Usuario()
						.setidUsuario(result.getLong(1))
						.setNome(result.getString(2))
						.setSenha(result.getString(3))
						.setLogin(result.getString(4))
						.setUsuarioAdmin(result.getInt(5));

				return usuarioEncontrado;
			}
		} catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return null;
	}

}
