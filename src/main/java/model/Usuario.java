package model;

public class Usuario {
	
	private long id_fk_usuario;
	private String nome;
	private String senha;
	private String login;
	private boolean UsuarioAdmin;
	
	public Usuario(long id_usuario, String nome, String senha, String login, boolean UsuarioAdmin) {
		super();
		this.id_fk_usuario = id_usuario;
		this.nome = nome;
		this.senha = senha;
		this.login = login;
		this.UsuarioAdmin = UsuarioAdmin;
	}
	
	public boolean getUsuarioAdmin() {
		return this.UsuarioAdmin;
	}
	
	public void setUsuarioAdmin(boolean UsuarioAdmin) {
		this.UsuarioAdmin = UsuarioAdmin;
	}
	
	public long getId_fk_usuario() {
		return id_fk_usuario;
	}
	public void setId_fk_usuario(long id_usuario) {
		this.id_fk_usuario = id_usuario;
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
