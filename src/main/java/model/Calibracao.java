package model;
import org.hashids.Hashids;

import java.util.Date;

public class Calibracao {
    private static final Hashids CRIADOR_DE_HASHID = new Hashids("G4RT3N4UT0M4C40", 8);
    private long id;
    private String pontos;
    private Date dataDeCriacao;
    private Date dataDeModificacao;
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPontos() {
        return pontos;
    }

    public void setPontos(String pontos) {
        this.pontos = pontos;
    }

    public Date getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(Date dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }

    public Date getDataDeModificacao() {
        return dataDeModificacao;
    }

    public void setDataDeModificacao(Date dataDeModificacao) {
        this.dataDeModificacao = dataDeModificacao;
    }

    public String getHashid() {
        return CRIADOR_DE_HASHID.encode(this.id);
    }

    public void setHashid(String hashid) {
        this.id = CRIADOR_DE_HASHID.decode(hashid)[0];
    }

    @Override
    public String toString() {
        return pontos == "null" ? "[]" : this.pontos;
    }
}
