package model;

public class Usuario {
	
	private long idUsuario;
	private String nome;
	private String senha;
	private String login;
	private int usuarioAdmin;
	
	public Usuario() { 
	}
	
	public int getUsuarioAdmin() {
		return this.usuarioAdmin;
	}
	
	public Usuario setUsuarioAdmin(int UsuarioAdmin) {
		this.usuarioAdmin = UsuarioAdmin;
		return this;
	}
	
	public long getidUsuario() {
		return idUsuario;
	}
	public Usuario setidUsuario(long idUsuario) {
		this.idUsuario = idUsuario;
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
