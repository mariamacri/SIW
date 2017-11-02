package persistence;


import java.util.Calendar;
import java.util.Date;

import model.Corso;
import model.Gruppo;
import model.Indirizzo;
import model.Studente;
import persistence.dao.CorsoDao;
import persistence.dao.GruppoDao;
import persistence.dao.IndirizzoDao;
import persistence.dao.StudenteDao;

public class MainJDBC {
//ESERCIZIO (richiede Java8	
//	install Postgres https://www.postgresql.org/download/
//  scegliere utente: postgres -- password: postgres
//	
//	Lanciare PGADIMN oppure psql 
//	create database test;
//
//
//
//	- Vedere MainJDBC File.
//	- Testare i Dao Studente e Gruppo.
//	- Aggiungere l'entita' INDIRIZZO(codice, nome) per lo studente 
	//(uno studente ha un solo indirizzo)
//	- Aggiungere l'entita' CORSO(codice, nome), molti a molti con Studente.
	
	public static void main(String args[]) {
		Calendar cal = Calendar.getInstance();
		cal.set(1995, Calendar.MARCH, 21); // // 21 marzo 1995
		Date date1 = cal.getTime();
		cal.set(1996, Calendar.APRIL, 12); // 12 aprile 1996
		Date date2 = cal.getTime();
		cal.set(1998, Calendar.OCTOBER, 1);  // 1 ottobre 1998
		Date date3 = cal.getTime();

		DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.POSTGRESQL);
		StudenteDao studenteDao = factory.getStudentDAO();
		GruppoDao gruppoDao = factory.getGruppoDAO();
		IndirizzoDao indirizzoDao = factory.getIndirizzoDAO();
		CorsoDao corsoDao = factory.getCorsoDAO();
		UtilDao util = factory.getUtilDAO();
		util.dropDatabase();
		
		util.createDatabase();

		//crea studenti
		//crea gruppo
		//find studenti
		//find gruppo
		//delete gruppo/studenti
		
		Studente studente1 = new Studente("00000001","Rossi","Mario",date1);
		
		Studente studente2 = new Studente();
		studente2.setCognome("Verdi");
		studente2.setNome("Anna");
		studente2.setMatricola("00000002");
		studente2.setDataNascita(date2);

		Studente studente3 = new Studente();
		studente3.setCognome("Bianchi");
		studente3.setNome("Antonio");
		studente3.setMatricola("00000003");
		studente3.setDataNascita(date3);

		Gruppo gruppo1 = new Gruppo();
		//l'id del gruppo e' gestito tramite la classe IDBroker
		gruppo1.setNome("Colori");
		gruppo1.addStudente(studente1);
		gruppo1.addStudente(studente2);
		
		Indirizzo indirizzo1 = new Indirizzo();
		indirizzo1.setNome("Telecomunicazioni");
		studente1.setIndirizzo(indirizzo1);
		studente2.setIndirizzo(indirizzo1);
		
		Indirizzo indirizzo2 = new Indirizzo();
		indirizzo2.setNome("Intelligenza Artificiale e Robotica");
		studente3.setIndirizzo(indirizzo2);
		
		Corso corso1 = new Corso();
		corso1.setNome("Web Computing");
		corso1.addStudente(studente1);
		corso1.addStudente(studente2);
		corso1.addStudente(studente3);
		
		Corso corso2 = new Corso();
		corso2.setNome("Ingegneria del Software");
		corso2.addStudente(studente1);		
		corso2.addStudente(studente3);

		//CREATE
		indirizzoDao.save(indirizzo1);
		indirizzoDao.save(indirizzo2);
		
		studenteDao.save(studente1);
		studenteDao.save(studente2);
		studenteDao.save(studente3);
		
		corsoDao.save(corso1);
		corsoDao.save(corso2);
		
		gruppoDao.save(gruppo1);
		
		//RETRIEVE
		System.out.println("Retrieve all gruppo");
		for(Gruppo g : gruppoDao.findAll()) {
			System.out.println(g.getNome());  // NB: non viene invocato il metodo getStudenti()
			System.out.println(".-.-.-.");
			g.addStudente(studente3);
			System.out.println(".-.-.-.");

		}

		System.out.println("Retrieve all gruppo: proxy all'opera");
		for(Gruppo g : gruppoDao.findAll()) {
			System.out.println(g);
		}

//		gruppo1.addStudente(studente3);
//		gruppoDao.update(gruppo1);
		
		System.out.println("Retrieve all gruppo: proxy all'opera");
		for(Gruppo g : gruppoDao.findAll()) {
			System.out.println(g);
		}
		
		System.out.println("Elenco studenti");
		gruppoDao.delete(gruppo1);			
		for(Studente s : studenteDao.findAll()) {
			System.out.println(s);
		}		
	}
}
