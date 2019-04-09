import java.sql.*;

public class CalibracaoDAO {
    private Connection con = null;
    private BancoDeDados bd = null;

    public CalibracaoDAO() {
        this.bd = new BancoDeDados();
        try {
            this.con = bd.criarConexao();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Calibracao getCalibracao(String hashid) {

        Calibracao cal = new Calibracao();
        cal.setHashid(hashid);

        String selectHash = "SELECT id, pontos FROM webcal where id = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.con.prepareStatement(selectHash);
            preparedStatement.setLong(1, cal.getId());
            ResultSet rs = preparedStatement.executeQuery();

            String pontos = null;
            Date dataCriacao = null, dataModificacao = null;
            while (rs.next()) {
                pontos = rs.getString("pontos");
                dataCriacao = rs.getDate("dataDeCriacao");
                dataModificacao = rs.getDate("dataDeCriacao");
            }
            preparedStatement.close();
            cal.setDataDeCriacao(dataCriacao);
            cal.setDataDeModificacao(dataModificacao);
            cal.setPontos(pontos);
            return cal;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void atualizarCalibracao(Calibracao cal) {
        cal = new Calibracao();

    }

    public Calibracao criarNovaCalibracao() {
        return null;
    }

    public void fecharConexao() {
        bd.fecharConexao(con);
    }

}
