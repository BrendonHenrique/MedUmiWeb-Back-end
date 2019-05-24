package model;

public class ConfiguracoesCalibracao {
	
	private String hash;
	private String medidor;
	private String produtoSelecionado;
	
	public ConfiguracoesCalibracao() {
		
	}
	
	public String getMedidor() {
		return medidor;
	}
	public void setMedidor(String medidor) {
		this.medidor = medidor;
	}
	public String getProdutoSelecionado() {
		return produtoSelecionado;
	}
	public void setProdutoSelecionado(String produtoSelecionado) {
		this.produtoSelecionado = produtoSelecionado;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
 
	
}
