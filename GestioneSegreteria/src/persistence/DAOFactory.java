package persistence;

import persistence.dao.CorsoDao;
import persistence.dao.GruppoDao;
import persistence.dao.IndirizzoDao;
import persistence.dao.StudenteDao;

public abstract class DAOFactory {

	// --- List of supported DAO types ---

	
	/**
	 * Numeric constant '1' corresponds to explicit Hsqldb choice
	 */
	public static final int HSQLDB = 1;
	
	/**
	 * Numeric constant '2' corresponds to explicit Postgres choice
	 */
	public static final int POSTGRESQL = 2;
	
	
	// --- Actual factory method ---
	
	/**
	 * Depending on the input parameter
	 * this method returns one out of several possible 
	 * implementations of this factory spec 
	 */
	public static DAOFactory getDAOFactory(int whichFactory) {
		switch ( whichFactory ) {
		
		case HSQLDB:
			return null;//new HsqldbDAOFactory();
		case POSTGRESQL:
			return new PostgresDAOFactory();
		default:
			return null;
		}
	}
	
	
	
	// --- Factory specification: concrete factories implementing this spec must provide this methods! ---
	
	/**
	 * Method to obtain a DATA ACCESS OBJECT
	 * for the datatype 'Student'
	 */
	public abstract StudenteDao getStudentDAO();
	
	public abstract GruppoDao getGruppoDAO();
	
	public abstract IndirizzoDao getIndirizzoDAO();
	
	public abstract CorsoDao getCorsoDAO();

	public abstract persistence.UtilDao getUtilDAO();

}
