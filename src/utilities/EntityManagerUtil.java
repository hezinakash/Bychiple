package utilities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerUtil 
{
	private static final EntityManagerFactory entityManagerFactory;
	
	static 
	{
	    try
	    {
	    	entityManagerFactory = Persistence.createEntityManagerFactory("Bychiple");
	    } catch (Throwable ex) {
	    	System.err.println("Initial SessionFactory creation failed." + ex);
	    	throw new ExceptionInInitializerError(ex);
	    }
	}

	public static EntityManager getEntityManager() 
	{
		return entityManagerFactory.createEntityManager();
	}
}
