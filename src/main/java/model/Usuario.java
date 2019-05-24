package model;

import java.util.Date;

import com.google.gson.Gson;

public class Usuario {
	
	private long idUsuario;
	private String nome;
	private String senha;
	private String login;
	private int usuarioAdmin;
	private Date dataDeCriacao;
	
	public Usuario() { 
	}
	
	
	
	public Date getdataDeCriacao() {
		return this.dataDeCriacao;
	}
	
	public Usuario setdataDeCriacao(Date novaData) {
		this.dataDeCriacao = novaData;
		return this;
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
		
    @Override
    public String toString() {

        Gson gson = new Gson();
        
    	return gson.toJson(this);
    }


}
