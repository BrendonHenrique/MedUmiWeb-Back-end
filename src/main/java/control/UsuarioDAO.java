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
	String selectId = "SELECT id_usuario,nome,senha,login,UsuarioAdmin FROM usuarios WHERE login = ? AND senha = ?";

	public UsuarioDAO() {
		this.bd = new BancoDeDados();
		try {
			this.con = bd.criarConexao();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fecharConexao() {
		bd.fecharConexao(con);
	}

	public Usuario getUsuario(String login, String senha) {
		try {
			PreparedStatement preparedStatement = con.prepareStatement(selectId);
			preparedStatement.setString(1, login);
			preparedStatement.setString(2, senha);
			ResultSet rs = preparedStatement.executeQuery();
		
			if (!rs.isBeforeFirst()) {
				this.fecharConexao();
				preparedStatement.close();
				
				return null;
			} else {
				rs.next();
				long resultedSetId = rs.getLong(1);
				String resultedSetNome = rs.getString(2);
				String resultedSetSenha = rs.getString(3);
				String resultedSetLogin = rs.getString(4);
				boolean resultedSetUsuarioAdmin = rs.getBoolean(5);
				Usuario usuarioEncontrado = new Usuario(resultedSetId, resultedSetNome, resultedSetSenha,
						resultedSetLogin, resultedSetUsuarioAdmin);

				this.fecharConexao();
				preparedStatement.close();
				
				return usuarioEncontrado;
			}
		} catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return null;
	}

}
