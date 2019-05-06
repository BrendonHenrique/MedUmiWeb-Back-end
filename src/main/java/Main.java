import java.io.IOException;
import java.sql.*;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import control.CalibracaoDAO;
import control.UsuarioDAO;
import control.ValidationUtils;
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
			String result;

			if (ValidationUtils.isJsonValid(loginSchema.getSchema(), request.body())) {
				UsuarioDAO usuarioDAO = new UsuarioDAO();
				Gson gson = new Gson();
				Usuario setUserWithOnlyLoginAndPassword = gson.fromJson(request.body(), Usuario.class);
				Usuario user = usuarioDAO.getUsuario(setUserWithOnlyLoginAndPassword.getLogin(),
						setUserWithOnlyLoginAndPassword.getSenha());

				if (user != null) {
					result = "Login e senha corretos";
				} else {
					//Entra em usuarioDAO com setUserWithOnlyLoginAndPassword 
					//Primeiro verifica se existe login no banco de dados e retorna true ou false
						//Se retornar true , então testa a senha , se retornar false , envia -> senha incorreta
						//Se retornar false , então login está incorreto					
					
					System.out.println("Usuario nao encontrado , vamos verificar login e a senha");
					
					result = "Login ou senha incorretos";
				}

			} else {
				result = "Json incorreto";
			}


			return result;
		});

		get("/api/sketch/", (req, res) -> {

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

			if (ValidationUtils.isJsonValid(pontosSchema.getSchema(), request.body())) {
				Calibracao novaCalibracao = new Calibracao();
				novaCalibracao.setHashid(request.params(":hash"));
				novaCalibracao.setPontos(request.body());
				resultado = calibracaoDAO.atualizarCalibracao(novaCalibracao);
			}

			return resultado ? "atualizado" : "naoatualizado";

		});
	}
}