package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import model.BancoDeDados;
import model.Usuario;
import java.util.LinkedList;

public class UsuarioDAO {

	private Connection con = null;
	private BancoDeDados bd = null;
	private String searchForAllFields = "SELECT * FROM usuarios";
	private String searchWithLoginAndPassword = searchForAllFields + " WHERE login = ? AND senha = ?";
	private String searchWithLogin = searchForAllFields + " WHERE login = ?";
	private String insertNewUser = "INSERT INTO usuarios (nome , senha , login , UsuarioAdmin , "
			+ "data_de_criacao , qtd_medidores ) VALUES (?,?,?,?,?,?) ";
	private String selectAllUsers = "SELECT * from usuarios";
	private String updateUser = "UPDATE `pontos_calibracao`.`usuarios` SET `nome` = ?, `senha` = ?,"
			+ " `login` = ?, `UsuarioAdmin` = ? , `qtd_medidores` = ?  WHERE `id_usuario`= ? ;";
    private String deleteUserWithId = "DELETE FROM `pontos_calibracao`.`usuarios` WHERE `id_usuario`= ? ";
    
    
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
	 
	public boolean hasThisLoginInDatabase(String login) {
		try {
			preparedStatement = con.prepareStatement(searchWithLogin);
			preparedStatement.setString(1, login); 
			ResultSet result = preparedStatement.executeQuery();

			if (!result.isBeforeFirst()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return true;
	} 
	
	public boolean insertNewUsuario(Usuario user) {

		try {
	        
			preparedStatement = con.prepareStatement(insertNewUser);
			preparedStatement.setString(1, user.getNome());
			preparedStatement.setString(2, user.getSenha());
			preparedStatement.setString(3, user.getLogin());
			preparedStatement.setInt(4, user.getUsuarioAdmin());
			preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setInt(6, user.getQuantidadeDeMedidores());
			
			preparedStatement.executeUpdate();
	    	
			if (preparedStatement.getUpdateCount() > 0) {

		        preparedStatement.close();
				return true;
			} else {
				return false;
			}

		} catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		
		
		return false;
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
						.setUsuarioAdmin(result.getInt(5))
						.setQuantidadeDeMedidores(result.getInt(6));
				
				return usuarioEncontrado;
			}
		} catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return null;
	}
	
	public LinkedList<Usuario> listAllUsers() {
		try {
			preparedStatement = con.prepareStatement(selectAllUsers);
			ResultSet result = preparedStatement.executeQuery();
			LinkedList<Usuario> listaDeUsuarios = new LinkedList<Usuario>();
	        
			while(result.next()) {
				
				Usuario  user =  new Usuario();
				user.setidUsuario(result.getInt(1))
				.setNome(result.getString(2))
				.setSenha(result.getString(3))
				.setLogin(result.getString(4))
				.setUsuarioAdmin(result.getInt(5))
				.setdataDeCriacao(result.getTimestamp(6))
				.setQuantidadeDeMedidores(result.getInt(7));
				
				listaDeUsuarios.add(user);
			}
			
			return listaDeUsuarios;
		}catch(SQLException error) {
			System.out.println(error.getMessage());
		}
		
		return null;
	}
	
	public boolean atualizarUsuario(Usuario user) {
		
	try {
			preparedStatement = con.prepareStatement(updateUser);
			preparedStatement.setString(1, user.getNome());
			preparedStatement.setString(2, user.getSenha());
			preparedStatement.setString(3, user.getLogin());
			preparedStatement.setInt(4, user.getUsuarioAdmin());
			preparedStatement.setInt(5, user.getQuantidadeDeMedidores());
			preparedStatement.setLong(6, user.getidUsuario());
			
			
			preparedStatement.executeUpdate();
			
	    	if (preparedStatement.getUpdateCount() > 0) {

		        preparedStatement.close();
				return true;
			} else {

		        preparedStatement.close();
				return false;
			}
	
			 
			} catch (SQLException error) {
				System.out.println(error.getMessage());
			}
		
		
			return false;
	}
	
	
	public boolean deletarUsuario(long id) {
		
	try {
			preparedStatement = con.prepareStatement(deleteUserWithId);
			preparedStatement.setLong(1, id);
			preparedStatement.executeUpdate();
			
	    	if (preparedStatement.getUpdateCount() > 0) {
	
		        preparedStatement.close();
				return true;
			} else {
				return false;
			}
	
			 
			} catch (SQLException error) {
				System.out.println(error.getMessage());
			}
		
		
			return false;
	}
	
	
		
}

