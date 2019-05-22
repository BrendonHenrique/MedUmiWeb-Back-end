package model;
import org.hashids.Hashids;

import java.util.Date;

public class Calibracao {
    private static final Hashids CRIADOR_DE_HASHID = new Hashids("G4RT3N4UT0M4C40", 8);
    private long id;
    private String pontos;
    private Date dataDeCriacao;
    private Date dataDeModificacao;
    private int desabilitado;
	private float M;
	private float B;
	
    

	public float getB() {
		return this.B;
	}
	
	public Calibracao setB(float  B) {
		this.B = B;
		return this;
	}

	public float getM() {
		return this.M;
	}
	
	public Calibracao setM(float  M) {
		this.M = M;
		return this;
	}
	
    public int getDesabilitado() {
    	return this.desabilitado;
    }
    
    public void setDesabilitado(int desabilitado) {
    	this.desabilitado =  desabilitado;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPontos() {
        return pontos;
    }

    public Calibracao setPontos(String pontos) {
        this.pontos = pontos;
        return this;
    }

    public Date getDataDeCriacao() {
        return dataDeCriacao;
    }

    public Calibracao setDataDeCriacao(Date dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
        return this;
    }

    public Date getDataDeModificacao() {
        return dataDeModificacao;
    }

    public Calibracao setDataDeModificacao(Date dataDeModificacao) {
        this.dataDeModificacao = dataDeModificacao;
        return this;
    }

    public String getHashid() {
        return CRIADOR_DE_HASHID.encode(this.id);
    }

    public Calibracao setHashid(String hashid) {
        this.id = CRIADOR_DE_HASHID.decode(hashid)[0];
        return this;
    }
    
    public long parseHash(String hashid) {
    	return CRIADOR_DE_HASHID.decode(hashid)[0];
    }

    @Override
    public String toString() {
        return pontos == "null" ? "[]" : this.pontos;
    }

}
