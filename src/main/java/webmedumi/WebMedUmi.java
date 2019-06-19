package webmedumi;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.post; 
import static spark.Spark.staticFiles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import com.google.gson.Gson;
import control.CalibracaoDAO;
import control.UsuarioDAO;
import control.JsonSchemaValidator;
import control.TokensControl;
import model.BancoDeDados;
import model.Calibracao;
import model.ConfiguracoesCalibracao;
import model.CalibracaoMB;
import model.Usuario; 
import model.loginJsonSchema;
import model.pontosJsonSchema;
import spark.servlet.SparkApplication;

/**
 * @version 3.0
 * @author Brendon H.S.S
 */

public class WebMedUmi implements SparkApplication {


	public WebMedUmi() {}
		
		
	public void init() {
		
		final String ArquivosEstaticos = "/public";
		final String PATH_HOME_WITH_HASH = "/client/home/:hash";
		final String PATH_LOGIN = "/client/login";
		final String PATH_WITH_TOKEN = "/api/sketch/:Token";
		final String PATH_WITH_MB ="/api/sketch/MB";
		final String PATH_AUTH_TOKEN = "/auth/token";
		final String PATH_AUTH_ADMIN = "/auth/admin/";
		final String PATH_SEARCHLOGIN = "/admin/verifyLogin/";
		final String PATH_NEW_USER = "/admin/newUser/";
		final String PATH_EDIT_USER = "/admin/editUser/";
		final String PATH_LIST_USERS = "/admin/listUsers/";
		final String PATH_DEL_USER = "/admin/deleteUser/";
		final String PATH_USER_HISTORIC = "/admin/userHistory";
		final String PATH_USER_NAME = "/client/userName/";
		final String PATH_USER_ID = "/client/userID/";
		final String PATH_IS_ADMIN = "/client/isUserAdmin/";
		final String PATH_GET_HASH = "/client/getHash/";
		final String PATH_SET_CONFIGURACOES = "/client/setConfiguracoes/";
		
		
		BancoDeDados BancoDeDadosWebcal = new BancoDeDados();
		CalibracaoDAO calibracaoDAO = new CalibracaoDAO();
		staticFiles.location(ArquivosEstaticos);
		BancoDeDadosWebcal.inicializarBancoDeDados();
		
						
		// Filtros
		
		after((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Methods", "GET");
			response.header("Access-Control-Allow-Methods", "POST");
			return;
		});
		
		get("/test" , (request,response) ->{
			
			return "ok";
			
		});
		
		//User control
		
		post(PATH_USER_ID , (request, response)->{
					
			TokensControl tokenControl =  new TokensControl();
			Usuario user = tokenControl.getUsuarioWithToken(request.body());
			
			return user.getidUsuario();
		});

		post(PATH_IS_ADMIN , (request, response)->{
			
			TokensControl tokenControl =  new TokensControl();
			Usuario user = tokenControl.getUsuarioWithToken(request.body());
			
			return user.getUsuarioAdmin();
		});

		
		post(PATH_USER_NAME , (request, response)->{
			
			TokensControl tokenControl =  new TokensControl();
			Usuario user = tokenControl.getUsuarioWithToken(request.body());
			
			return user.getNome();
		});
		
		post(PATH_DEL_USER, (request, response)->{
			

			System.out.println(request.body());
			UsuarioDAO usuariodao =  new UsuarioDAO();
		 	final long userID = Long.parseLong(request.body());
		 	boolean isUserDeleted = false;
		 	
		 	if(calibracaoDAO.getHistoricoDoUser(userID).size() == 0) {
		 		isUserDeleted = usuariodao.deletarUsuario(userID);
		 	}else {
		 		calibracaoDAO.deletarHistorico(userID);
	 			isUserDeleted = usuariodao.deletarUsuario(userID);
			}
		 	
			return isUserDeleted ;
		});
		
		
		post(PATH_EDIT_USER, (request, response)->{
			
			System.out.println(request.body());
			Gson gson = new Gson();
			Usuario UserInfos = gson.fromJson(request.body(), Usuario.class);
			UsuarioDAO usuariodao =  new UsuarioDAO();
			boolean isUserAtualized = usuariodao.atualizarUsuario(UserInfos);
			
			return isUserAtualized;
			
		});
		
		
		post(PATH_SEARCHLOGIN, (request, response)->{
					
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			usuarioDAO.hasThisLoginInDatabase(request.body());
			
			return usuarioDAO.hasThisLoginInDatabase(request.body()) ? false : true ;	
		});
		
		
		post(PATH_NEW_USER, (request, response)->{

			Gson gson = new Gson();
			Usuario user = gson.fromJson(request.body(), Usuario.class);
			UsuarioDAO usuariodao = new UsuarioDAO();
			Boolean result = usuariodao.insertNewUsuario(user);
			
			return result;	
		});
		
		
		get(PATH_LIST_USERS, (request, response)->{
			
			UsuarioDAO usuariodao = new UsuarioDAO();
			LinkedList<Usuario> lista = usuariodao.listAllUsers();
			return lista;
		});
		
		
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


		//Tokens control
		
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
		
		
		//amostras control
				

		post(PATH_SET_CONFIGURACOES , (request, response)->{
			Gson gson = new Gson();
			ConfiguracoesCalibracao configuracoes =  gson.fromJson(request.body(), ConfiguracoesCalibracao.class);
			Calibracao calibracao = new Calibracao();
			calibracao.setHashid(configuracoes.getHash());
			
			boolean result = calibracaoDAO.setConfiguracoes(calibracao , configuracoes);
			
			return result;
		});


		post(PATH_WITH_MB, (request, response)->{
					
			Gson gson = new Gson();
			CalibracaoMB calibracao =  gson.fromJson(request.body(), CalibracaoMB.class);
			Calibracao novaCalibracao = new Calibracao();
			novaCalibracao.setHashid(calibracao.getHash());
			boolean resultado = calibracaoDAO.atualizarMB(novaCalibracao, calibracao);
			
			
			return resultado;
		});
  
		
		
		get(PATH_HOME_WITH_HASH, (request, response) -> {
			
			String hashid = request.params(":hash");
			Calibracao cal = calibracaoDAO.getCalibracao(hashid);
			return cal.toString();
			
		});
		
		post(PATH_USER_HISTORIC, (request, response)->{
			
			Long userID = Long.parseLong(request.body());
			LinkedList<Calibracao> historicoDoUsuario = new LinkedList<Calibracao>();
			CalibracaoDAO calibracaodao =  new CalibracaoDAO();
			historicoDoUsuario = calibracaodao.getHistoricoDoUser(userID);
			Gson gson = new Gson();
			
			return gson.toJson(historicoDoUsuario);	
		});
				
		
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
		
		 
		post(PATH_HOME_WITH_HASH, (request, response) -> {
			pontosJsonSchema pontosSchema = new pontosJsonSchema();
			Boolean resultado = false;

			if (JsonSchemaValidator.isJsonValid(pontosSchema.getSchema(), request.body())) {
				Calibracao novaCalibracao = new Calibracao();
				novaCalibracao.setHashid(request.params(":hash"));
				novaCalibracao.setPontos(request.body());
				resultado = calibracaoDAO.atualizarCalibracao(novaCalibracao);
				System.out.println(resultado);
			}

			return resultado ? "atualizado" : "naoatualizado";

		});
			
		
		post(PATH_GET_HASH, (request, response) -> {
			
			Calibracao calibracao = new Calibracao();
			calibracao.setId(Long.parseLong(request.body()));
			return calibracao.getHashid();
			
		});
		
	}
	
	public void destroy() {
		
	}
}