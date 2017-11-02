package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.Indirizzo;
import model.Studente;
import persistence.dao.IndirizzoDao;
import persistence.dao.StudenteDao;


class StudenteDaoJDBC implements StudenteDao {
	private DataSource dataSource;

	public StudenteDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(Studente studente) {
		Connection connection = this.dataSource.getConnection();
		try {
			String insert = "insert into studente(matricola, nome, cognome, data_nascita, indirizzo_codice) values (?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setString(1, studente.getMatricola());
			statement.setString(2, studente.getNome());
			statement.setString(3, studente.getCognome());
			long secs = studente.getDataNascita().getTime();
			statement.setDate(4, new java.sql.Date(secs));
			statement.setLong(5, studente.getIndirizzo().getCodice());
			statement.executeUpdate();
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

	public Studente findByPrimaryKey(String matricola) {
		Connection connection = this.dataSource.getConnection();
		Studente studente = null;
		try {
			PreparedStatement statement;
			String query = "select * from studente where matricola = ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, matricola);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				studente = new Studente();
				studente.setMatricola(result.getString("matricola"));				
				studente.setNome(result.getString("nome"));
				studente.setCognome(result.getString("cognome"));
				long secs = result.getDate("data_nascita").getTime();
				studente.setDataNascita(new java.util.Date(secs));
				
				IndirizzoDao indirizzoDao = new IndirizzoDaoJDBC(dataSource);
				Indirizzo indirizzo = indirizzoDao.findByPrimaryKey(result.getLong("indirizzo_codice"));
				studente.setIndirizzo(indirizzo);
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
		return studente;
	}

	public List<Studente> findAll() {
		Connection connection = this.dataSource.getConnection();
		List<Studente> studenti = new LinkedList<>();
		try {
			Studente studente;
			PreparedStatement statement;
			String query = "select * from studente";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				studente = new Studente();
				studente.setMatricola(result.getString("matricola"));				
				studente.setNome(result.getString("nome"));
				studente.setCognome(result.getString("cognome"));
				long secs = result.getDate("data_nascita").getTime();
				studente.setDataNascita(new java.util.Date(secs));
				
				IndirizzoDao indirizzoDao = new IndirizzoDaoJDBC(dataSource);
				Indirizzo indirizzo = indirizzoDao.findByPrimaryKey(result.getLong("indirizzo_codice"));
				studente.setIndirizzo(indirizzo);
				
				studenti.add(studente);
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
		return studenti;
	}

	public void update(Studente studente) {
		Connection connection = this.dataSource.getConnection();
		try {
			String update = "update studente SET nome = ?, cognome = ?, data_nascita = ?, indirizzo_codice = ? WHERE matricola=?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, studente.getNome());
			statement.setString(2, studente.getCognome());
			long secs = studente.getDataNascita().getTime();
			statement.setDate(3, new java.sql.Date(secs));
			statement.setString(4, studente.getMatricola());
			statement.setLong(5, studente.getIndirizzo().getCodice());
			statement.executeUpdate();
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

	public void delete(Studente studente) {
		Connection connection = this.dataSource.getConnection();
		try {
			String delete = "delete FROM studente WHERE matricola = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setString(1, studente.getMatricola());
			statement.executeUpdate();
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
