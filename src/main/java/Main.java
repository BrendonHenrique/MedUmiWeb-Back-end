import static spark.Spark.after; 
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
 * Classe responsável pelo request e response da API do wbecalmedumi.
 * 
 * A API é repartida em três partes:
 * 
 * Primeiro GET : Responsável pela criação de uma posição no banco , com valores
 * de pontos nulos e retorno do ID criado em formato de HASH.
 * 
 * Segundo GET : Recebe o ID no formato de hash na rota /api/sketch e retorna os
 * pontos da calibração no body do response.
 * 
 * POST : Recebe hash na rota e pontos no body e atualiza os valores no ID
 * correspondente do banco de dados e retorna no body do response.
 * 
 * 
 * @version 3.0
 * @author Brendon H.S.S
 */

public class Main {

	public static void main(String[] args) throws ProcessingException, IOException {
		port(8081);
		
		final String ArquivosEstaticos = "/public";
		staticFiles.location(ArquivosEstaticos);
		final String PATH_HOME_WITH_HASH = "/client/home/:hash";
		final String PATH_LOGIN = "/client/login";
		final String PATH_WITH_TOKEN = "/api/sketch/:Token";
		BancoDeDados BancoDeDadosWebcal = new BancoDeDados();
		BancoDeDadosWebcal.inicializarBancoDeDados();
		CalibracaoDAO calibracaoDAO = new CalibracaoDAO();

		
		after((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Methods", "GET");
			return;
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


		post("/auth/", (request, response) -> {
			final String token = request.body();
			TokensControl tokencontrol = new TokensControl();
			return tokencontrol.validateToken(token);
			
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
			}

			return resultado ? "atualizado" : "naoatualizado";

		});
		
		
		get(PATH_HOME_WITH_HASH, (request, response) -> {
			
			String hashid = request.params(":hash");
			Calibracao cal = calibracaoDAO.getCalibracao(hashid);
			System.out.println(cal.getPontos());
			
			return cal.toString()+";"+cal.getDesabilitado();
			
		});
	}
}