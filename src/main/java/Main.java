import java.sql.*;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
    	
    	
    	port(8080);
			
        BancoDeDados bd = new BancoDeDados(); 
    	
        CalibracaoDAO calibracaoDAO = new CalibracaoDAO();
        
         
        staticFiles.location("/public");

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
            return;
        });


//    	Cria uma posição no banco de dados e retorna uma hash do respectivo id 
        
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
        
        
//      Recebe hash na rota e retorna os pontos da calibração
       
        get("/api/sketch/:hash", (request, response) -> {
        	System.out.println("tentando pegar pontos");
            String hashid = request.params(":hash");
            Calibracao cal = calibracaoDAO.getCalibracao(hashid);
            return cal.toString();
        });
        
        

//    	recebe hash na rota e pontos no body e atualiza os valores
        
        post("/api/sketch/:hash", (request, response) -> {
            	
        	Calibracao novaCalibracao  = new Calibracao();
            novaCalibracao.setHashid(request.params(":hash"));
	        novaCalibracao.setPontos(request.body());
	        Boolean resultado = calibracaoDAO
	        		.atualizarCalibracao(novaCalibracao);
	        
	        return resultado ? "Ok" : "Erro";
        });
    }
}