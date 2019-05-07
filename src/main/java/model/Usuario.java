package model;

public class Usuario {
	
	private long id_fk_usuario;
	private String nome;
	private String senha;
	private String login;
	private boolean UsuarioAdmin;
	
	public Usuario() { 
	}
	
	public boolean getUsuarioAdmin() {
		return this.UsuarioAdmin;
	}
	
	public Usuario setUsuarioAdmin(boolean UsuarioAdmin) {
		this.UsuarioAdmin = UsuarioAdmin;
		return this;
	}
	
	public long getId_fk_usuario() {
		return id_fk_usuario;
	}
	public Usuario setId_fk_usuario(long id_usuario) {
		this.id_fk_usuario = id_usuario;
		return this;
	}
	public String getNome() {
		return nome;
	}
	public Usuario setNome(String nome) {
		this.nome = nome;
		return this;
	}
	public String getSenha() {
		return senha;
	}
	public Usuario setSenha(String senha) {
		this.senha = senha;
		return this;
	}
	public String getLogin() {
		return login;
	}
	public Usuario setLogin(String login) {
		this.login = login;
		return this;
	}
	
	
}
