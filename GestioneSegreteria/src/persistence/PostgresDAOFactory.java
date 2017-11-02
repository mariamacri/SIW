package persistence;



import persistence.dao.CorsoDao;
import persistence.dao.GruppoDao;
import persistence.dao.IndirizzoDao;
import persistence.dao.StudenteDao;

class PostgresDAOFactory extends DAOFactory {

	
	
	private static  DataSource dataSource;
	

	// --------------------------------------------

	static {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			//questi vanno messi in file di configurazione!!!	
//			dataSource=new DataSource("jdbc:postgresql://52.39.164.176:5432/xx","xx","p@xx");
			dataSource=new DataSource("jdbc:postgresql://localhost:5432/Segreteria","postgres","postgres");
		} 
		catch (Exception e) {
			System.err.println("PostgresDAOFactory.class: failed to load MySQL JDBC driver\n"+e);
			e.printStackTrace();
		}
	}

	
	// --------------------------------------------
	
	@Override
	public StudenteDao getStudentDAO() {
		return new StudenteDaoJDBC(dataSource);
	}

	@Override
	public GruppoDao getGruppoDAO() {
		return new GruppoDaoJDBC(dataSource);
	}
	
	@Override
	public IndirizzoDao getIndirizzoDAO() {
		return new IndirizzoDaoJDBC(dataSource);
	}
	
	@Override
	public CorsoDao getCorsoDAO() {
		return new CorsoDaoJDBC(dataSource);
	}
	@Override
	public UtilDao getUtilDAO(){
		return new UtilDao(dataSource);
	}
	

}
