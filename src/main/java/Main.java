import static spark.Spark.after;
import static spark.Spark.before; 
import static spark.Spark.halt;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post; 
import static spark.Spark.staticFiles;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.gson.Gson;

import control.CalibracaoDAO;
import control.UsuarioDAO;
import control.JsonSchemaValidator;
import control.TokensControl;
import model.BancoDeDados;
import model.Calibracao;
import model.Usuario;
import model.loginJsonSchema;
import model.pontosJsonSchema;

/**
 * @version 3.0
 * @author Brendon H.S.S
 */

public class Main {

	public static void main(String[] args) throws ProcessingException, IOException {
		
		final String ArquivosEstaticos = "/public";
		final String PATH_HOME_WITH_HASH = "/client/home/:hash";
		final String PATH_LOGIN = "/client/login";
		final String PATH_WITH_TOKEN = "/api/sketch/:Token";
		final String PATH_AUTH_TOKEN = "/auth/token";
		final String PATH_AUTH_ADMIN = "/auth/admin/";
		final String PATH_NEW_SEARCHLOGIN = "/admin/verifyLogin/";
		final String PATH_NEW_USER = "/admin/newUser/";
		
		BancoDeDados BancoDeDadosWebcal = new BancoDeDados();
		CalibracaoDAO calibracaoDAO = new CalibracaoDAO();
		
		port(8081);
		staticFiles.location(ArquivosEstaticos);
		BancoDeDadosWebcal.inicializarBancoDeDados();
		
						
		// Filtros
		after((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Methods", "GET");
			return;
		});
		

		//Método para registro de novo usuário
		post(PATH_NEW_USER, (request, response)->{
					
//			System.out.println(request.body());
			return "";	
		});
		
		post(PATH_NEW_SEARCHLOGIN, (request, response)->{
					
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			usuarioDAO.hasThisLoginInDatabase(request.body());
			
			return usuarioDAO.hasThisLoginInDatabase(request.body()) ? false : true ;	
		});
		
		
		 
		
		
		//Métodos para Login
		post(PATH_LOGIN, (request, response) -> {

			response.type("application/json");
			loginJsonSchema loginSchema = new loginJsonSchema();
			UsuarioDAO usuarioDAO = new UsuarioDAO();

			if (JsonSchemaValidator.isJsonValid(loginSchema.getSchema(), request.body())) {
				Gson gson = new Gson();
				Usuario userWithInputedLoginAndPassword = gson.fromJson(request.body(), Usuario.class);
				Usuario userFoundedInDatabase = usuarioDAO.searchUserInDatabase(
						userWithInputedLoginAndPassword.getLogin(),
						userWithInputedLoginAndPassword.getSenha());

				if (userFoundedInDatabase.getLogin() == null && userFoundedInDatabase.getSenha() == null) {

					return usuarioDAO.validateLoginAndPassword(userWithInputedLoginAndPassword.getLogin());

				} else if (userFoundedInDatabase.getLogin().equals(userWithInputedLoginAndPassword.getLogin())
					&& userFoundedInDatabase.getSenha().equals(userWithInputedLoginAndPassword.getSenha())) {
					
					
					TokensControl geradorDeTokens = new TokensControl();
					final long ONEDAYTIMEINMILLIS = 1000 * 60 * 60 * 24;
					final String Token = geradorDeTokens.getToken(ONEDAYTIMEINMILLIS, userFoundedInDatabase);

					response.header("Authorization", "Bearer " + Token);
					
					
					return "Login e senha corretos:" + Token + ":" + userFoundedInDatabase.getUsuarioAdmin();
				}
			} else {
				return "Json incorreto";
			}

			usuarioDAO.fecharConexao();
			return request;
		});

		
		
		
		
		//Métodos Getters e Setters das amostras
				
		//Get Token and return Hash 		
		get(PATH_WITH_TOKEN, (req, res) -> {
			 
			String token = req.params(":Token");	
			TokensControl tokencontrol = new TokensControl();
			Usuario user = tokencontrol.getUsuarioWithToken(token);
			
			if(user!=null) {
				
				Connection connection = BancoDeDadosWebcal.criarConexao();
				Statement statement = connection.createStatement();
				PreparedStatement preparedStatement;
				
				String createRowIntoWebcalTable = "INSERT INTO webcal (pontos) VALUES (null);";
				statement.execute(createRowIntoWebcalTable, Statement.RETURN_GENERATED_KEYS);
				ResultSet generatedKeys = statement.getGeneratedKeys();
				generatedKeys.next();
				int idGerado = generatedKeys.getInt(1);
				Calibracao novoCalibracao = new Calibracao();
				novoCalibracao.setId(idGerado);
			    statement.close();
				
				String insertUserIdIntoWebcalTable = "UPDATE webcal SET id_fk_usuario = ?, desabilitado = ?  WHERE id = ? ";
				preparedStatement = connection.prepareStatement(insertUserIdIntoWebcalTable);
				preparedStatement.setLong(1, user.getidUsuario());
				preparedStatement.setInt(2, 1);
				preparedStatement.setLong(3, novoCalibracao.getId());
		        preparedStatement.executeUpdate();
		        
		        if(preparedStatement.getUpdateCount() > 0) {
			        preparedStatement.close();
					BancoDeDadosWebcal.fecharConexao(connection);
					return novoCalibracao.getHashid();
		        }
			}
			
	        return "";
	    });
				
		//Get pontos and return isAtualized		
		post(PATH_HOME_WITH_HASH, (request, response) -> {
			pontosJsonSchema pontosSchema = new pontosJsonSchema();
			Boolean resultado = false;

			if (JsonSchemaValidator.isJsonValid(pontosSchema.getSchema(), request.body())) {
				Calibracao novaCalibracao = new Calibracao();
				novaCalibracao.setHashid(request.params(":hash"));
				novaCalibracao.setPontos(request.body());
				resultado = calibracaoDAO.atualizarCalibracao(novaCalibracao);
			}

			return resultado ? "atualizado" : "naoatualizado";

		});
				
		//Get hash and return pontos + isDesabilitado		
		get(PATH_HOME_WITH_HASH, (request, response) -> {
			
			String hashid = request.params(":hash");
			Calibracao cal = calibracaoDAO.getCalibracao(hashid);
			System.out.println(cal.getPontos());
			
			return cal.toString()+";"+cal.getDesabilitado();
			
		});
		

		
		
		
		
		//Métodos para autenticação de Token e das credenciais de ADM
		
		post(PATH_AUTH_TOKEN, (request, response) -> {
			final String token = request.body();
			TokensControl tokencontrol = new TokensControl();
			return tokencontrol.validateToken(token);
			
		});
		
		post(PATH_AUTH_ADMIN, (request,response)->{
			final String Token = request.body();
			TokensControl tokencontrol =  new TokensControl();
			Usuario userFounded = tokencontrol.getUsuarioWithToken(Token);
			return  userFounded.getUsuarioAdmin();
			
		});
		
	}
}