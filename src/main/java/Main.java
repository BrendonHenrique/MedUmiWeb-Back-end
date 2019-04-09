import org.hashids.Hashids;
import java.sql.*;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        BancoDeDados bd = new BancoDeDados();
        Hashids hashids = new Hashids();


        staticFiles.location("/public");

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

//                  GERA ID
        get("/api/sketch", (req, res) -> {
            Connection connection = bd.criarConexao();
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO webcal (pontos) VALUES (null);", Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int idGerado = generatedKeys.getInt(1);

            statement.close();
            bd.fecharConexao(connection);

            return hashids.encode(idGerado);
        });


//                  MANDA HASH PARA FRONT
        get("/api/sketch/:hash", (request, response) -> {
            String hashid = request.params(":hash");
            CalibracaoDAO calibracaoDAO = new CalibracaoDAO();
            Calibracao cal = calibracaoDAO.getCalibracao(hashid);
            return cal.toString();
        });

//                  RECEBE 02 PARAMETROS: UM NA ROTA(HASH) E OUTRO NO BODY(PONTOS)
        post("/api/sketch/:hash", (request, response) -> {
            String jsonPontos = request.body();
            String hash = request.params(":hash");
            System.out.println(jsonPontos);

            // decodificar o id
            long[] decodes = hashids.decode(hash);
            long id = decodes[0];

            String update = "UPDATE webcal SET pontos = ?, data_de_modificacao = ? WHERE id = ? ";
            Connection connection = bd.criarConexao();
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, jsonPontos);
            preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setLong(3, id);
            preparedStatement.executeUpdate();
            boolean atualizouAlgo = preparedStatement.getUpdateCount() > 0;
            preparedStatement.close();
            bd.fecharConexao(connection);
            return atualizouAlgo ? "Ok" : "Erro";
        });
    }
}