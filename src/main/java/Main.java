import java.io.IOException;
import java.sql.*;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import control.CalibracaoDAO;
import control.UsuarioDAO;
import control.JsonSchemaValidator;
import control.TokensControl;
import model.BancoDeDados;
import model.Calibracao;
import model.Usuario;
import model.loginJsonSchema;
import model.pontosJsonSchema; 
import com.google.gson.Gson;
import static spark.Spark.*;

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
		BancoDeDados bd = new BancoDeDados();
		CalibracaoDAO calibracaoDAO = new CalibracaoDAO();
		staticFiles.location("/public");

		after((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Methods", "GET");
			return;
		});

		post("/client/login", (request, response) -> {
			
			response.type("application/json");
			loginJsonSchema loginSchema = new loginJsonSchema();
			UsuarioDAO usuarioDAO = new UsuarioDAO();

			if (JsonSchemaValidator.isJsonValid(loginSchema.getSchema(), request.body())) {
				Gson gson = new Gson();
				Usuario userWithOnlyLoginAndPassword = gson.fromJson(request.body(), Usuario.class);

				Usuario user = usuarioDAO.getUsuario(userWithOnlyLoginAndPassword.getLogin(),
						userWithOnlyLoginAndPassword.getSenha());
				
				if (user.getLogin() == null && user.getSenha() == null) {

					return usuarioDAO.hasThisLogin(userWithOnlyLoginAndPassword.getLogin());
				
				}else if(user.getLogin().equals(userWithOnlyLoginAndPassword.getLogin())
						&& user.getSenha().equals(userWithOnlyLoginAndPassword.getSenha())) {
					
					TokensControl geradorDeTokens = new TokensControl();
				 	long ONEDAYTIMEINMILLIS = 60 * 60 * 24 * 1000;
//				 	int ONEDAYTIMEINSECS =  60 * 60 * 24;
				 	
				 	String Token = geradorDeTokens.getToken(ONEDAYTIMEINMILLIS, user);
				 	String parsedToStringToken = geradorDeTokens.parseTokenToString(Token);
				 	System.out.println(parsedToStringToken);
					
				 	response.header("Authorization", "Bearer "+Token);
				 	
//				 	response.cookie("/","Token", Token ,ONEDAYTIMEINSECS,false,true);
				 	
				 	return "Login e senha corretos";
				} 
			} else {
				return "Json incorreto";
			}
			
			usuarioDAO.fecharConexao();
			return request;
		});
		
	  
		
		get("/api/sketch", (req, res) -> {

			Connection connection = bd.criarConexao();
			Statement statement = connection.createStatement();
			
			statement.execute("INSERT INTO webcal (pontos) VALUES (null);", Statement.RETURN_GENERATED_KEYS);
			ResultSet generatedKeys = statement.getGeneratedKeys();
			generatedKeys.next();
			int idGerado = generatedKeys.getInt(1);
			Calibracao novoCalibracao = new Calibracao();
			novoCalibracao.setId(idGerado);
			
			statement.close();
			bd.fecharConexao(connection);
			return novoCalibracao.getHashid();
		});

		get("/api/sketch/:hash", (request, response) -> {

			String hashid = request.params(":hash");
			Calibracao cal = calibracaoDAO.getCalibracao(hashid);
			return cal.toString();
		});

		post("/api/sketch/:hash", (request, response) -> {
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
	}
}