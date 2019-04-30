import java.io.IOException;
import java.sql.*;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import static spark.Spark.*;

/**
* Classe responsável pelo request e response da API do wbecalmedumi.
* 
* A API é repartida em três partes:
* 
* Primeiro GET : Responsável pela criação de uma posição no banco , com valores de pontos nulos
* e retorno do ID criado em formato de HASH.
* 
* Segundo GET : Recebe o ID no formato de hash na rota /api/sketch 
* e retorna os pontos da calibração no body do response.
* 
* POST : Recebe hash na rota e pontos no body e atualiza os valores no ID correspondente 
* do banco de dados e retorna no body do response.
        
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
        
        get("/api/sketch", (req, res) -> {
        	
        	Connection connection = bd.criarConexao();
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO webcal (pontos) VALUES (null);", Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int idGerado = generatedKeys.getInt(1);
            Calibracao novoCalibracao  = new Calibracao();
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
	     	pontosJsonSchema pontosSchema =  new pontosJsonSchema();
	       	Boolean resultado = false;
       	 
		    if (ValidationUtils.isJsonValid(pontosSchema.getSchema(), request.body())){
		      	Calibracao novaCalibracao  = new Calibracao();
		        novaCalibracao.setHashid(request.params(":hash"));
		        novaCalibracao.setPontos(request.body());
		        resultado = calibracaoDAO.atualizarCalibracao(novaCalibracao);
	 		}
            
        return resultado ? "atualizado" : "naoatualizado";
        
        });
    }
}