package control;
import java.sql.*;
import java.util.LinkedList;

import model.BancoDeDados;
import model.Calibracao;
import model.CalibracaoMB;
import model.ConfiguracoesCalibracao;

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
		
        String selectHash = "SELECT id, pontos, data_de_criacao, data_de_modificacao, desabilitado , produto "
        		+ ", medidor , M , B FROM webcal where id = ?";

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
            
            cal.setProdutoSelecionado(rs.getString(6));
            cal.setMedidor(rs.getString(7));
            cal.setM(rs.getFloat(8));
        	cal.setB(rs.getFloat(9));
            
            

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
        preparedStatement.setFloat(1, MB.getM());
        preparedStatement.setFloat(2, MB.getB());
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
    
    
    public boolean deletarHistorico(Long userID) {
    	
    	String deleteUserHistory = "DELETE from webcal where id_fk_usuario = ?";
    	Connection connection;
    	boolean result = false;
    	
    	try {
			connection = bd.criarConexao();
			PreparedStatement preparedStatement = connection.prepareStatement(deleteUserHistory);
			preparedStatement.setLong(1, userID);		
			preparedStatement.executeUpdate();
			result = preparedStatement.getUpdateCount() > 0;
			preparedStatement.close();
            return result;
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e.getMessage());
		}
        
		
		return result;
    	
    }
    
	public LinkedList<Calibracao> getHistoricoDoUser(Long userID) {
		
		String selectAllFromUser = "SELECT * from webcal where id_fk_usuario = ?";
		Connection connection;
		
		try {
			connection = bd.criarConexao();
			PreparedStatement preparedStatement = connection.prepareStatement(selectAllFromUser);
			preparedStatement.setLong(1, userID);			
			ResultSet result = preparedStatement.executeQuery();
			LinkedList<Calibracao> historicoDoUsuario = new LinkedList<Calibracao>();
			
			while(result.next()) {
				
				Calibracao calibracao =  new Calibracao();
				
				calibracao.setId(result.getLong(1));
				
				calibracao
				.setPontos(result.getString(2))
				.setM(result.getFloat(3))
				.setB(result.getFloat(4))
				.setDataDeCriacao(result.getTimestamp(5))
				.setDataDeModificacao(result.getTimestamp(6))
				.setDesabilitado(result.getInt(8));

				calibracao.setProdutoSelecionado(result.getString(9));
				calibracao.setMedidor(result.getString(10));
				
						
				historicoDoUsuario.add(calibracao);
				
			}
            preparedStatement.close();
            
            return historicoDoUsuario;
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e.getMessage());
		}
        
		
		
	return null;
	}

	public boolean setConfiguracoes(Calibracao calibracao , ConfiguracoesCalibracao configuracoes) {
		
		try {
    		String updateConfiguracoes = "UPDATE webcal SET medidor = ? , produto = ?  WHERE id = ? ";
            Connection connection = bd.criarConexao();
            PreparedStatement preparedStatement = connection.prepareStatement(updateConfiguracoes);
            
            preparedStatement.setString(1, configuracoes.getMedidor());
            preparedStatement.setString(2, configuracoes.getProdutoSelecionado());
            preparedStatement.setLong(3, calibracao.getId());
            
            preparedStatement.executeUpdate();
            
            boolean atualizouAlgo = preparedStatement.getUpdateCount() > 0;
        	preparedStatement.close();
            bd.fecharConexao(connection);
            
            System.out.println("Id "+calibracao.getId());
            System.out.println("medidor "+configuracoes.getMedidor());
            System.out.println("produto "+configuracoes.getProdutoSelecionado());
            System.out.println(atualizouAlgo);
            
            if(atualizouAlgo) {
            	return true;
            }else {
        		return false;
            }
            
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    		return false;
    	}
    	
		
	}
       

}
