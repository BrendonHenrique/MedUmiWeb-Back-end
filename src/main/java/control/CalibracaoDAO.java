package control;
import java.sql.*;

import model.BancoDeDados;
import model.Calibracao;
import model.CalibracaoMB;

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
    
    public void fecharConexao() {
        bd.fecharConexao(con);
    }

    public Calibracao getCalibracao(String hashid) {

        Calibracao cal = new Calibracao();
        cal.setHashid(hashid);
		
        String selectHash = "SELECT id, pontos, data_de_criacao, data_de_modificacao, desabilitado FROM webcal where id = ?";

        PreparedStatement preparedStatement = null;
        try {
        	
            preparedStatement = con.prepareStatement(selectHash);
            preparedStatement.setLong(1, cal.getId());
            ResultSet rs = preparedStatement.executeQuery();
            rs.next(); 
            
            cal.setPontos(rs.getString(2))
            .setDataDeCriacao(rs.getDate(3))
            .setDataDeModificacao(rs.getDate(4))
            .setDesabilitado(rs.getInt(5));

            preparedStatement.close();
            
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
    
    public Boolean atualizarMB(Calibracao cal, CalibracaoMB MB) throws ClassNotFoundException, SQLException {
      
        String update = "UPDATE webcal SET M = ?, B = ? WHERE id = ? ";
        Connection connection = bd.criarConexao();
        PreparedStatement preparedStatement = connection.prepareStatement(update);
        preparedStatement.setInt(1, MB.getM());
        preparedStatement.setInt(2, MB.getB());
        preparedStatement.setLong(3, cal.getId());
        System.out.println("Atualizado M e B com os valores "+ MB.getM() + " " + MB.getB() + " no ID "+cal.getId());
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

   

}
