package model;

public class Indirizzo {
	private Long codice;
	private String nome;
	public Long getCodice() {
		return codice;
	}
	public void setCodice(Long codice) {
		this.codice = codice;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String toString() {
		return "Indirizzo[" + this.getCodice() + ", " + this.getNome()+"]"; 		
	}
}
