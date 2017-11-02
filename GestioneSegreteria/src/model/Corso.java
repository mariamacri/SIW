package model;

import java.util.HashSet;
import java.util.Set;

public class Corso {
	private Long codice;
	private String nome;
	private Set<Studente> studenti;
	
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
	
	public Set<Studente> getStudenti() {
		return studenti;
	}
	public void setStudenti(Set<Studente> studenti) {
		this.studenti = studenti;
	}
	
	public void addStudente(Studente studente){
		if (this.studenti == null){
			this.studenti = new HashSet<Studente>();
		}
		this.studenti.add(studente);
	}
}
