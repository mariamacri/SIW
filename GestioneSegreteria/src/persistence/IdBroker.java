package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


class IdBroker {

	// Standard SQL (queste stringhe andrebbero scritte in un file di configurazione
	// private static final String query = "SELECT NEXT VALUE FOR SEQ_ID AS id";
	// postgresql
	private static final String query = "SELECT nextval('sequenza_id') AS id";

	public static Long getId(Connection connection) {
		Long id = null;
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			result.next();
			id = result.getLong("id");
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		return id;
	}
}