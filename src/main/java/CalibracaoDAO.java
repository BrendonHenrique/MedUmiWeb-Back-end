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
        String pontos = null;
        Date dataCriacao = null, dataModificacao = null;
        
         
    	cal.setHashid(hashid);
		
//    	System.out.println(cal.getId());
    	
    	
        
        String selectHash = "SELECT id, pontos, data_de_criacao, data_de_modificacao FROM webcal where id = ?";

        PreparedStatement preparedStatement = null;
        try {
        	
            preparedStatement = con.prepareStatement(selectHash);
            
            
            preparedStatement.setLong(1, cal.getId());
            
            
            ResultSet rs = preparedStatement.executeQuery();

            rs.next(); 
            
            pontos = rs.getString(2);
            dataCriacao = rs.getDate(3);
            dataModificacao = rs.getDate(4);
        

            preparedStatement.close();
            
            cal.setPontos(pontos);
            cal.setDataDeCriacao(dataCriacao);
            cal.setDataDeModificacao(dataModificacao);

        	System.out.println(cal);

        	return cal;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean atualizarCalibracao(Calibracao cal) throws ClassNotFoundException, SQLException {
      
        String update = "UPDATE webcal SET pontos = ?, data_de_modificacao = ? WHERE id = ? ";
        Connection connection = bd.criarConexao();
        PreparedStatement preparedStatement = connection.prepareStatement(update);
        preparedStatement.setString(1, cal.getPontos());
        preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        preparedStatement.setLong(3, cal.getId());
        preparedStatement.executeUpdate();
        boolean atualizouAlgo = preparedStatement.getUpdateCount() > 0;
    	preparedStatement.close();
        bd.fecharConexao(connection);
        
        if(atualizouAlgo) {
        	return true;
        }else {
    		return false;
        }
    }

    public Calibracao criarNovaCalibracao() {
        return null;
    }

    public void fecharConexao() {
        bd.fecharConexao(con);
    }

}
