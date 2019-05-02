
public class Usuario {
	
	private int id_usuario;
	private String nome;
	private String senha;
	private String login;
	
	
	public Usuario(int id_usuario, String nome, String senha, String login) {
		super();
		this.id_usuario = id_usuario;
		this.nome = nome;
		this.senha = senha;
		this.login = login;
	}
	
	
	public int getId_usuario() {
		return id_usuario;
	}
	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	
}
