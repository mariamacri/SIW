package persistence;

import model.Studente;
import persistence.dao.GruppoDao;
import persistence.dao.IndirizzoDao;
import persistence.dao.StudenteDao;
import model.Gruppo;
import model.Indirizzo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


class GruppoDaoJDBC implements GruppoDao {
	private DataSource dataSource;

	public GruppoDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(Gruppo gruppo) {
		Connection connection = this.dataSource.getConnection();
		try {
			Long id = IdBroker.getId(connection);
			gruppo.setId(id); 			
			String insert = "insert into gruppo(id, nome) values (?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, gruppo.getId());
			statement.setString(2, gruppo.getNome());

			//connection.setAutoCommit(false);
			//serve in caso gli studenti non siano stati salvati. Il DAO studente apre e chiude una transazione nuova.
			//connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);			
			statement.executeUpdate();
			// salviamo anche tutti gli studenti del gruppo in CASACATA
			this.updateStudenti(gruppo, connection);
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

	private void updateStudenti(Gruppo gruppo, Connection connection) throws SQLException {
		StudenteDao studenteDao = new StudenteDaoJDBC(dataSource);
		for (Studente studente : gruppo.getStudenti()) {
			if (studenteDao.findByPrimaryKey(studente.getMatricola()) == null){
				studenteDao.save(studente);
			}
			String update = "update studente SET gruppo_id = ? WHERE matricola = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, gruppo.getId());
			statement.setString(2, studente.getMatricola());
			int s=statement.executeUpdate();
			
		}
	}

	private void removeForeignKeyFromStudente(Gruppo gruppo, Connection connection) throws SQLException {
		for (Studente studente : gruppo.getStudenti()) {
			String update = "update studente SET gruppo_id = NULL WHERE matricola = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, studente.getMatricola());
			statement.executeUpdate();
		}	
	}

	/* 
	 * versione con Join
	 */
	public Gruppo findByPrimaryKeyJoin(Long id) {
		Connection connection = this.dataSource.getConnection();
		Gruppo gruppo = null;
		try {
			PreparedStatement statement;
			String query = "select g.id as g_id, g.nome as g_nome, s.matricola as matricola, s.nome as nome, "
					+ "s.cognome as cognome, s.data_nascita as data_nascita, s.indirizzo_codice as indirizzo_codice "
					+ "from gruppo g left outer join studente s on g.id=s.gruppo_id "
					+ "where g.nome = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			boolean primaRiga = true;
			while (result.next()) {
				if (primaRiga) {
					gruppo = new Gruppo();
					gruppo.setId(result.getLong("g_id"));				
					gruppo.setNome(result.getString("g_nome"));
					primaRiga = false;
				}
				if(result.getString("matricola")!=null){
					Studente studente = new Studente();
					studente.setMatricola(result.getString("matricola"));
					studente.setNome(result.getString("nome"));
					studente.setCognome(result.getString("cognome"));
					long secs = result.getDate("data_nascita").getTime();
					studente.setDataNascita(new java.util.Date(secs));
					
					IndirizzoDao indirizzoDao = new IndirizzoDaoJDBC(dataSource);
					Indirizzo indirizzo = indirizzoDao.findByPrimaryKey(result.getLong("indirizzo_codice"));
					studente.setIndirizzo(indirizzo);
					
					gruppo.addStudente(studente);
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
		return gruppo;
	}



	/* 
	 * versione con Lazy Load
	 */
	public Gruppo findByPrimaryKey(Long id) {
		Connection connection = this.dataSource.getConnection();
		Gruppo gruppo = null;
		try {
			PreparedStatement statement;
			String query = "select * from gruppo where id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				gruppo = new GruppoProxy(dataSource);
				gruppo.setId(result.getLong("id"));				
				gruppo.setNome(result.getString("nome"));
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
		return gruppo;
	}

	public List<Gruppo> findAll() {
		Connection connection = this.dataSource.getConnection();
		List<Gruppo> gruppi = new ArrayList<>();
		try {
			Gruppo gruppo;
			PreparedStatement statement;
			String query = "select * from gruppo";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				gruppo = new GruppoProxy(dataSource);
				gruppo.setId(result.getLong("id"));				
				gruppo.setNome(result.getString("nome"));
				gruppi.add(gruppo);
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
		return gruppi;
	}

	public void update(Gruppo gruppo) {
		Connection connection = this.dataSource.getConnection();
		try {
			String update = "update gruppo SET nome = ? WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, gruppo.getNome());
			statement.setLong(2, gruppo.getId());

			//connection.setAutoCommit(false);
			//connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);			
			statement.executeUpdate();
			this.updateStudenti(gruppo, connection); // se abbiamo deciso di propagare gli update seguendo il riferimento
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

	public void delete(Gruppo gruppo) {
		Connection connection = this.dataSource.getConnection();
		try {
			String delete = "delete FROM gruppo WHERE id = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, gruppo.getId());

			/* 
			 * rimuoviamo gli studenti dal gruppo (ma non dal database) 
			 * potevano esserci soluzioni diverse (ad esempio rimuovere tutti gli studenti dal database
			 * (ma in questo caso non avrebbe senso)
			 * La scelta dipende dalla semantica delle operazioni di dominio
			 * 
			 * */
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);			
			this.removeForeignKeyFromStudente(gruppo, connection);     			
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
