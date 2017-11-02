package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Corso;
import model.Gruppo;
import model.Studente;
import persistence.dao.CorsoDao;
import persistence.dao.StudenteDao;


public class CorsoDaoJDBC implements CorsoDao {
	private DataSource dataSource;

	public CorsoDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(Corso corso) {
		if ( (corso.getStudenti() == null) 
				|| corso.getStudenti().isEmpty()){
			throw new PersistenceException("Corso non memorizzato: un corso deve avere almeno uno studente");
		}
		Connection connection = this.dataSource.getConnection();
		try {
			Long id = IdBroker.getId(connection);
			corso.setCodice(id); 
			String insert = "insert into corso(codice, nome) values (?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, corso.getCodice());
			statement.setString(2, corso.getNome());

			//connection.setAutoCommit(false);
			//serve in caso gli studenti non siano stati salvati. Il DAO studente apre e chiude una transazione nuova.
			//connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);			
			statement.executeUpdate();
			// salviamo anche tutti gli studenti del gruppo in CASACATA
			this.updateStudenti(corso, connection);
			//connection.commit();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			} 
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}  

	private void updateStudenti(Corso corso, Connection connection) throws SQLException {
		StudenteDao studenteDao = new StudenteDaoJDBC(dataSource);
		for (Studente studente : corso.getStudenti()) {
			if (studenteDao.findByPrimaryKey(studente.getMatricola()) == null){
				studenteDao.save(studente);
			}
			String update = "update studente SET corso_codice = ? WHERE matricola = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, corso.getCodice());
			statement.setString(2, studente.getMatricola());
			int s=statement.executeUpdate();
		}
	}

	private void removeForeignKeyFromStudente(Corso corso, Connection connection) throws SQLException {
		for (Studente studente : corso.getStudenti()) {
			String update = "update studente SET corso_codice = NULL WHERE matricola = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, studente.getMatricola());
			statement.executeUpdate();
		}	
	}

	/* 
	 * versione con Join
	 */
	public Corso findByPrimaryKeyJoin(Long id) {
		Connection connection = this.dataSource.getConnection();
		Corso corso = null;
		try {
			PreparedStatement statement;
			String query = "select c.codice as c_codice, c.nome as c_nome, s.matricola as matricola, s.nome as nome, "
					+ "s.cognome as cognome, s.data_nascita as data_nascita "
					+ "from corso c left outer join studente s on c.codice=s.corso_codice "
					+ "where g.nome = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			boolean primaRiga = true;
			while (result.next()) {
				if (primaRiga) {
					corso = new Corso();
					corso.setCodice(result.getLong("c_codice"));				
					corso.setNome(result.getString("c_nome"));
					primaRiga = false;
				}
				if(result.getString("matricola")!=null){
					Studente studente = new Studente();
					studente.setMatricola(result.getString("matricola"));
					studente.setNome(result.getString("nome"));
					studente.setCognome(result.getString("cognome"));
					long secs = result.getDate("data_nascita").getTime();
					studente.setDataNascita(new java.util.Date(secs));
					corso.addStudente(studente);
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}	
		return corso;
	}



	/* 
	 * versione con Lazy Load
	 */
	public Corso findByPrimaryKey(Long id) {
		Connection connection = this.dataSource.getConnection();
		Corso corso = null;
		try {
			PreparedStatement statement;
			String query = "select * from corso where codice = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				corso = new Corso();
				corso.setCodice(result.getLong("codice"));				
				corso.setNome(result.getString("nome"));
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}	
		return corso;
	}

	public List<Corso> findAll() {
		Connection connection = this.dataSource.getConnection();
		List<Corso> corsi = new ArrayList<>();
		try {			
			Corso corso;
			PreparedStatement statement;
			String query = "select * from corso";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				corso = findByPrimaryKeyJoin(result.getLong("codice"));
				corsi.add(corso);
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}	 finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return corsi;
	}

	public void update(Corso corso) {
		Connection connection = this.dataSource.getConnection();
		try {
			String update = "update corso SET nome = ? WHERE codice = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, corso.getNome());
			statement.setLong(2, corso.getCodice());

			//connection.setAutoCommit(false);
			//connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);			
			statement.executeUpdate();
			this.updateStudenti(corso, connection); // se abbiamo deciso di propagare gli update seguendo il riferimento
			//connection.commit();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			} 
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	public void delete(Corso corso) {
		Connection connection = this.dataSource.getConnection();
		try {
			String delete = "delete FROM corso WHERE codice = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, corso.getCodice());

			/* 
			 * rimuoviamo gli studenti dal gruppo (ma non dal database) 
			 * potevano esserci soluzioni diverse (ad esempio rimuovere tutti gli studenti dal database
			 * (ma in questo caso non avrebbe senso)
			 * La scelta dipende dalla semantica delle operazioni di dominio
			 * 
			 * */
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);			
			this.removeForeignKeyFromStudente(corso, connection);     			
			/* 
			 * ora rimuoviamo il gruppo
			 * 
			 * */
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}
}
