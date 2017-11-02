package persistence.dao;

import java.util.List;
import model.Gruppo;

public interface GruppoDao {
	
	public void save(Gruppo gruppo);  // Create
	public Gruppo findByPrimaryKey(Long id);     // Retrieve
	public List<Gruppo> findAll();       
	public void update(Gruppo gruppo); //Update
	public void delete(Gruppo gruppo); //Delete
}
