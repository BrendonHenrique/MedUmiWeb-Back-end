import java.sql.Connection;
import java.sql.SQLException;

public class UsuarioDAO {
	

    private Connection con = null;
    private BancoDeDados bd = null;
    
    

    public UsuarioDAO() {
        this.bd = new BancoDeDados();
        try {
            this.con = bd.criarConexao();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    

}
