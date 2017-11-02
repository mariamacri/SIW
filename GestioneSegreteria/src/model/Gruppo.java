package model;

import java.util.HashSet;
import java.util.Set;

public class Gruppo {
	private Long id;
	private String nome;
	private Set<Studente> studenti;

	public Gruppo(){
		this.studenti = new HashSet<>();
	}
	
	public Gruppo(String nome){
		this.nome=nome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Set<Studente> getStudenti(){
		return this.studenti;
	}

	public void setStudenti(Set<Studente> studenti){
		this.studenti = studenti;
	}
	
	public void addStudente(Studente studente){
		this.getStudenti().add(studente);         // fondamentale usare .getStudenti() per il proxy!!!
	}
	
	public void removeStudente(Studente studente){
		this.getStudenti().remove(studente);     // fondamentale usare .getStudenti() per il proxy!!!
	}
	
	public String toString() {
		StringBuffer str = new StringBuffer("[");
		str.append(this.getId() + ", " + this.getNome());
		str.append(", {");
		for (Studente s : this.getStudenti()) {
			str.append(s.toString());
		}
		str.append("}\n");
		return str.toString();
	}
}